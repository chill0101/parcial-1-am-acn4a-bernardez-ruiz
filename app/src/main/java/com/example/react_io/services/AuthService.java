package com.example.react_io.services;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.react_io.models.User;

public class AuthService {
    private static final String TAG = "AuthService";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public AuthService() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String error);
    }

    public void registerUser(String email, String password, String username, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Crear documento de usuario en Firestore
                            User user = new User(firebaseUser.getUid(), email, username);
                            db.collection("users").document(firebaseUser.getUid())
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Usuario creado en Firestore");
                                        callback.onSuccess(firebaseUser);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error creando usuario en Firestore", e);
                                        callback.onFailure("Error al crear perfil de usuario");
                                    });
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        callback.onSuccess(user);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void logout() {
        mAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }
}