/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Fragmento que muestra la pantalla principal de la app con las actividades destacadas.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Mostrado después de iniciar sesión.
 */

package com.example.freetime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freetime.entities.Activity;
import com.example.freetime.entities.User;
import com.example.freetime.dao.ActivityDao;
import com.example.freetime.dao.UserDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewMorning, recyclerViewAfternoon, recyclerViewNight;
    private ActivityAdapter morningAdapter, afternoonAdapter, nightAdapter;
    private ActivityDao activityDao;
    private UserDao userDao;
    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView greetingTextView = view.findViewById(R.id.greetingTextView);

        // Obtener userDao para acceder a la base de datos
        userDao = AppDatabase.getDatabase(getContext()).userDao();


        // Configurar saludo personalizado
        setUserGreeting(greetingTextView);

        // Configuración del CalendarView
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        recyclerViewMorning = view.findViewById(R.id.recyclerViewMorningActivities);
        recyclerViewAfternoon = view.findViewById(R.id.recyclerViewAfternoonActivities);
        recyclerViewNight = view.findViewById(R.id.recyclerViewNightActivities);

        // Configuración de los RecyclerView
        recyclerViewMorning.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAfternoon.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNight.setLayoutManager(new LinearLayoutManager(getContext()));

        morningAdapter = new ActivityAdapter(new ArrayList<>());
        afternoonAdapter = new ActivityAdapter(new ArrayList<>());
        nightAdapter = new ActivityAdapter(new ArrayList<>());

        recyclerViewMorning.setAdapter(morningAdapter);
        recyclerViewAfternoon.setAdapter(afternoonAdapter);
        recyclerViewNight.setAdapter(nightAdapter);

        // Inicializar DAO para acceder a la base de datos
        activityDao = AppDatabase.getDatabase(getContext()).activityDao();

        // Fecha inicial (fecha actual)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());

        // Listener para la selección de fecha en el CalendarView
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            loadActivitiesForDate(selectedDate);
        });

        // Cargar actividades para la fecha actual al iniciar
        loadActivitiesForDate(selectedDate);

        return view;
    }

    private void setUserGreeting(TextView greetingTextView) {
        Context context = getContext();  // Capturamos el contexto en una variable

        if (context == null) {
            return;  // Si el contexto es null, salimos de la función para evitar errores
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);

            if (userId != -1) {
                User user = userDao.getUserById(userId);
                if (user != null && isAdded()) {  // Verifica si el fragmento está adjunto
                    requireActivity().runOnUiThread(() -> {
                        if (isAdded()) {  // Verificación adicional en el hilo principal
                            String greeting = getString(R.string.greeting_message, user.name);
                            greetingTextView.setText(greeting);
                        }
                    });
                }
            }
        });
    }



    // Método para cargar actividades en los RecyclerView según la fecha y el periodo del día
    private void loadActivitiesForDate(String date) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            // Si no se encuentra el userId en SharedPreferences, muestra un mensaje de error
            Toast.makeText(getContext(), getString(R.string.error_unauthenticated_user), Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            List<Activity> activities = activityDao.getActivitiesForDate(date, userId);

            List<Activity> morningActivities = new ArrayList<>();
            List<Activity> afternoonActivities = new ArrayList<>();
            List<Activity> nightActivities = new ArrayList<>();

            for (Activity activity : activities) {
                if (activity.startTime != null && !activity.startTime.isEmpty()) {
                    try {
                        int startHour = Integer.parseInt(activity.startTime.split(":")[0]);
                        if (startHour >= 6 && startHour < 12) {
                            morningActivities.add(activity);
                        } else if (startHour >= 12 && startHour < 18) {
                            afternoonActivities.add(activity);
                        } else {
                            nightActivities.add(activity);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Verifica si el fragmento sigue conectado a la actividad antes de llamar a runOnUiThread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    morningAdapter.setActivityList(morningActivities);
                    recyclerViewMorning.setVisibility(morningActivities.isEmpty() ? View.GONE : View.VISIBLE);

                    afternoonAdapter.setActivityList(afternoonActivities);
                    recyclerViewAfternoon.setVisibility(afternoonActivities.isEmpty() ? View.GONE : View.VISIBLE);

                    nightAdapter.setActivityList(nightActivities);
                    recyclerViewNight.setVisibility(nightActivities.isEmpty() ? View.GONE : View.VISIBLE);
                });
            }
        }).start();
    }

}
