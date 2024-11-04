/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Clase encargada de rastrear y registrar el final de cada actividad, posiblemente para estadísticas o historial de usuario.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Llamada al final de una actividad para guardar información relacionada.
 */


package com.example.freetime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.freetime.dao.UserProgressDao;
import com.example.freetime.entities.UserProgress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityEndTracker extends AppCompatActivity {

    private UserProgressDao userProgressDao;
    private int activityId;
    private int userId;
    private String date;
    private String activityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_tracker);

        userProgressDao = AppDatabase.getDatabase(this).userProgressDao();

        // Obtener datos de la actividad desde el Intent
        activityId = getIntent().getIntExtra("activityId", -1);
        userId = getIntent().getIntExtra("userId", -1);
        activityName = getIntent().getStringExtra("activityName");
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Configurar el texto de actividad
        TextView activityNameTextView = findViewById(R.id.activityNameTextView);
        activityNameTextView.setText(getString(R.string.end_feedback_activity_prompt, activityName));

        // Configurar botones
        Button btnEndActivity = findViewById(R.id.btnEndActivity);
        Button btnViewCalendar = findViewById(R.id.btnViewCalendar);

        btnEndActivity.setOnClickListener(v -> markActivityEnded());
        btnViewCalendar.setOnClickListener(v -> navigateToHome());
    }

    private void markActivityEnded() {
        String endTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime());

        new Thread(() -> {
            UserProgress progress = userProgressDao.getProgressByUserAndActivity(userId, activityId, date);
            if (progress != null) {
                progress.endTime = endTime;
                progress.status = getString(R.string.activity_status_finished);  // Referencia a strings.xml
                userProgressDao.updateProgress(progress);
            } else {
                // Manejo en caso de que no exista un registro de progreso
                progress = new UserProgress(userId, activityId, date, false, true, null, endTime, getString(R.string.activity_status_finished));
                userProgressDao.upsertProgress(progress);
            }

            // Navegar de regreso al HomeFragment después de marcar la actividad como finalizada
            runOnUiThread(this::navigateToHome);
        }).start();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigateTo", "HomeFragment");
        startActivity(intent);
        finish();  // Finaliza ActivityEndTracker para que el usuario no pueda volver atrás
    }
}
