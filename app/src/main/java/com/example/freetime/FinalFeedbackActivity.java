/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Pantalla final de comentarios que recopila opiniones del usuario antes de cerrar sesión.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Mostrada al finalizar el uso de la app.
 */

package com.example.freetime;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.example.freetime.dao.ActivityDao;
import com.example.freetime.dao.UserProgressDao;
import com.example.freetime.entities.Activity;
import com.example.freetime.entities.UserProgress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinalFeedbackActivity extends AppCompatActivity {

    private ActivityDao activityDao;
    private UserProgressDao userProgressDao;
    private int userId;
    private TextView alignmentPercentageTextView;
    private TextView alignmentRatingTextView;
    private LinearLayout deviationDetailsContainer;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_feedback);

        alignmentPercentageTextView = findViewById(R.id.alignment_percentage);
        alignmentRatingTextView = findViewById(R.id.alignment_rating);
        deviationDetailsContainer = findViewById(R.id.deviation_details_container);

        activityDao = AppDatabase.getDatabase(this).activityDao();
        userProgressDao = AppDatabase.getDatabase(this).userProgressDao();
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);

        calculateFeedback();
    }

    private void calculateFeedback() {
        executorService.execute(() -> {
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
            List<Activity> plannedActivities = activityDao.getActivitiesForDate(todayDate, userId);
            List<UserProgress> completedActivities = userProgressDao.getDailyProgress(userId, todayDate);

            int alignmentScore = calculateAlignmentScore(plannedActivities, completedActivities);
            String alignmentRating = getAlignmentRating(alignmentScore);

            runOnUiThread(() -> {
                alignmentPercentageTextView.setText(getString(R.string.alignment_percentage_placeholder, alignmentScore));
                alignmentRatingTextView.setText(alignmentRating);
                displayDeviationDetails(plannedActivities, completedActivities);
            });
        });
    }

    private int calculateAlignmentScore(List<Activity> planned, List<UserProgress> completed) {
        int totalActivities = planned.size();
        double alignedActivities = 0;

        for (Activity activity : planned) {
            for (UserProgress progress : completed) {
                if (progress.activityId == activity.activityId) {
                    int plannedStart = timeToMinutes(activity.startTime);
                    int actualStart = timeToMinutes(progress.startTime);

                    int deviation = Math.abs(plannedStart - actualStart);
                    if (deviation <= 5) {
                        alignedActivities += 1;
                    } else if (deviation <= 15) {
                        alignedActivities += 0.5;
                    }
                }
            }
        }

        return totalActivities > 0 ? (int) ((alignedActivities / totalActivities) * 100) : 0;
    }

    private String getAlignmentRating(int score) {
        if (score >= 90) {
            return getString(R.string.excellent_rating);
        } else if (score >= 60) {
            return getString(R.string.medium_rating);
        } else {
            return getString(R.string.poor_rating);
        }
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private void displayDeviationDetails(List<Activity> planned, List<UserProgress> completed) {
        deviationDetailsContainer.removeAllViews();

        for (Activity activity : planned) {
            TextView activityView = new TextView(this);
            activityView.setTextSize(16);
            activityView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_regular));
            activityView.setText(getString(R.string.activity_name, activity.name));

            TextView deviationView = new TextView(this);
            deviationView.setTextSize(14);
            deviationView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_regular));

            String deviationText = getString(R.string.not_completed);
            for (UserProgress progress : completed) {
                if (progress.activityId == activity.activityId) {
                    int plannedStart = timeToMinutes(activity.startTime);
                    int actualStart = timeToMinutes(progress.startTime);
                    int deviation = Math.abs(plannedStart - actualStart);

                    if (deviation <= 5) {
                        deviationText = getString(R.string.perfect);
                    } else if (deviation <= 15) {
                        deviationText = getString(R.string.small_deviation, deviation);
                    } else {
                        deviationText = getString(R.string.high_deviation, deviation);
                    }
                }
            }

            deviationView.setText(deviationText);

            deviationDetailsContainer.addView(activityView);
            deviationDetailsContainer.addView(deviationView);
        }
    }
}
