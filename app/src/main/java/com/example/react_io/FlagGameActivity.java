package com.example.react_io;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.react_io.services.GameDataService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FlagGameActivity extends AppCompatActivity {

    private ImageView imgFlag;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;

    private final Random random = new Random();

    private List<Country> countries = new ArrayList<>();
    private Country currentCountry;

    private long startTime = 0L;
    private int errors = 0;

    private static final long ERROR_PENALTY_MS = 500L; // misma penalización que en otros
    private static final int TOTAL_ROUNDS = 7;         // 👈 queremos 7 rondas
    private int currentRound = 0;                      // empieza en 0 y vamos sumando

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_game);

        imgFlag = findViewById(R.id.imgFlag);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);

        btnOption1.setOnClickListener(v -> onAnswerSelected(btnOption1.getText().toString()));
        btnOption2.setOnClickListener(v -> onAnswerSelected(btnOption2.getText().toString()));
        btnOption3.setOnClickListener(v -> onAnswerSelected(btnOption3.getText().toString()));
        btnOption4.setOnClickListener(v -> onAnswerSelected(btnOption4.getText().toString()));

        fetchCountries();
    }

    // ==============================
    //   LÓGICA DEL JUEGO
    // ==============================

    private void fetchCountries() {
        new Thread(() -> {
            String urlString = "https://restcountries.com/v3.1/all?fields=name,flags,translations";

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    showToast("Error al obtener países (código " + responseCode + ")");
                    return;
                }

                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                String body = responseBuilder.toString();
                JSONArray array = new JSONArray(body);

                List<Country> result = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    // Nombre en español si existe, sino en inglés
                    String nameEs = null;
                    if (obj.has("translations")) {
                        JSONObject translations = obj.getJSONObject("translations");
                        if (translations.has("spa")) {
                            JSONObject spa = translations.getJSONObject("spa");
                            nameEs = spa.optString("common", null);
                        }
                    }

                    if (nameEs == null && obj.has("name")) {
                        JSONObject nameObj = obj.getJSONObject("name");
                        nameEs = nameObj.optString("common", "Desconocido");
                    }

                    if (!obj.has("flags")) continue;
                    JSONObject flags = obj.getJSONObject("flags");
                    String flagPng = flags.optString("png", null);
                    if (flagPng == null) continue;

                    result.add(new Country(nameEs, flagPng));
                }

                if (result.size() < 4) {
                    showToast("No hay suficientes países para jugar");
                    return;
                }

                countries = result;
                runOnUiThread(this::startGame);   // 👈 arrancamos el juego

            } catch (Exception e) {
                e.printStackTrace();
                showToast("Error al obtener datos");
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    // Arranca el juego completo (las 7 rondas)
    private void startGame() {
        errors = 0;
        currentRound = 1;
        startTime = System.currentTimeMillis();
        showRound();
    }

    // Muestra una ronda (una bandera + 4 opciones)
    private void showRound() {
        // Mezclamos países y tomamos 4 opciones
        Collections.shuffle(countries, random);
        List<Country> options = countries.subList(0, 4);

        // Elegimos una correcta
        currentCountry = options.get(random.nextInt(options.size()));

        // Cargar la bandera
        Glide.with(this)
                .load(currentCountry.flagUrl)
                .into(imgFlag);

        // Mezclar nombres para los botones
        List<String> names = new ArrayList<>();
        for (Country c : options) names.add(c.name);
        Collections.shuffle(names, random);

        btnOption1.setText(names.get(0));
        btnOption2.setText(names.get(1));
        btnOption3.setText(names.get(2));
        btnOption4.setText(names.get(3));

        // Podríamos mostrar un Toast con la ronda
        Toast.makeText(
                this,
                "Ronda " + currentRound + " de " + TOTAL_ROUNDS,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void onAnswerSelected(String selectedName) {
        if (currentCountry == null) return;

        if (selectedName.equals(currentCountry.name)) {
            // ✅ Correcta
            if (currentRound >= TOTAL_ROUNDS) {
                // Última ronda → fin del juego
                finishGame();
            } else {
                // Pasamos a la siguiente ronda
                currentRound++;
                showRound();
            }
        } else {
            // ❌ Incorrecta → sumamos error (penaliza en el tiempo final)
            errors++;
            Toast.makeText(this, "Incorrecto, probá de nuevo", Toast.LENGTH_SHORT).show();
        }
    }

    // ==============================
    //   IGUAL PATRÓN QUE SEQUENCE
    // ==============================

    private void finishGame() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        // Penalización por errores
        elapsedTime += errors * ERROR_PENALTY_MS;

        // 1) GUARDAR SCORE ANTES DE IR A RESULTADOS:
        gameFinished(startTime, errors, "FlagGame");

        // 2) NAVEGAR A RESULTADOS:
        Intent i = new Intent(FlagGameActivity.this, ResultsActivity.class);
        i.putExtra("game", "flags");  // ResultsActivity ya tiene el else if("flags")
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

    private void showToast(String msg) {
        runOnUiThread(() ->
                Toast.makeText(FlagGameActivity.this, msg, Toast.LENGTH_SHORT).show()
        );
    }

    private static class Country {
        String name;
        String flagUrl;

        Country(String name, String flagUrl) {
            this.name = name;
            this.flagUrl = flagUrl;
        }
    }
}
