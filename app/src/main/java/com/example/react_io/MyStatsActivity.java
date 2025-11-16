package com.example.react_io;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.react_io.services.GameDataService;
import com.example.react_io.models.GameScore;
import com.example.react_io.adapters.ScoreAdapter;
import java.util.List;

public class MyStatsActivity extends AppCompatActivity {
    private TextView tvUserStats;
    private RecyclerView rvUserScores;
    private GameDataService gameDataService;
    private ScoreAdapter scoreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stats);

        tvUserStats = findViewById(R.id.tvUserStats);
        rvUserScores = findViewById(R.id.rvUserScores);

        gameDataService = new GameDataService();

        setupRecyclerView();
        loadUserScores();

    }

    private void setupRecyclerView() {
        rvUserScores.setLayoutManager(new LinearLayoutManager(this));
        scoreAdapter = new ScoreAdapter();
        rvUserScores.setAdapter(scoreAdapter);
    }

    private void loadUserScores() {

        Log.d(TAG, "Iniciando carga de scores...");
        tvUserStats.setText("Cargando estadísticas...");

        gameDataService.getUserScores(new GameDataService.ScoresCallback() {
            @Override
            public void onSuccess(List<GameScore> scores) {
                Log.d(TAG, "onSuccess: Scores obtenidos (" + scores.size() + ")");
                Toast.makeText(MyStatsActivity.this, "Scores cargados: " + scores.size(), Toast.LENGTH_SHORT).show();

                scoreAdapter.updateScores(scores);

                if (!scores.isEmpty()) {

                    GameScore bestScore = scores.get(0);
                    for (GameScore score : scores) {
                        if (score.getScore() < bestScore.getScore()) {
                            bestScore = score;
                        }
                    }

                    String stats = "Juegos jugados: " + scores.size() +
                            "\nMejor tiempo: " + formatTime(bestScore.getTimeInMillis()) +
                            "\nMenor cantidad de errores: " + getMinErrors(scores);
                    tvUserStats.setText(stats);
                }
            }

            @Override
            public void onFailure(String error) {
                tvUserStats.setText("Error cargando estadísticas: " + error);
            }
        });
    }

    private String formatTime(long timeInMillis) {
        long seconds = timeInMillis / 1000;
        return seconds + "." + (timeInMillis % 1000) + "s";
    }

    private int getMinErrors(List<GameScore> scores) {
        int minErrors = Integer.MAX_VALUE;
        for (GameScore score : scores) {
            if (score.getErrors() < minErrors) {
                minErrors = score.getErrors();
            }
        }
        return minErrors == Integer.MAX_VALUE ? 0 : minErrors;
    }
}