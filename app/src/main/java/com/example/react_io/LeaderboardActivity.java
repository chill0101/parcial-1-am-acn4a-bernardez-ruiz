package com.example.react_io;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.react_io.adapters.LeaderboardAdapter;
import com.example.react_io.models.GameScore;
import com.example.react_io.services.GameDataService;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView rvLeaderboard;
    private AutoCompleteTextView spinnerGameType;
    private GameDataService gameDataService;
    private LeaderboardAdapter leaderboardAdapter;


    private String[] gameTypesForFilter = {"Juego de Reacción", "Secuencia Numérica", "Atrapar al objetivo"};
    // Nombres de colección en Firestore
    private String[] gameCollections = {"ReactionGame", "SequenceGame", "TapTarget"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        spinnerGameType = findViewById(R.id.spinnerGameType);


        setSupportActionBar(toolbar);


        gameDataService = new GameDataService();
        leaderboardAdapter = new LeaderboardAdapter();


        setupRecyclerView();
        setupGameTypeSpinner();

        // Cargar los datos del primer juego por defecto al iniciar para que no quede todo en blanco nomás
        if (gameCollections.length > 0) {
            loadGameLeaderboard(gameCollections[0]);

            spinnerGameType.setText(gameTypesForFilter[0], false);
        }
    }

    private void setupRecyclerView() {
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        rvLeaderboard.setAdapter(leaderboardAdapter);
    }

    private void setupGameTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                gameTypesForFilter
        );
        spinnerGameType.setAdapter(adapter);

        // Escuchar cambios de selección
        spinnerGameType.setOnItemClickListener((parent, view, position, id) -> {
            // Acá usamos el array de colecciones para obtener el nombre correcto para Firestore
            String selectedGameCollection = gameCollections[position];
            loadGameLeaderboard(selectedGameCollection);
        });
    }

    private void loadGameLeaderboard(String gameName) {
        gameDataService.getLeaderboardByGame(gameName, new GameDataService.ScoresCallback() {
            @Override
            public void onSuccess(List<GameScore> scores) {
                leaderboardAdapter.updateScores(scores);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LeaderboardActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
