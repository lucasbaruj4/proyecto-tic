package com.example.freetime;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityQuestion extends AppCompatActivity {

    private String activityName;
    private List<String> selectedDays = new ArrayList<>();
    private String startTime = "";
    private String endTime = "";
    private boolean isFixedActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_question);

        // Referencias a los componentes
        EditText editTextActivityName = findViewById(R.id.activityNameActivityQuestion);
        CheckBox checkBoxFixed = findViewById(R.id.inamovible_checkbox);
        Button saveButton = findViewById(R.id.save_activity);



        // Referencias a los botones de días
        Button[] dayButtons = new Button[]{
                findViewById(R.id.lunesActivityQuestion),
                findViewById(R.id.martesActivityQuestion),
                findViewById(R.id.miercolesActivityQuestion),
                findViewById(R.id.juevesActivityQuestion),
                findViewById(R.id.viernesActivityQuestion),
                findViewById(R.id.sabadoActivityQuestion),
                findViewById(R.id.domingoActivityQuestion)
        };

        // Manejar selección de días
        handleDaySelection(dayButtons);

        // Manejar selección de horas
        handleTimePickers();

        // Acción del botón guardar
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Guardar nombre de la actividad
                activityName = editTextActivityName.getText().toString();

                // Guardar si es una actividad inamovible
                isFixedActivity = checkBoxFixed.isChecked();

                // Mostrar el diálogo de confirmación
                showSaveDialog();
            }
        });
    }

    // Función para manejar la selección de días
    private void handleDaySelection(Button[] dayButtons) {
        String[] dayNames = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

        for (int i = 0; i < dayButtons.length; i++) {
            final Button dayButton = dayButtons[i];
            final String dayName = dayNames[i];

            // Inicialmente, todos los botones en gris
            dayButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));

            dayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Cambiar el color entre gris y verde al hacer clic
                    if (selectedDays.contains(dayName)) {
                        // Si el día ya está seleccionado, quitarlo y cambiar a gris
                        selectedDays.remove(dayName);
                        dayButton.setBackgroundColor(ContextCompat.getColor(ActivityQuestion.this, R.color.gray));
                    } else {
                        // Si no está seleccionado, agregarlo y cambiar a verde
                        selectedDays.add(dayName);
                        dayButton.setBackgroundColor(ContextCompat.getColor(ActivityQuestion.this, R.color.FreeTimeGreen));
                    }
                }
            });
        }
    }

    // Función para manejar la selección de horas desde TimePickers
    private void handleTimePickers() {
        TimePicker startTimePicker = findViewById(R.id.start_time_picker);
        TimePicker endTimePicker = findViewById(R.id.end_time_picker);

        // Configurar TimePicker en formato 24 horas
        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);


        // Listener para obtener la hora de inicio
        startTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                startTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            }
        });

        // Listener para obtener la hora de fin
        endTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                endTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            }
        });
    }

    // Función para mostrar el diálogo al guardar
    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Quiere guardar otra actividad?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Limpiar el formulario para agregar otra actividad
                        resetForm();
                    }
                })
                .setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Ir a otra pantalla (todavía no creada)
                        goToNextScreen();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Función para limpiar el formulario
    private void resetForm() {
        EditText editTextActivityName = findViewById(R.id.activityNameActivityQuestion);
        CheckBox checkBoxFixed = findViewById(R.id.inamovible_checkbox);

        // Limpiar el nombre de la actividad
        editTextActivityName.setText("");

        // Deseleccionar todos los días
        selectedDays.clear();
        Button[] dayButtons = new Button[]{
                findViewById(R.id.lunesActivityQuestion),
                findViewById(R.id.martesActivityQuestion),
                findViewById(R.id.miercolesActivityQuestion),
                findViewById(R.id.juevesActivityQuestion),
                findViewById(R.id.viernesActivityQuestion),
                findViewById(R.id.sabadoActivityQuestion),
                findViewById(R.id.domingoActivityQuestion)
        };
        for (Button dayButton : dayButtons) {
            dayButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        }

        // Deseleccionar el checkbox
        checkBoxFixed.setChecked(false);

        // Reiniciar horas (opcional)
        TimePicker startTimePicker = findViewById(R.id.start_time_picker);
        TimePicker endTimePicker = findViewById(R.id.end_time_picker);
        startTimePicker.setHour(0);
        startTimePicker.setMinute(0);
        endTimePicker.setHour(0);
        endTimePicker.setMinute(0);

        // Reiniciar variables
        startTime = "";
        endTime = "";
        isFixedActivity = false;
    }

    // Función para ir a la siguiente pantalla (todavía no creada)
    private void goToNextScreen() {
        // Aquí puedes usar un Intent para ir a la siguiente actividad o pantalla
        //Intent intent = new Intent(this, ResumenActivity.class);
        //startActivity(intent);
    }
}
