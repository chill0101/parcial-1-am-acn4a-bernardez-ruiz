package com.example.react_io;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import com.example.react_io.services.GameDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LightSequenceGameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SEQUENCE_LENGTH = 8;
    private static final long SHOW_DELAY_MS = 500;
    private static final long BETWEEN_LIGHT_MS = 300;
    private static final String GAME_TYPE = "lightsequence";

    private Button btnStartSequence, btnReintentar;
    private AppCompatImageButton btnRojo, btnVerde, btnAzul, btnAmarillo;
    private TextView txtInstruccion, txtResult;

    private final List<Integer> gameSequence = new ArrayList<>();
    private final List<Integer> playerSequence = new ArrayList<>();

    private boolean showingSequence = false;
    private boolean userTurn = false;
    private long userStartTime = 0L;
    private int errors = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private static final int RED_ID = 1;
    private static final int GREEN_ID = 2;
    private static final int BLUE_ID = 3;
    private static final int YELLOW_ID = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sequence_game);

        bindViews();
        setListeners();
        setInitialState();

        Log.d("LightSequenceGame", "onCreate finished");
    }

    private void bindViews() {
        btnStartSequence = findViewById(R.id.startButton);
        btnReintentar = findViewById(R.id.btnReintentar);
        txtInstruccion = findViewById(R.id.txtInstruccion);
        txtResult = findViewById(R.id.txtResult);

        btnRojo = findViewById(R.id.btnRojo);
        btnVerde = findViewById(R.id.btnVerde);
        btnAzul = findViewById(R.id.btnAzul);
        btnAmarillo = findViewById(R.id.btnAmarillo);
    }

    private void setListeners() {
        if (btnStartSequence != null) btnStartSequence.setOnClickListener(v -> {
            Log.d("LightSequenceGame", "StartSequence clicked");
            startGame();
        });
        if (btnReintentar != null) btnReintentar.setOnClickListener(v -> restartGame());

        if (btnRojo != null) btnRojo.setOnClickListener(this);
        if (btnVerde != null) btnVerde.setOnClickListener(this);
        if (btnAzul != null) btnAzul.setOnClickListener(this);
        if (btnAmarillo != null) btnAmarillo.setOnClickListener(this);
    }

    private void setInitialState() {
        errors = 0;
        userTurn = false;
        showingSequence = false;
        gameSequence.clear();
        playerSequence.clear();
        if (btnReintentar != null) btnReintentar.setVisibility(View.GONE);
        if (btnStartSequence != null) btnStartSequence.setVisibility(View.VISIBLE);
        if (txtResult != null) txtResult.setText("");
        if (txtInstruccion != null)
            txtInstruccion.setText("Presiona 'Iniciar Secuencia' y repite la secuencia de luces");
        setButtonsEnabled(false);
    }

    private void restartGame() {
        setInitialState();
    }

    private void startGame() {
        if (btnStartSequence != null) btnStartSequence.setVisibility(View.GONE);
        if (txtResult != null) txtResult.setText("");
        if (txtInstruccion != null) txtInstruccion.setText("Observa la secuencia...");
        errors = 0;
        playerSequence.clear();
        gameSequence.clear();
        generateSequence();
        showingSequence = true;
        userTurn = false;
        setButtonsEnabled(false);
        playSequence(0);
    }

    private void generateSequence() {
        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
            gameSequence.add(random.nextInt(4) + 1);
        }
    }

    private void playSequence(int index) {
        if (index >= gameSequence.size()) {
            showingSequence = false;
            userTurn = true;
            if (txtInstruccion != null) txtInstruccion.setText("¡Tu turno! Reproduce la secuencia");
            playerSequence.clear();
            // Reseter los errores antes del turno del usuario porque la idea es que solo cuente los errores de esta ronda
            errors = 0;
            userStartTime = System.currentTimeMillis();
            setButtonsEnabled(true);
            return;
        }

        int code = gameSequence.get(index);
        AppCompatImageButton button = mapCodeToImageButton(code);
        int normalRes = mapCodeToNormalDrawable(code);
        int highlightRes = mapCodeToHighlightDrawable(code);

        highlightImageButton(button, normalRes, highlightRes, () -> {
            handler.postDelayed(() -> playSequence(index + 1), BETWEEN_LIGHT_MS);
        });
    }

    private void highlightImageButton(AppCompatImageButton button, int normalRes, int highlightRes, Runnable onRestored) {
        if (button == null) return;
        runOnUiThread(() -> button.setImageResource(highlightRes));
        handler.postDelayed(() -> {
            runOnUiThread(() -> button.setImageResource(normalRes));
            if (onRestored != null) onRestored.run();
        }, SHOW_DELAY_MS);
    }

    @Override
    public void onClick(View v) {
        if (!userTurn || showingSequence) return;

        int tappedCode = 0;
        if (v.getId() == R.id.btnRojo) tappedCode = RED_ID;
        else if (v.getId() == R.id.btnVerde) tappedCode = GREEN_ID;
        else if (v.getId() == R.id.btnAzul) tappedCode = BLUE_ID;
        else if (v.getId() == R.id.btnAmarillo) tappedCode = YELLOW_ID;

        if (tappedCode == 0) return;

        AppCompatImageButton b = (AppCompatImageButton) v;
        int normalRes = mapCodeToNormalDrawable(tappedCode);
        int highlightRes = mapCodeToHighlightDrawable(tappedCode);

        b.setImageResource(highlightRes);
        handler.postDelayed(() -> b.setImageResource(normalRes), 160);

        playerSequence.add(tappedCode);
        int currentIndex = playerSequence.size() - 1;

        if (!playerSequence.get(currentIndex).equals(gameSequence.get(currentIndex))) {
            errors++;
        }

        if (playerSequence.size() == gameSequence.size()) {
            gameFinished();
        }
    }


// Tenemos que evaluar estandarizar estos métodos de gamefinished pero como
    private void gameFinished() {

        userTurn = false;
        setButtonsEnabled(false);

        // Calcular el tiempo final del juego
        long elapsedMs = System.currentTimeMillis() - userStartTime;

        // Navegar diretamente a la pantalla de resultados
        Log.d(GAME_TYPE, "Juego finalizado. Navegando a resultados AHORA.");
        Intent i = new Intent(LightSequenceGameActivity.this, ResultsActivity.class);
        i.putExtra("game", GAME_TYPE);
        i.putExtra("errors", errors);
        i.putExtra("time", elapsedMs);
        startActivity(i);

        //Iniciar el guardado de datos en segundo plano -- lo hice medio distinto que en los otros juegos para ir probando, debemos estandarizarlo luego
        // En los demás le pasamos los parámetros desde el juego, acá los tomamos de las variables globales
        saveScoreInBackground(userStartTime, errors, GAME_TYPE);

        finish();
    }


    private void saveScoreInBackground(long startTime, int errors, String gameName) {
        long endTime = System.currentTimeMillis();
        long gameTimeInMillis = endTime - startTime;

        GameDataService gameDataService = new GameDataService();
        Log.d("GameDataService", "Iniciando guardado en segundo plano: tiempo=" + gameTimeInMillis + ", errores=" + errors);

        gameDataService.saveGameScore(
                gameTimeInMillis,
                errors,
                gameName,
                new GameDataService.GameDataCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("GameDataService", "Guardado en segundo plano exitoso para " + gameName);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("GameDataService", "Error en el guardado en segundo plano para " + gameName + ": " + error);
                    }
                }
        );
    }


    //AUXILIARES

    private AppCompatImageButton mapCodeToImageButton(int code) {
        switch (code) {
            case RED_ID: return btnRojo;
            case GREEN_ID: return btnVerde;
            case BLUE_ID: return btnAzul;
            case YELLOW_ID: return btnAmarillo;
            default: return null;
        }
    }

    private int mapCodeToNormalDrawable(int code) {
        switch (code) {
            case RED_ID: return R.drawable.circle_red;
            case GREEN_ID: return R.drawable.circle_green;
            case BLUE_ID: return R.drawable.circle_blue;
            case YELLOW_ID: return R.drawable.circle_yellow;
            default: return 0;
        }
    }

    private int mapCodeToHighlightDrawable(int code) {
        switch (code) {
            case RED_ID: return R.drawable.circle_red_highlight;
            case GREEN_ID: return R.drawable.circle_green_highlight;
            case BLUE_ID: return R.drawable.circle_blue_highlight;
            case YELLOW_ID: return R.drawable.circle_yellow_highlight;
            default: return 0;
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        float alpha = enabled ? 1f : 0.65f;
        if (btnRojo != null) { btnRojo.setEnabled(enabled); btnRojo.setAlpha(alpha); }
        if (btnVerde != null) { btnVerde.setEnabled(enabled); btnVerde.setAlpha(alpha); }
        if (btnAzul != null) { btnAzul.setEnabled(enabled); btnAzul.setAlpha(alpha); }
        if (btnAmarillo != null) { btnAmarillo.setEnabled(enabled); btnAmarillo.setAlpha(alpha); }
    }
}
