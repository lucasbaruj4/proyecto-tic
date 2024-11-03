package com.example.freetime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

public class FirstTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        TextView welcomeTitle = findViewById(R.id.tv_welcome_title);
        welcomeTitle.setText(Html.fromHtml(getString(R.string.welcome_title)));

        Button startButton = findViewById(R.id.button_start);

        // Configurar el botón para que navegue a la siguiente pantalla
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla principal o formulario inicial
                Intent intent = new Intent(FirstTimeActivity.this, RestQuestion.class); // Cambia MainActivity por la actividad deseada
                startActivity(intent);
                finish();
            }
        });
    }
}
