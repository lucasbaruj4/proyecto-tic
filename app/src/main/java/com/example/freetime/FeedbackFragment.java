/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Fragmento que presenta un formulario de comentarios para los usuarios.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Incluido en actividades que recogen feedback de usuario.
 */

package com.example.freetime;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
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

public class FeedbackFragment extends Fragment {

    private CalendarView calendarView;
    private ActivityDao activityDao;
    private UserProgressDao userProgressDao;
    private int userId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();  // Executor para tareas en segundo plano
    private ScrollView plannedContainer;
    private ScrollView completedContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        userId = requireActivity().getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE).getInt("user_id", -1);
        activityDao = AppDatabase.getDatabase(requireContext()).activityDao();
        userProgressDao = AppDatabase.getDatabase(requireContext()).userProgressDao();

        // Referencias a los ScrollView para sincronizar desplazamiento
        plannedContainer = view.findViewById(R.id.plannedActivitiesContainer);
        completedContainer = view.findViewById(R.id.completedActivitiesContainer);

        setupScrollSync();

        // Cargar actividades automáticamente para la fecha actual al abrir el fragmento
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        loadActivitiesForDate(todayDate, view);

        // Configurar el listener para obtener la fecha seleccionada
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            loadActivitiesForDate(selectedDate, view);
        });

        // Botón para generar feedback
        view.findViewById(R.id.btnGenerateFeedback).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FinalFeedbackActivity.class);
            startActivity(intent);
        });

        return view;
    }

    // Configuración para sincronizar el desplazamiento de los ScrollView
    private void setupScrollSync() {
        plannedContainer.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                completedContainer.scrollTo(scrollX, scrollY));

        completedContainer.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                plannedContainer.scrollTo(scrollX, scrollY));
    }

    // Método para cargar actividades por fecha
    private void loadActivitiesForDate(String date, View view) {
        LinearLayout plannedActivitiesLayout = view.findViewById(R.id.plannedActivitiesLayout);
        LinearLayout completedActivitiesLayout = view.findViewById(R.id.completedActivitiesLayout);

        // Limpiar solo los elementos dinámicos, manteniendo los encabezados
        clearDynamicViews(plannedActivitiesLayout);
        clearDynamicViews(completedActivitiesLayout);

        executorService.execute(() -> {
            List<Activity> plannedActivities = activityDao.getActivitiesForDate(date, userId);
            List<UserProgress> completedActivities = userProgressDao.getDailyProgress(userId, date);

            requireActivity().runOnUiThread(() -> {
                for (Activity activity : plannedActivities) {
                    TextView textView = new TextView(requireContext());
                    textView.setText(String.format("%s\n%s - %s", activity.name, activity.startTime, activity.endTime));
                    textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_regular));
                    plannedActivitiesLayout.addView(textView);
                }

                for (UserProgress progress : completedActivities) {
                    TextView textView = new TextView(requireContext());
                    textView.setText(String.format("%s\n%s - %s", progress.status, progress.startTime, progress.endTime));
                    textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_regular));
                    completedActivitiesLayout.addView(textView);
                }
            });
        });
    }

    // Método para limpiar solo las vistas dinámicas, sin afectar los encabezados
    private void clearDynamicViews(LinearLayout layout) {
        int childCount = layout.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            View child = layout.getChildAt(i);
            if (child.getId() != R.id.plannedHeader && child.getId() != R.id.completedHeader) {
                layout.removeViewAt(i);
            }
        }
    }
}
