/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Pantalla de bienvenida que introduce a los usuarios a la aplicación.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Mostrada solo al inicio de la app para dar la bienvenida.
 */

package com.example.freetime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Referencias a los elementos de la UI
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        // Ajustar el texto de registrarse con HTML para el color verde
        tvRegister.setText(Html.fromHtml(getString(R.string.register_activity_title)));

        // Manejo de clic para "Iniciar Sesión"
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Manejo de clic para "Registrarse"
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


    }
    private void applySavedLanguage() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String languageCode = prefs.getString("language_code", "es"); // Default to Spanish
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
