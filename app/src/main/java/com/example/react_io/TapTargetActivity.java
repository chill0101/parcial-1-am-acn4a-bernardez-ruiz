package com.example.react_io;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.react_io.services.GameDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TapTargetActivity extends AppCompatActivity {
    //TODO: Comentar bien todo para la entrega
    private GridLayout grid;
    private TextView resultText;
    private Button startButton;
    private ImageButton restartButton, homeButton;
    private List<Button> buttons = new ArrayList<>();
    private Handler handler = new Handler();
    private Random random = new Random();
    private long startTime;
    private int errors = 0;
    private int targetsHit = 0;
    private int totalTargets = 5; // Total de objetivos a tocar
    private long delay = 2000L; // Delay inicial entre objetivos
    private boolean gameActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_target);

        grid = findViewById(R.id.gridNumbers);
        resultText = findViewById(R.id.resultText);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        homeButton = findViewById(R.id.homeButton);

        restartButton.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.INVISIBLE);

        startButton.setOnClickListener(v -> startGame());
        restartButton.setOnClickListener(v -> startGame());
        homeButton.setOnClickListener(v -> finish());

        setupGrid();
    }

    private void setupGrid() {
        grid.removeAllViews();
        buttons.clear();
        for (int i = 0; i < 9; i++) {
            Button btn = new Button(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 120; // Aumentar ancho para mejor visibilidad
            params.height = 120; // Aumentamos el alto también para lo mismo
            params.setMargins(16, 16, 16, 16); // Spacing
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.button_bg);
            btn.setTextColor(Color.WHITE);
            btn.setTextSize(18);
            btn.setOnClickListener(this::onButtonClicked);
            grid.addView(btn);
            buttons.add(btn);
        }
    }

    private void startGame() {
        resultText.setText("NO TE DUERMAS!");
        startButton.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.INVISIBLE);

        errors = 0;
        targetsHit = 0;
        delay = 2000L;
        gameActive = true;
        startTime = System.currentTimeMillis();

        showNextTarget();
    }

    private void showNextTarget() {
        if (!gameActive || targetsHit >= totalTargets) {
            finishGame();
            return;
        }

        // Resetear todos los botones
        for (Button btn : buttons) {
            btn.setBackgroundResource(R.drawable.button_bg);
            btn.setTag(null);
        }

        // Resaltar un botón aleatorio como objetivo
        Button target = buttons.get(random.nextInt(buttons.size()));
        target.setBackgroundColor(Color.parseColor("#DF2935"));
        target.setTag("target");

        // Bajar el delay para hacer el juego más desafiante e.e
        delay = Math.max(500L, delay - 50L);

        handler.postDelayed(this::showNextTarget, delay);
    }

    private void onButtonClicked(View v) {
        if (!gameActive) return;

        Button btn = (Button) v;
        if ("target".equals(btn.getTag())) {
            // Buen tap ah
            targetsHit++;
            btn.setTag(null); // Prevenir taps múltiples
            btn.setBackgroundColor(Color.parseColor("#4CAF50"));
            if (targetsHit >= totalTargets) {
                finishGame();
            } else {
                handler.postDelayed(() -> showNextTarget(), 200);
            }
        } else {
            // Mal tap
            errors++;
            btn.setBackgroundColor(Color.parseColor("#F44336"));
            handler.postDelayed(() -> {
                btn.setBackgroundResource(R.drawable.button_bg);
            }, 500);
        }
    }


    private void finishGame() {
        gameActive = false;
        handler.removeCallbacksAndMessages(null);
        long elapsedTime = System.currentTimeMillis() - startTime;

        // 1) GUARDAR SCORE ANTES DE IR A RESULTADOS:
        gameFinished(startTime, errors, "TapTarget");

        // 2) NAVEGAR A RESULTADOS:
        Intent i = new Intent(TapTargetActivity.this, ResultsActivity.class);
        i.putExtra("game", "taptarget");
        i.putExtra("errors", errors);
        i.putExtra("time", elapsedTime);
        startActivity(i);
        finish();
    }

    private void gameFinished(long startTime, int errors, String gameName) {
        long endTime = System.currentTimeMillis();
        long gameTimeInMillis = endTime - startTime;

        GameDataService gameDataService = new GameDataService();
        gameDataService.saveGameScore(
                gameTimeInMillis,
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
