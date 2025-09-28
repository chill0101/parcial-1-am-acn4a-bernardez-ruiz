package com.example.react_io;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class InDevelopmentActivity extends AppCompatActivity {

    private ImageButton homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_development_screen);

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> finish());
    }
}