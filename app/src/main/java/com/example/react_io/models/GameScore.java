package com.example.react_io.models;

import com.google.firebase.Timestamp;

public class GameScore {
    private String userId;
    private String username;
    private long timeInMillis;
    private int errors;
    private String gameType;
    private Timestamp timestamp;

    public GameScore() {
        // Constructor vacío requerido por Firestore
    }

    public GameScore(String userId, String username, long timeInMillis, int errors, String gameType) {
        this.userId = userId;
        this.username = username;
        this.timeInMillis = timeInMillis;
        this.errors = errors;
        this.gameType = gameType;
        this.timestamp = Timestamp.now();
    }

    // Getters y Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getTimeInMillis() { return timeInMillis; }
    public void setTimeInMillis(long timeInMillis) { this.timeInMillis = timeInMillis; }

    public int getErrors() { return errors; }
    public void setErrors(int errors) { this.errors = errors; }

    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    // Método para calcular puntuación (menor tiempo + menos errores = mejor)
    public double getScore() {
        return timeInMillis + (errors * 5000); // Cada error suma 5 segundos
    }
}