package com.example.react_io;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.react_io.services.AuthService;

public class MainActivity extends AppCompatActivity {

    private FrameLayout bgContainer;
    private ImageView bg1, bg2;
    private TextView tvWelcome;
    private Button btnLogout, btnProfile, btnLeaderboard;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authService = new AuthService();

        // Verificar si el usuario está logueado
        if (!authService.isUserLoggedIn()) {
            // Redirigir a LoginActivity si no está logueado
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        setupUI();
        setupAnimations();
        setupClickListeners();
    }

    private void initViews() {
        bgContainer = findViewById(R.id.bgContainer);
        bg1 = findViewById(R.id.bg1);
        bg2 = findViewById(R.id.bg2);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnProfile);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);

        View content = findViewById(R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    private void setupUI() {
        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String welcomeText = "¡Bienvenido, " + (email != null ? email.split("@")[0] : "Usuario") + "!";
            tvWelcome.setText(welcomeText);
        }
    }

    private void setupAnimations() {
        // Configurar animación después de que el layout mida la pantalla
        bgContainer.post(() -> {
            int screenW = getResources().getDisplayMetrics().widthPixels;

            // Posiciones iniciales: una imagen visible y la otra pegada a su derecha
            bg1.setX(0f);
            bg2.setX(screenW);

            // Animación continua:
            ValueAnimator scroll = ValueAnimator.ofInt(0, screenW);
            scroll.setDuration(15000L);               // velocidad del looooooop
            scroll.setInterpolator(new LinearInterpolator());
            scroll.setRepeatCount(ValueAnimator.INFINITE);

            scroll.addUpdateListener(a -> {
                int offset = (int) a.getAnimatedValue();

                // bg1: 0 → -W ; bg2: pegada a la derecha de bg1
                bg1.setX(-offset);
                bg2.setX(-offset + screenW);

                // reset exacto al final para evitar cualquier 1px por redondeo
                if (offset == screenW) {
                    bg1.setX(0f);
                    bg2.setX(screenW);
                }
            });

            scroll.start();
        });
    }

    private void setupClickListeners() {
        Button btnEmpezar = findViewById(R.id.btnEmpezar);
        btnEmpezar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GameMenuActivity.class));
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        btnLeaderboard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LeaderboardActivity.class));
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        authService.logout();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}