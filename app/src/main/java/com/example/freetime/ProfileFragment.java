/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Fragmento que muestra el perfil del usuario y permite editar información.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Accesible desde el menú principal para ver o editar el perfil.
 */


package com.example.freetime;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.freetime.dao.UserDao;
import com.example.freetime.entities.User;

import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private Spinner languageSpinner;
    private Button applyLanguageButton, logoutButton;
    private TextView tvUserName, tvUserEmail;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        languageSpinner = view.findViewById(R.id.spinner_language);
        applyLanguageButton = view.findViewById(R.id.apply_language_button);
        logoutButton = view.findViewById(R.id.logout_button); // Asegúrate de que este ID esté en tu layout
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        userDao = AppDatabase.getDatabase(requireContext()).userDao();

        setupLanguageSpinner();
        applyLanguageButton.setOnClickListener(v -> changeLanguage());
        logoutButton.setOnClickListener(v -> confirmLogout()); // Escuchador del botón de logout

        loadUserProfile();

        return view;
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Obtener la posición de idioma guardada; predeterminado a 0 (Español)
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", requireContext().MODE_PRIVATE);
        int languagePosition = prefs.getInt("language_position", 0);  // 0 = Español, 1 = Inglés, 2 = Alemán
        languageSpinner.setSelection(languagePosition);
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", requireContext().MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                User user = userDao.getUserById(userId);
                if (user != null) {
                    requireActivity().runOnUiThread(() -> {
                        tvUserName.setText(user.name);
                        tvUserEmail.setText(user.email);
                    });
                }
            });
        }
    }

    private void changeLanguage() {
        int selectedPosition = languageSpinner.getSelectedItemPosition();
        String languageCode;

        // Asignamos códigos de idioma según la posición del spinner
        switch (selectedPosition) {
            case 0:
                languageCode = "es"; // Español
                break;
            case 1:
                languageCode = "en"; // Inglés
                break;
            case 2:
                languageCode = "de"; // Alemán
                break;
            default:
                languageCode = "es"; // Predeterminado a Español
                break;
        }

        // Guardamos la posición seleccionada en SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", requireContext().MODE_PRIVATE);
        prefs.edit().putString("language_code", languageCode).putInt("language_position", selectedPosition).apply();

        Toast.makeText(requireContext(), getString(R.string.language_change_message), Toast.LENGTH_SHORT).show();

        // Reiniciar la app para aplicar el cambio de idioma
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> logout())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void logout() {
        // Limpiamos el user_id de SharedPreferences para cerrar sesión
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", requireContext().MODE_PRIVATE);
        sharedPreferences.edit().remove("user_id").apply();

        // Navegar a la pantalla de inicio de sesión
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar la pila de actividades
        startActivity(intent);
    }
}
