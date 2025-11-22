package com.example.react_io;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton; // Importante: Usar ImageButton para los íconos
import android.widget.Toast; // Para mostrar mensajes temporales

import androidx.appcompat.app.AppCompatActivity;

public class GameMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);


        // Botones de la lista de juegos
        Button game1Button = findViewById(R.id.game1Button);
        Button game2Button = findViewById(R.id.game2Button);
        Button game3Button = findViewById(R.id.game3Button);
        Button game4Button = findViewById(R.id.game4Button);
        Button game5Button = findViewById(R.id.game5Button);
        Button game6Button = findViewById(R.id.game6Button);
        Button game7Button = findViewById(R.id.game7Button);




        // Botones de la barra de navegación inferior y superior
        ImageButton profileButton = findViewById(R.id.profileButton);
        ImageButton rankingButton = findViewById(R.id.rankingButton);
        ImageButton achievementsButton = findViewById(R.id.achievementsButton);
        ImageButton homeButton = findViewById(R.id.homeButton);

        // Listeners para los botones de la lista de juegos
        game1Button.setOnClickListener(v -> startActivity(new Intent(GameMenuActivity.this, ReactionGameActivity.class)));
        game2Button.setOnClickListener(v -> startActivity(new Intent(GameMenuActivity.this, SequenceGameActivity.class)));
        game3Button.setOnClickListener(v -> startActivity(new Intent(GameMenuActivity.this, TapTargetActivity.class)));
        game4Button.setOnClickListener(v -> startActivity(new Intent(GameMenuActivity.this, LightSequenceGameActivity.class)));
        game5Button.setOnClickListener(v -> startActivity(new Intent(GameMenuActivity.this, ColorChangeGameActivity.class)));
        game6Button.setOnClickListener(v -> startActivity(new Intent(GameMenuActivity.this, ShapeMatchingGameActivity.class)));
        game7Button.setOnClickListener(v -> startActivity(new Intent(GameMenuActivity.this, FlagGameActivity.class)));

        // Listeners para la barra de navegación
        profileButton.setOnClickListener(v -> {
            // TODO: Reemplazar con la Activity real de Estadísticas/Perfil
            // Por ahora, redirigimos a MyStatsActivity como ejemplo
            startActivity(new Intent(GameMenuActivity.this, MyStatsActivity.class));
        });

        rankingButton.setOnClickListener(v -> {
            // Redirige a la Activity de Leaderboard
            startActivity(new Intent(GameMenuActivity.this, LeaderboardActivity.class));
        });

        achievementsButton.setOnClickListener(v -> {
            // Redirige a la Activity de Logros
            startActivity(new Intent(GameMenuActivity.this, AchievementsActivity.class));
        });

        homeButton.setOnClickListener(v -> {
            // Redirige a la Activity principal
            startActivity(new Intent( GameMenuActivity.this, MainActivity.class));
        });
    }
}