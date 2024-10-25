/*
 *
 * Project: FreeTime
 * Author: Lucas Baruja
 * Date: 10/23/2024
 * Description: Esta clase maneja la selección del periodo de tiempo donde el usuario "se apaga" o deja de realizar actividades.
 *
 * Use: Se utiliza para obtener el horario en el que el usuario prefiere no realizar ninguna actividad. Estos datos se almacenarán para luego ser usados por el algoritmo de FreeTime.
 *
 */

package com.example.freetime;

import android.content.Intent;  // Importar para manejar la navegación
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RestQuestion extends AppCompatActivity {

    private TimePicker timePickerStart, timePickerEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_question);

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

            // Mostrar las horas seleccionadas con un mensaje (puedes quitar esto más adelante)
            String message = "Horario de apagado: " + startHour + ":" + startMinute + " - " + endHour + ":" + endMinute;
            Toast.makeText(RestQuestion.this, message, Toast.LENGTH_SHORT).show();

            // Guardar estos valores en una base de datos o en SharedPreferences (por hacer)
            saveShutdownTime(startHour, startMinute, endHour, endMinute);

            // Navegar a la pantalla de ActivityQuestion
            goToActivityQuestion();
        });
    }

    // Método para guardar los horarios seleccionados
    private void saveShutdownTime(int startHour, int startMinute, int endHour, int endMinute) {
        // Guardar los datos en SharedPreferences (o base de datos)
        getSharedPreferences("FreeTimePrefs", MODE_PRIVATE)
                .edit()
                .putInt("startHour", startHour)
                .putInt("startMinute", startMinute)
                .putInt("endHour", endHour)
                .putInt("endMinute", endMinute)
                .apply();
    }

    // Método para navegar a la pantalla de ActivityQuestion
    private void goToActivityQuestion() {
        // Crear un Intent para navegar a ActivityQuestion
        Intent intent = new Intent(RestQuestion.this, ActivityQuestion.class);
        startActivity(intent);  // Iniciar la actividad
    }
}
