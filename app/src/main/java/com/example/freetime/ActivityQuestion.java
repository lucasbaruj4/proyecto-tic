package com.example.freetime;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.freetime.dao.ActivityDao;
import com.example.freetime.entities.Activity;
import com.example.freetime.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityQuestion extends AppCompatActivity {

    private String activityName;
    private List<String> selectedDays = new ArrayList<>();
    private String startTime = "";
    private String endTime = "";
    private boolean isFixedActivity;

    private ActivityDao activityDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_question);

        activityDao = AppDatabase.getDatabase(this).activityDao();

        EditText editTextActivityName = findViewById(R.id.activityNameActivityQuestion);
        CheckBox checkBoxFixed = findViewById(R.id.inamovible_checkbox);
        Button saveButton = findViewById(R.id.save_activity);

        Button[] dayButtons = new Button[]{
                findViewById(R.id.lunesActivityQuestion),
                findViewById(R.id.martesActivityQuestion),
                findViewById(R.id.miercolesActivityQuestion),
                findViewById(R.id.juevesActivityQuestion),
                findViewById(R.id.viernesActivityQuestion),
                findViewById(R.id.sabadoActivityQuestion),
                findViewById(R.id.domingoActivityQuestion)
        };

        handleDaySelection(dayButtons);
        handleTimePickers();

        saveButton.setOnClickListener(view -> {
            activityName = editTextActivityName.getText().toString();
            isFixedActivity = checkBoxFixed.isChecked();
            saveActivityData();
            showSaveDialog();
        });
    }

    private void handleDaySelection(Button[] dayButtons) {
        String[] dayNames = {
                getString(R.string.summary_day_lunes),
                getString(R.string.summary_day_martes),
                getString(R.string.summary_day_miercoles),
                getString(R.string.summary_day_jueves),
                getString(R.string.summary_day_viernes),
                getString(R.string.summary_day_sabado),
                getString(R.string.summary_day_domingo)
        };

        for (int i = 0; i < dayButtons.length; i++) {
            final Button dayButton = dayButtons[i];
            final String dayName = dayNames[i];

            dayButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));

            dayButton.setOnClickListener(view -> {
                if (selectedDays.contains(dayName)) {
                    selectedDays.remove(dayName);
                    dayButton.setBackgroundColor(ContextCompat.getColor(ActivityQuestion.this, R.color.gray));
                } else {
                    selectedDays.add(dayName);
                    dayButton.setBackgroundColor(ContextCompat.getColor(ActivityQuestion.this, R.color.FreeTimeGreen));
                }
            });
        }
    }

    private void handleTimePickers() {
        TimePicker startTimePicker = findViewById(R.id.start_time_picker);
        TimePicker endTimePicker = findViewById(R.id.end_time_picker);

        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);

        startTimePicker.setOnTimeChangedListener((timePicker, hourOfDay, minute) ->
                startTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));

        endTimePicker.setOnTimeChangedListener((timePicker, hourOfDay, minute) ->
                endTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
    }

    private void saveActivityData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, getString(R.string.error_user_not_authenticated), Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que startTime y endTime tengan valores predeterminados (la hora actual)
        if (startTime == null || startTime.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            startTime = String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }

        if (endTime == null || endTime.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            endTime = String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        int weeksToGenerate = 4;

        if (selectedDays.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_at_least_one_day), Toast.LENGTH_SHORT).show();
            return;
        }

        for (String dayOfWeek : selectedDays) {
            int dayIndex = getDayOfWeekIndex(dayOfWeek);

            for (int week = 0; week < weeksToGenerate; week++) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, dayIndex);
                calendar.add(Calendar.WEEK_OF_YEAR, week);

                String date = dateFormat.format(calendar.getTime());

                Activity newActivity = new Activity(userId, activityName, selectedDays, startTime, endTime, isFixedActivity, date);

                new Thread(() -> {
                    activityDao.insertActivity(newActivity);
                    Log.d("ActivityQuestion", getString(R.string.activity_saved_log, newActivity.name, newActivity.date, selectedDays));
                }).start();
            }
        }
    }

    private int getDayOfWeekIndex(String dayName) {
        switch (dayName) {
            case "Lunes":
                return Calendar.MONDAY;
            case "Martes":
                return Calendar.TUESDAY;
            case "Miércoles":
                return Calendar.WEDNESDAY;
            case "Jueves":
                return Calendar.THURSDAY;
            case "Viernes":
                return Calendar.FRIDAY;
            case "Sábado":
                return Calendar.SATURDAY;
            case "Domingo":
                return Calendar.SUNDAY;
            default:
                return Calendar.MONDAY;
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmation_text))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) -> resetForm())
                .setNegativeButton(R.string.no, (dialog, id) -> goToNextScreen());
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void resetForm() {
        EditText editTextActivityName = findViewById(R.id.activityNameActivityQuestion);
        CheckBox checkBoxFixed = findViewById(R.id.inamovible_checkbox);

        editTextActivityName.setText("");
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

        checkBoxFixed.setChecked(false);
        TimePicker startTimePicker = findViewById(R.id.start_time_picker);
        TimePicker endTimePicker = findViewById(R.id.end_time_picker);
        startTimePicker.setHour(0);
        startTimePicker.setMinute(0);
        endTimePicker.setHour(0);
        endTimePicker.setMinute(0);

        startTime = "";
        endTime = "";
        isFixedActivity = false;
    }

    private void goToNextScreen() {
        Intent intent = new Intent(this, ResumeActivity.class);
        startActivity(intent);
    }
}
