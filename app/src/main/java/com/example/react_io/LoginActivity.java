package com.example.react_io;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.react_io.services.AuthService;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etUsername;
    private Button btnLogin, btnRegister;
    private AuthService authService;
    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = new AuthService();

        // Verificar si el usuario ya está logueado
        if (authService.isUserLoggedIn()) {
            startMainActivity();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);


        etUsername.setVisibility(android.view.View.GONE);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            if (isRegisterMode) {
                registerUser();
            } else {
                loginUser();
            }
        });

        btnRegister.setOnClickListener(v -> toggleMode());
    }

    private void toggleMode() {
        isRegisterMode = !isRegisterMode;
        if (isRegisterMode) {
            etUsername.setVisibility(android.view.View.VISIBLE);
            btnLogin.setText("Registrarse");
            btnRegister.setText("¿Ya tienes cuenta? Inicia sesión");
        } else {
            etUsername.setVisibility(android.view.View.GONE);
            btnLogin.setText("Iniciar Sesión");
            btnRegister.setText("¿No tienes cuenta? Regístrate");
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completá todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        authService.loginUser(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                Toast.makeText(LoginActivity.this, "Bienvenido!", Toast.LENGTH_SHORT).show();
                startMainActivity();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Por favor completá todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        authService.registerUser(email, password, username, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                Toast.makeText(LoginActivity.this, "Registro exitoso! BIENVENIDO D:", Toast.LENGTH_SHORT).show();
                startMainActivity();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}