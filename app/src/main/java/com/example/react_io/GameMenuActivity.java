package com.example.react_io;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class GameMenuActivity extends AppCompatActivity {

    private FrameLayout bgContainer;
    private ImageView bg1, bg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_menu);

        bgContainer = findViewById(R.id.bgContainer);
        bg1 = findViewById(R.id.bg1);
        bg2 = findViewById(R.id.bg2);

        View content = findViewById(R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // misma animación de fondo que en MainActivity
        bgContainer.post(() -> {
            int screenW = getResources().getDisplayMetrics().widthPixels;

            bg1.setX(0f);
            bg2.setX(screenW);

            ValueAnimator scroll = ValueAnimator.ofInt(0, screenW);
            scroll.setDuration(15000L);
            scroll.setInterpolator(new LinearInterpolator());
            scroll.setRepeatCount(ValueAnimator.INFINITE);

            scroll.addUpdateListener(a -> {
                int offset = (int) a.getAnimatedValue();
                bg1.setX(-offset);
                bg2.setX(-offset + screenW);
                if (offset == screenW) {
                    bg1.setX(0f);
                    bg2.setX(screenW);
                }
            });

            scroll.start();
        });

        // Botones
        Button game1 = findViewById(R.id.game1Button);
        Button game2 = findViewById(R.id.game2Button);
        Button game3 = findViewById(R.id.game3Button);

        game1.setOnClickListener(v -> {
            // Abrir el juego 1 (ReactionGameActivity)
            startActivity(new android.content.Intent(GameMenuActivity.this, ReactionGameActivity.class));
        });

        game2.setOnClickListener(v -> {
            // TODO: conectar al Juego 2
        });

        game3.setOnClickListener(v -> {
            // TODO: conectar al Juego 3
        });
    }
}
