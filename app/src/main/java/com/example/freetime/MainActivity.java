package com.example.freetime;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.freetime.dao.ActivityDao;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fabAddActivity;
    private ActivityDao activityDao;
    private NavController navController;
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set default language to Spanish if no language preference is set
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String languageCode = prefs.getString("language_code", "es"); // Default to Spanish ("es")
        applyLanguage(languageCode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the user is authenticated
        SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);

        // If user is not authenticated, open WelcomeActivity and exit MainActivity
        if (userId == -1) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        fabAddActivity = findViewById(R.id.fab_add_activity);
        fabAddActivity.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityQuestion.class);
            startActivity(intent);
        });

        // Configure NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            handleNavigationIntent(getIntent());

            activityDao = AppDatabase.getDatabase(this).activityDao();

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.homeFragment) {
                    fabAddActivity.setVisibility(View.VISIBLE);
                } else {
                    fabAddActivity.setVisibility(View.GONE);
                }
            });
        }

        setupActivityReminders();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void applyLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void setupActivityReminders() {
        WorkRequest reminderRequest = new PeriodicWorkRequest.Builder(ActivityReminderWorker.class, 24, TimeUnit.HOURS)
                .addTag(getString(R.string.work_request_tag))
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                getString(R.string.work_request_tag),
                ExistingPeriodicWorkPolicy.REPLACE,
                (PeriodicWorkRequest) reminderRequest
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNavigationIntent(intent);
    }

    private void handleNavigationIntent(Intent intent) {
        if (intent != null && getString(R.string.navigate_to_home_fragment).equals(intent.getStringExtra("navigateTo"))) {
            if (navController != null) {
                navController.navigate(R.id.homeFragment, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, true)
                        .build());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupActivityReminders();
            }
        }
    }
}
