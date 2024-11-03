package com.example.freetime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.freetime.dao.UserProgressDao;
import com.example.freetime.entities.UserProgress;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    private UserProgressDao userProgressDao;
    private int activityId;
    private int userId;
    private String date;
    private String activityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        userProgressDao = AppDatabase.getDatabase(this).userProgressDao();

        // Obtener datos de la actividad desde el Intent
        activityId = getIntent().getIntExtra("activityId", -1);
        userId = getIntent().getIntExtra("userId", -1);
        activityName = getIntent().getStringExtra("activityName");
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Configurar los TextViews
        TextView activityPromptTextView = findViewById(R.id.activityPromptTextView);
        if (activityName != null) {
            activityPromptTextView.setText(getString(R.string.feedback_activity_prompt, activityName));
        } else {
            activityPromptTextView.setText(getString(R.string.default_feedback_prompt));
        }

        // Configurar botones y acciones
        Button btnStartActivity = findViewById(R.id.btn_start_activity);
        Button btnSkipActivity = findViewById(R.id.btn_skip_activity);

        btnStartActivity.setOnClickListener(v -> markActivityStarted());
        btnSkipActivity.setOnClickListener(v -> markActivitySkipped());
    }

    private void markActivityStarted() {
        String startTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime());

        new Thread(() -> {
            UserProgress progress = userProgressDao.getProgressByUserAndActivity(userId, activityId, date);

            Log.d("FeedbackActivity", "markActivityStarted - userId: " + userId + ", activityId: " + activityId + ", date: " + date);

            if (progress != null) {
                // Actualiza el progreso existente
                progress.startTime = startTime;
                progress.status = getString(R.string.status_started);
                userProgressDao.updateProgress(progress);
            } else {
                // Crea un nuevo progreso si no existe
                progress = new UserProgress(userId, activityId, date, false, true, startTime, null, getString(R.string.status_started));
                Log.d("FeedbackActivity", "Inserting new progress - userId: " + userId + ", activityId: " + activityId);
                userProgressDao.upsertProgress(progress);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, R.string.activity_started_message, Toast.LENGTH_SHORT).show();
                // Navega a la pantalla de finalización con activityName incluido
                Intent intent = new Intent(FeedbackActivity.this, ActivityEndTracker.class);
                intent.putExtra("activityId", activityId);
                intent.putExtra("userId", userId);
                intent.putExtra("activityName", activityName);
                startActivity(intent);
                finish();
            });
        }).start();
    }

    private void markActivitySkipped() {
        new Thread(() -> {
            UserProgress progress = userProgressDao.getProgressByUserAndActivity(userId, activityId, date);

            if (progress != null) {
                // Si el progreso ya existe, actualizamos su estado a "NO_REALIZADA"
                progress.status = getString(R.string.status_skipped);
                userProgressDao.updateProgress(progress);
            } else {
                // Si no existe, creamos un nuevo registro de progreso con estado "NO_REALIZADA"
                progress = new UserProgress(userId, activityId, date, false, true, null, null, getString(R.string.status_skipped));
                userProgressDao.upsertProgress(progress);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, R.string.activity_skipped_message, Toast.LENGTH_SHORT).show();
                // Redirigir al HomeFragment en MainActivity
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                intent.putExtra("navigateTo", "HomeFragment");
                startActivity(intent);
            });
        }).start();
    }

}
