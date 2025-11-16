package com.example.react_io;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.react_io.services.GameDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorChangeGameActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private TextView resultText;
    private Button startButton;
    private ImageButton restartButton, homeButton;
    private int currentRound = 0;
    private int totalErrors = 0;
    private List<Long> roundTimes = new ArrayList<>();
    private long roundStartTime = 0L;
    private long colorChangeTime = 0L;
    private boolean colorChanged = false;
    private final Handler handler = new Handler();
    private final Random random = new Random();
    private static final long EARLY_PRESS_PENALTY_MS = 2000L;
    private static final int TOTAL_ROUNDS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_change_game);

        mainLayout = findViewById(R.id.mainLayout);
        resultText = findViewById(R.id.resultText);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        homeButton = findViewById(R.id.homeButton);

        startButton.setOnClickListener(v -> setupGame());

        mainLayout.setOnClickListener(v -> onScreenClicked());

        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(ColorChangeGameActivity.this, GameMenuActivity.class));
            finish();
        });
    }

    private void setupGame() {
        startButton.setVisibility(View.INVISIBLE);
        resultText.setVisibility(View.INVISIBLE);
        currentRound = 0;
        totalErrors = 0;
        roundTimes.clear();
        startNextRound();
    }

    private void startNextRound() {
        currentRound++;
        colorChanged = false;
        roundStartTime = System.currentTimeMillis();
        long randomDelay = 1000 + random.nextInt(8000); // 1-9 secs
        handler.postDelayed(this::changeColor, randomDelay);
    }

    private void changeColor() {
        colorChangeTime = System.currentTimeMillis();
        colorChanged = true;
        int randomColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        mainLayout.setBackgroundColor(randomColor);
    }
    private void onScreenClicked() {
        if (!colorChanged) {
            // Tap temprano
            totalErrors++;
            roundTimes.add(EARLY_PRESS_PENALTY_MS);
        } else {
            // Tap ok
            long reactionTime = System.currentTimeMillis() - colorChangeTime;
            roundTimes.add(reactionTime);
        }

        // Reset background
        mainLayout.setBackgroundResource(R.drawable.bg_gradient_darker);

        if (currentRound < TOTAL_ROUNDS) {
            handler.postDelayed(this::startNextRound, 1000); // Brief pause
        } else {
            finishGame();
        }
    }


    private void finishGame() {
        long totalTime = 0;
        for (long time : roundTimes) {
            totalTime += time;
        }
        long averageTime = totalTime / TOTAL_ROUNDS;

        gameFinished(averageTime, totalErrors, "ColorChangeGame");


        Intent i = new Intent(ColorChangeGameActivity.this, ResultsActivity.class);
        i.putExtra("game", "colorchange");
        i.putExtra("errors", totalErrors);
        i.putExtra("time", averageTime);
        startActivity(i);
        finish();
    }

    private void gameFinished(long averageTime, int errors, String gameName) {
        GameDataService gameDataService = new GameDataService();
        gameDataService.saveGameScore(
                averageTime,
                errors,
                gameName,
                new GameDataService.GameDataCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("GameDataService", "Puntuación guardada correctamente (" + gameName + ")");
                        Toast.makeText(getApplicationContext(), "Puntuación guardada!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(String error) {
                        Log.e("GameDataService", "Error al guardar puntuación: " + error);
                        Toast.makeText(getApplicationContext(), "Error al guardar: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
