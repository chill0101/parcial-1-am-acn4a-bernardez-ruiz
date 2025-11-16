package com.example.react_io;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.react_io.services.GameDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShapeMatchingGameActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private TextView resultText;
    private Button startButton;
    private ImageButton restartButton, homeButton;
    private ImageView targetShapeImageView, changingShapeImageView;
    private int currentRound = 0;
    private int totalErrors = 0;
    private List<Long> roundTimes = new ArrayList<>();
    private long matchTime = 0L;
    private boolean shapesMatched = false;
    private boolean tapProcessed = false;
    private final Handler handler = new Handler();
    private final Random random = new Random();
    private static final long EARLY_PRESS_PENALTY_MS = 2000L;
    private static final long TIMEOUT_MS = 2000L; // Tiempo para responder después de que las formas coinciden, si no se toca es un error
    private static final int TOTAL_ROUNDS = 3;
    private String currentTargetShape = "";
    private String currentChangingShape = "";

    private final int[] shapeDrawables = {R.drawable.ic_circle, R.drawable.ic_square, R.drawable.ic_triangle, R.drawable.ic_star, R.drawable.ic_hexagon, R.drawable.ic_pentagon};
    private final String[] shapeNames = {"circle", "square", "triangle", "star", "hexagon", "pentagon"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_matching_game);

        mainLayout = findViewById(R.id.mainLayout);
        resultText = findViewById(R.id.resultText);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        homeButton = findViewById(R.id.homeButton);
        targetShapeImageView = findViewById(R.id.targetShapeImageView);
        changingShapeImageView = findViewById(R.id.changingShapeImageView);

        startButton.setOnClickListener(v -> setupGame());
        mainLayout.setOnClickListener(v -> onScreenClicked());
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(ShapeMatchingGameActivity.this, GameMenuActivity.class));
            finish();
        });
    }

    private void setupGame() {
        startButton.setVisibility(View.INVISIBLE);
        resultText.setVisibility(View.INVISIBLE);
        targetShapeImageView.setVisibility(View.VISIBLE);
        changingShapeImageView.setVisibility(View.INVISIBLE);
        currentRound = 0;
        totalErrors = 0;
        roundTimes.clear();
        startNextRound();
    }

    private void startNextRound() {
        currentRound++;
        shapesMatched = false;
        tapProcessed = false;
        // Acá usamos random para elegir la forma target
        int targetIndex = random.nextInt(shapeNames.length);
        currentTargetShape = shapeNames[targetIndex];
        targetShapeImageView.setImageResource(shapeDrawables[targetIndex]);
        changingShapeImageView.setVisibility(View.VISIBLE);
        startChangingShapes();
    }

    private void startChangingShapes() {
        changeShape();
        handler.postDelayed(this::startChangingShapes, 500 + random.nextInt(1000)); //Cambio cada medio segundo a uno y medio, lo podemos mover si queremos
    }

    private void changeShape() {
        if (shapesMatched) return; // Si ya matchearon, no cambiar más
        int shapeIndex = random.nextInt(shapeNames.length); // y acá es para la forma que va cambiando
        currentChangingShape = shapeNames[shapeIndex];
        changingShapeImageView.setImageResource(shapeDrawables[shapeIndex]);
        if (currentChangingShape.equals(currentTargetShape)) {
            shapesMatched = true;
            matchTime = System.currentTimeMillis();
            handler.removeCallbacksAndMessages(null); // Esto nos sirve para detener el cambio de formas
            handler.postDelayed(this::onTimeout, TIMEOUT_MS);
        }
    }

    private void onScreenClicked() {
        if (tapProcessed) return;
        tapProcessed = true;
        handler.removeCallbacksAndMessages(null);

        if (!shapesMatched) {
            // tap temprano
            totalErrors++;
            roundTimes.add(EARLY_PRESS_PENALTY_MS);
        } else {
            // tap correcto
            long reactionTime = System.currentTimeMillis() - matchTime;
            roundTimes.add(reactionTime);
        }

        resetShapes();
        if (currentRound < TOTAL_ROUNDS) {
            handler.postDelayed(this::startNextRound, 1000);
        } else {
            finishGame();
        }
    }

    private void onTimeout() {
        if (!tapProcessed && shapesMatched) {
            // No tap dentro del tiempo
            totalErrors++;
            roundTimes.add(EARLY_PRESS_PENALTY_MS);
            tapProcessed = true;
            resetShapes();
            if (currentRound < TOTAL_ROUNDS) {
                handler.postDelayed(this::startNextRound, 1000);
            } else {
                finishGame();
            }
        }
    }

    private void resetShapes() {
        changingShapeImageView.setVisibility(View.INVISIBLE);
    }

    private void finishGame() {
        long totalTime = 0;
        for (long time : roundTimes) {
            totalTime += time;
        }
        long averageTime = totalTime / TOTAL_ROUNDS;

        gameFinished(averageTime, totalErrors, "ShapeMatchingGame");

        Intent i = new Intent(ShapeMatchingGameActivity.this, ResultsActivity.class);
        i.putExtra("game", "shapematching");
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
