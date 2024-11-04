/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Pantalla de registro de nuevos usuarios.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Pantalla accesible desde la pantalla de inicio de sesión.
 */

package com.example.freetime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.freetime.dao.UserDao;
import com.example.freetime.entities.User;

import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Spinner spinnerFeedbackDay;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar DAO
        userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();

        // Referencias a los campos
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonRegister = findViewById(R.id.buttonRegister);


        // Acción al presionar el botón de registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_complete_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(name, email, password, 8, 0, 17, 0);

        // Guardar usuario en la base de datos en un hilo de fondo
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.insertUser(newUser);
                //Log.d("RegisterActivity", "Usuario registrado: " + email);

                // Espera breve para asegurar sincronización en la inserción antes de la consulta
                Thread.sleep(100);

                // Autenticar automáticamente al usuario registrado
                User user = userDao.getUserByEmail(email);
                if (user != null) {
                    saveUserIdInSharedPreferences(user.id);

                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, getString(R.string.toast_register_successful), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, FirstTimeActivity.class);
                        startActivity(intent);
                        finish(); // Termina la actividad para que no se pueda volver atrás
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, getString(R.string.error_unauthenticated_user), Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                //Log.e("RegisterActivity", "Error al registrar usuario: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show());
            }
        });
    }


    // Método para guardar el user_id en SharedPreferences
    private void saveUserIdInSharedPreferences(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", userId);
        editor.apply();

        // Log para confirmar almacenamiento
        //Log.d("RegisterActivity", "user_id guardado en SharedPreferences: " + userId);
    }

}
