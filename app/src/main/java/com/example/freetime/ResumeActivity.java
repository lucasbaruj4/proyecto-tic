/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Permite a los usuarios retomar una actividad pausada.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Llamado para reanudar una actividad previamente pausada.
 */

package com.example.freetime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.example.freetime.entities.Activity;
import com.example.freetime.dao.ActivityDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResumeActivity extends AppCompatActivity {

    private LinearLayout morningActivities;
    private LinearLayout afternoonActivities;
    private LinearLayout nightActivities;
    private TextView tvCurrentDay;
    private Button buttonPreviousDay, buttonNextDay, buttonConfirm, buttonModify;

    private String[] daysOfWeek;
    private int currentDayIndex = 0;
    private ActivityDao activityDao;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        // Obtener el userId del usuario autenticado desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // Inicializar DAO de actividad
        activityDao = AppDatabase.getDatabase(this).activityDao();

        // Referencias a los contenedores de cada sección
        morningActivities = findViewById(R.id.ll_morning_activities);
        afternoonActivities = findViewById(R.id.ll_afternoon_activities);
        nightActivities = findViewById(R.id.ll_night_activities);

        // Referencias a los botones y TextView de día
        tvCurrentDay = findViewById(R.id.tv_summary_day);
        buttonPreviousDay = findViewById(R.id.button_previous_day);
        buttonNextDay = findViewById(R.id.button_next_day);
        buttonConfirm = findViewById(R.id.button_confirm_summary);
        buttonModify = findViewById(R.id.button_modify_activity);

        daysOfWeek = new String[]{
                getString(R.string.summary_day_lunes),
                getString(R.string.summary_day_martes),
                getString(R.string.summary_day_miercoles),
                getString(R.string.summary_day_jueves),
                getString(R.string.summary_day_viernes),
                getString(R.string.summary_day_sabado),
                getString(R.string.summary_day_domingo)
        };

        buttonPreviousDay.setOnClickListener(v -> changeDay(false));
        buttonNextDay.setOnClickListener(v -> changeDay(true));

        buttonModify.setOnClickListener(v -> {
            Intent intent = new Intent(ResumeActivity.this, ActivityQuestion.class);
            startActivity(intent);
        });

        buttonConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(ResumeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        loadActivitiesForDay(daysOfWeek[currentDayIndex]);
    }

    private void changeDay(boolean isNext) {
        if (isNext) {
            currentDayIndex = (currentDayIndex + 1) % daysOfWeek.length;
        } else {
            currentDayIndex = (currentDayIndex - 1 + daysOfWeek.length) % daysOfWeek.length;
        }

        tvCurrentDay.setText(daysOfWeek[currentDayIndex]);
        loadActivitiesForDay(daysOfWeek[currentDayIndex]);
    }

    private void loadActivitiesForDay(String day) {
        morningActivities.removeAllViews();
        afternoonActivities.removeAllViews();
        nightActivities.removeAllViews();

        Log.d("ResumeActivity", "Cargando actividades para el día: " + day + " y userId: " + userId);

        new Thread(() -> {
            // Consultar actividades según el día de la semana y el userId
            List<Activity> activitiesForDay = activityDao.getActivitiesForDay(day, userId);

            // Usar un HashSet para filtrar duplicados por nombre y hora
            Set<String> uniqueActivities = new HashSet<>();

            for (Activity activity : activitiesForDay) {
                // Concatenar el nombre y la hora para identificar la actividad como única
                String uniqueKey = activity.name + activity.startTime + activity.endTime;

                // Agregar solo actividades únicas al conjunto y luego mostrarlas
                if (!uniqueActivities.contains(uniqueKey)) {
                    uniqueActivities.add(uniqueKey);

                    // Enviar al hilo principal para actualizar la interfaz
                    runOnUiThread(() -> sortActivityByTime(activity.startTime, activity.endTime, activity.name));
                }
            }
        }).start();
    }

    private void sortActivityByTime(String startTime, String endTime, String activityName) {
        // Verificar si startTime o endTime están vacíos
        if (startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty()) {
            Log.d("ResumeActivity", "Actividad con hora vacía: " + activityName);
            return; // Ignorar esta actividad o manejar de otra manera
        }

        int startHour = getHourAsInt(startTime);

        if (startHour >= 6 && startHour < 12) {
            addActivityToSection(getString(R.string.activity_time_format, activityName, startTime, endTime), morningActivities);
        } else if (startHour >= 12 && startHour < 18) {
            addActivityToSection(getString(R.string.activity_time_format, activityName, startTime, endTime), afternoonActivities);
        } else if (startHour >= 18 && startHour <= 24) {
            addActivityToSection(getString(R.string.activity_time_format, activityName, startTime, endTime), nightActivities);
        } else {
            Log.d("ResumeActivity", getString(R.string.start_time_out_of_range, startTime));
        }
    }

    private void addActivityToSection(String activityText, LinearLayout section) {
        TextView activityTextView = new TextView(this);
        activityTextView.setText(activityText);
        activityTextView.setTextSize(16);

        try {
            Typeface poppinsTypeface = ResourcesCompat.getFont(this, R.font.poppins_bold);
            activityTextView.setTypeface(poppinsTypeface);
        } catch (Exception e) {
            Log.e("ResumeActivity", getString(R.string.font_load_error, e.getMessage()));
        }

        activityTextView.setTextColor(ContextCompat.getColor(this, R.color.black));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        activityTextView.setLayoutParams(params);

        section.addView(activityTextView);
    }

    private int getHourAsInt(String time) {
        try {
            String[] timeParts = time.split(":");
            return Integer.parseInt(timeParts[0]);
        } catch (Exception e) {
            Log.e("ResumeActivity", getString(R.string.time_parse_error, time));
            return 0;
        }
    }
}
