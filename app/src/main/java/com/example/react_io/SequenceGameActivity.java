package com.example.react_io;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.react_io.services.GameDataService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SequenceGameActivity extends AppCompatActivity {

    private GridLayout grid;
    private TextView resultText;
    private Button startButton;
    private ImageButton restartButton, homeButton;
    private int currentTarget = 1;
    private int errors = 0;
    private long startTime = 0L;
    private final Handler handler = new Handler();
    private static final long ERROR_PENALTY_MS = 2000L; // Se penaliza cada error con 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_game);

        grid = findViewById(R.id.gridNumbers);
        resultText = findViewById(R.id.resultText);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        homeButton = findViewById(R.id.homeButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGame();
            }
        });
    }

    private void setupGame() {
        // Ocultar botones y texto inicial cuando inicia el juego
        startButton.setVisibility(View.INVISIBLE);
        resultText.setVisibility(View.INVISIBLE);

        currentTarget = 1;
        errors = 0;
        startTime = System.currentTimeMillis();

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        grid.removeAllViews();

        for (int num : numbers) {
            final Button btn = (Button) getLayoutInflater().inflate(R.layout.item_number_button, grid, false);
            btn.setText(String.valueOf(num));
            btn.setTag(num);

            // Esto asegura que cada botón tenga su propia copia del drawable original, porque anteriormente compartían la misma instancia y eso causaba problemas
            Drawable safeOrig = DrawableCompat.wrap(
                    ContextCompat.getDrawable(this, R.drawable.button_bg)
            ).mutate();

            btn.setBackground(safeOrig);
            btn.setTag(R.id.gridNumbers, safeOrig);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable saved = (Drawable) v.getTag(R.id.gridNumbers);
                    onNumberClicked((Button) v, saved);
                }
            });

            grid.addView(btn);
        }
    }


    private void onNumberClicked(final Button btn, final Drawable safeOriginal) {
        Integer valueObj = (Integer) btn.getTag();
        int value = (valueObj != null) ? valueObj : -1;

        if (value == currentTarget) {
            // Tint verde correcto y deshabilitar
            btn.setEnabled(false);
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, android.R.color.holo_green_dark)
            ));

            currentTarget++;
            if (currentTarget > 9) {
                finishGame();
            }
        } else {
            // Tint rojo error y esperar un momento antes de restaurar
            errors++;
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, android.R.color.holo_red_dark)
            ));

            handler.postDelayed(() -> {
                btn.setBackgroundTintList(null); // Restaurar al original
                btn.setBackground(cloneDrawableOrFallback(safeOriginal));
            }, 500);
        }
    }

    // Tuvimos problemas al clonar drawables directamente, así que este metodo intenta usar ConstantState y si falla, simplemente muta el original
    private Drawable cloneDrawableOrFallback(Drawable d) {
        if (d == null) return null;
        try {
            Drawable.ConstantState cs = d.getConstantState();
            if (cs != null) {
                Drawable clone = cs.newDrawable().mutate();
                return DrawableCompat.wrap(clone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DrawableCompat.wrap(d).mutate();
    }

    private void finishGame() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        // Penalización por errores
        elapsedTime += errors * ERROR_PENALTY_MS;

        // 1) GUARDAR SCORE ANTES DE IR A RESULTADOS:
        gameFinished(startTime, errors, "SequenceGame");

        // 2) NAVEGAR A RESULTADOS:
        Intent i = new Intent(SequenceGameActivity.this, ResultsActivity.class);
        i.putExtra("game", "sequence");
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
