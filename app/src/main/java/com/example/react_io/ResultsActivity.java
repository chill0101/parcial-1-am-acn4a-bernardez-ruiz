package com.example.react_io;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    private TextView resultText;
    private ImageButton restartButton, homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        resultText = findViewById(R.id.resultText);
        restartButton = findViewById(R.id.restartButton);
        homeButton = findViewById(R.id.homeButton);

        Intent intent = getIntent();
        String game = intent.getStringExtra("game");
        int errors = intent.getIntExtra("errors", 0);
        long timeMs = intent.getLongExtra("time", 0L);

        String title = (game != null) ? game.toUpperCase(Locale.getDefault()) : getString(R.string.results_default_title);
        double seconds = timeMs / 1000.0;
        String formatted = String.format(Locale.getDefault(),
                "%s\n\nErrores: %d\nTiempo: %.2f s",
                title,
                errors,
                seconds);

        resultText.setText(formatted);

        restartButton.setOnClickListener(v -> {
            Class<?> targetActivity = SequenceGameActivity.class; // default
            if ("reaction".equals(game)) {
                targetActivity = ReactionGameActivity.class;
            } else if ("sequence".equals(game)) {
                targetActivity = SequenceGameActivity.class;
            } else if ("taptarget".equals(game)) {
                targetActivity = TapTargetActivity.class;
            }

            startActivity(new Intent(ResultsActivity.this, targetActivity));
            finish();
        });

        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(ResultsActivity.this, GameMenuActivity.class));
            finish();
        });
    }
}
