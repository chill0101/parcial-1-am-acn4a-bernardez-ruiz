package com.example.react_io.models;

import com.google.firebase.Timestamp;

public class User {
    private String uid;
    private String email;
    private String username;
    private Timestamp createdAt;
    private int totalGamesPlayed;
    private double bestScore;

    public User() {
        // Constructor vacío requerido por Firestore
    }

    public User(String uid, String email, String username) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.createdAt = Timestamp.now();
        this.totalGamesPlayed = 0;
        this.bestScore = Double.MAX_VALUE;
    }

    // Getters y Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getTotalGamesPlayed() { return totalGamesPlayed; }
    public void setTotalGamesPlayed(int totalGamesPlayed) { this.totalGamesPlayed = totalGamesPlayed; }

    public double getBestScore() { return bestScore; }
    public void setBestScore(double bestScore) { this.bestScore = bestScore; }
}