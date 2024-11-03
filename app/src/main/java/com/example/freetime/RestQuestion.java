package com.example.freetime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.freetime.entities.User;

import java.util.concurrent.Executors;

public class RestQuestion extends AppCompatActivity {

    private static final String TAG = "RestQuestion";
    private TimePicker timePickerStart, timePickerEnd;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_question);

        // Inicializar la base de datos
        db = AppDatabase.getDatabase(this);

        // Inicializar los TimePickers y el botón
        timePickerStart = findViewById(R.id.timePicker_start);
        timePickerEnd = findViewById(R.id.timePicker_end);
        Button btnConfirm = findViewById(R.id.btn_confirm);

        // Configurar TimePicker en formato 24 horas
        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);

        // Configurar el botón de confirmación
        btnConfirm.setOnClickListener(v -> {
            // Obtener las horas seleccionadas
            int startHour = timePickerStart.getHour();
            int startMinute = timePickerStart.getMinute();
            int endHour = timePickerEnd.getHour();
            int endMinute = timePickerEnd.getMinute();



            // Guardar estos valores en la base de datos para el usuario autenticado
            saveShutdownTime(startHour, startMinute, endHour, endMinute);

            // Navegar a la pantalla de ActivityQuestion
            goToActivityQuestion();
        });
    }

    // Método para guardar los horarios seleccionados en el usuario autenticado en la base de datos
    // saveShutdownTime en RestQuestion
    private void saveShutdownTime(int startHour, int startMinute, int endHour, int endMinute) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d(TAG, "Usuario autenticado con user_id: " + userId); // Log para verificar el user_id
        }

        // Ejecuta la actualización en un hilo separado
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = db.userDao().getUserById(userId);
            if (user != null) {
                user.startHour = startHour;
                user.startMinute = startMinute;
                user.endHour = endHour;
                user.endMinute = endMinute;

                db.userDao().updateUser(user);  // Usa updateUser para actualizar el usuario existente
                Log.d(TAG, "Horarios de apagado actualizados en la base de datos para user_id: " + userId);

                // Solo navega a la siguiente actividad después de que se actualicen los datos
                runOnUiThread(this::goToActivityQuestion);
            } else {
                Log.e(TAG, "Error: Usuario no encontrado en la base de datos con user_id: " + userId);
                runOnUiThread(() -> Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show());
            }
        });
    }


    // Método para navegar a la pantalla de ActivityQuestion
    private void goToActivityQuestion() {
        Intent intent = new Intent(RestQuestion.this, ActivityQuestion.class);
        startActivity(intent);  // Iniciar la actividad
    }
}
