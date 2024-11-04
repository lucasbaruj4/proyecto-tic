/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Clase que verifica la autenticación del usuario antes de permitir el acceso a ciertas funcionalidades.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Ejecutada al inicio para verificar si el usuario tiene una sesión activa.
 */

package com.example.freetime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AuthCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Verificar si el usuario está autenticado
        if (userId != -1) {
            // Usuario autenticado, ir a MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // Usuario no autenticado, ir a WelcomeActivity
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }

        // Cerrar esta actividad para que no se quede en el back stack
        finish();
    }
}
