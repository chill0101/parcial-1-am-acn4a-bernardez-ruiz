package com.example.react_io.services;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.react_io.models.GameScore;
import com.example.react_io.models.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para manejar datos de juego en Firestore. Guarda los scores de cada juego y obtiene los datos de leaderboard.
 * TODO: Remover los logs antes de entregar D:
 */
public class GameDataService {
    private static final String TAG = "GameDataService";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public GameDataService() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public interface GameDataCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface ScoresCallback {
        void onSuccess(List<GameScore> scores);
        void onFailure(String error);
    }

    public void saveGameScore(long timeInMillis, int errors, String gameType, GameDataCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }
        Log.d(TAG, "UID al guardar: " + currentUser.getUid());
        Log.d(TAG, "[saveGameScore] UID: " + currentUser.getUid() + ", time: " + timeInMillis + ", errors: " + errors + ", gameType: " + gameType);


        // GET username
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "[saveGameScore] getUser SUCCESS?");
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        String username = user != null ? user.getUsername() : "Usuario";
                        Log.d(TAG, "[saveGameScore] Username: " + username);

                        GameScore gameScore = new GameScore(
                                currentUser.getUid(),
                                username,
                                timeInMillis,
                                errors,
                                gameType
                        );
                        Log.d(TAG, "[saveGameScore] Instancia de juegoscore armada");

                        // Guardar en collection
                        db.collection("gameScores")
                                .add(gameScore)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d(TAG, "Puntuación guardada: " + documentReference.getId());
                                    Log.d(TAG, "[saveGameScore] SCORE GUARDADO OK id=" + documentReference.getId());
                                    // Actualizar stats
                                    updateUserStats(currentUser.getUid(), gameScore.getScore());
                                    Log.d(TAG, "[saveGameScore] Estadísticas de usuario actualizadas");
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> { // Error handling
                                    Log.w(TAG, "Error guardando puntuación", e);
                                    Log.e(TAG, "[saveGameScore] FALLO al guardar score", e);
                                    callback.onFailure("Error al guardar puntuación");
                                });
                    } else {
                        Log.w(TAG, "[saveGameScore] El documento del usuario no existe"); // Error handling
                        callback.onFailure("Usuario no encontrado");
                    }

                });
    }

    private void updateUserStats(String userId, double currentScore) { // Si el score es mejor (es menor), actualizar bestScore del user
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setTotalGamesPlayed(user.getTotalGamesPlayed() + 1);
                            if (currentScore < user.getBestScore()) {
                                user.setBestScore(currentScore);
                            }

                            db.collection("users").document(userId).set(user);
                        }
                    }
                });
    }

    public void getUserScores(ScoresCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        db.collection("gameScores")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<GameScore> scores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        GameScore score = document.toObject(GameScore.class);
                        scores.add(score);
                    }
                    callback.onSuccess(scores);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error obteniendo puntuaciones del usuario", e);
                    callback.onFailure("Error al obtener puntuaciones");
                });
    }

    public void getGlobalLeaderboard(ScoresCallback callback) {
        db.collection("gameScores")
                .orderBy("timeInMillis", Query.Direction.ASCENDING)
                .orderBy("errors", Query.Direction.ASCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<GameScore> scores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        GameScore score = document.toObject(GameScore.class);
                        scores.add(score);
                    }
                    callback.onSuccess(scores);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error obteniendo leaderboard global", e);
                    callback.onFailure("Error al obtener leaderboard");
                });
    }

    public void getLeaderboardByGame(String gameType, ScoresCallback callback) {
        db.collection("gameScores")
                .whereEqualTo("gameType", gameType)
                .orderBy("timeInMillis", Query.Direction.ASCENDING)
                .orderBy("errors", Query.Direction.ASCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<GameScore> scores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        GameScore score = document.toObject(GameScore.class);
                        scores.add(score);
                    }
                    callback.onSuccess(scores);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error obteniendo leaderboard de " + gameType, e);
                    callback.onFailure("Error al obtener leaderboard de " + gameType);
                });
    }
}