/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Pantalla de inicio de sesión para que los usuarios accedan a su cuenta.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Pantalla inicial para autenticación de usuario.
 */


package com.example.freetime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.freetime.dao.UserDao;
import com.example.freetime.entities.User;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        loginButton = findViewById(R.id.loginButton);
        userDao = AppDatabase.getDatabase(this).userDao();

        loginButton.setOnClickListener(v -> authenticateUser());

        TextView registerLink = findViewById(R.id.registerLink);
        registerLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void authenticateUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByEmail(email);

            runOnUiThread(() -> {
                if (user != null && user.getPassword().equals(password)) {
                    // Almacenar el ID de usuario autenticado en SharedPreferences
                    saveUserIdInSharedPreferences(user.id);

                    // Navegar a la pantalla principal
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.error_invalid_credentials, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void saveUserIdInSharedPreferences(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", userId);
        editor.apply();
    }
}
