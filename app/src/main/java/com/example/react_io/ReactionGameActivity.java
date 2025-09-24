package com.example.react_io;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {

    private Button startButton, tapButton;
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

        tapButton.setVisibility(View.INVISIBLE);

        startButton.setOnClickListener(v -> startGame());
        tapButton.setOnClickListener(v -> tapNow());
    }

    private void startGame() {
        resultText.setText("Espera...");
        startButton.setVisibility(View.INVISIBLE);
        tapButton.setVisibility(View.INVISIBLE);

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
            resultText.setText("Tu tiempo: " + reactionTime + " ms");
            tapButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
        } else {
            resultText.setText("¡Muy temprano!");
        }
    }
}

// TODO: AGREGAR BOTON PA OLVER ATRAS, POR QUE SINO TENES QUE CERRAR LA APP MIRA QUE OPORTUNO JAJAJA
// TODO: CAMBIAR COLORES EN VEZ DEL BOTON NO ME GUSTA.
// TODO:  QUE GUARDE LAST TIME, POR QUE SINO MUCHO SENTIDO NO TIENE, ESTARIA BUENO PONER UN RAKING NO TIPO RPEFEDIFINIDOS PARA QUE TE DE UNA MEDALLA IDK
// TODO: NO INMOLARME

