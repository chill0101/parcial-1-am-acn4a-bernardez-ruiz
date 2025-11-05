package com.example.react_io;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {

    private Button startButton, tapButton;
    private ImageButton restartButton, homeButton;
    private TextView resultText;
    private Handler handler = new Handler();
    private long startTime;
    private boolean waiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_game);

        startButton = findViewById(R.id.startButton);
        tapButton = findViewById(R.id.tapButton);
        resultText = findViewById(R.id.resultText);
        restartButton = findViewById(R.id.restartButton);
        homeButton = findViewById(R.id.homeButton);

        tapButton.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.INVISIBLE);

        startButton.setOnClickListener(v -> startGame());
        homeButton.setOnClickListener(v -> finish());
        restartButton.setOnClickListener(v -> startGame());
        tapButton.setOnClickListener(v -> tapNow());
    }

    private void startGame() {
        resultText.setText("Espera...");
        startButton.setVisibility(View.INVISIBLE);
        tapButton.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.INVISIBLE);

        int delay = new Random().nextInt(3000) + 2000; // 2 a 5 seg
        waiting = true;

        handler.postDelayed(() -> {
            resultText.setText("¡TOCA YA!");
            tapButton.setVisibility(View.VISIBLE);
            startTime = System.currentTimeMillis();
            waiting = false;
        }, delay);
    }

    private void tapNow() {
        if (!waiting) {
            long reactionTime = System.currentTimeMillis() - startTime;
            // Abrimos resultsActivity con el tiempo registrado
            Intent i = new Intent(ReactionGameActivity.this, ResultsActivity.class);
            i.putExtra("game", "reaction");
            i.putExtra("errors", 0);
            i.putExtra("time", reactionTime);
            startActivity(i);
            finish();
        } else {
            // El usuario tocó demasiado pronto (esto ya no sucede)
            Intent i = new Intent(ReactionGameActivity.this, ResultsActivity.class);
            i.putExtra("game", "reaction");
            i.putExtra("errors", 1);
            i.putExtra("time", 0L); // 0 ms porque falló
            startActivity(i);
            finish();
        }
    }
}
