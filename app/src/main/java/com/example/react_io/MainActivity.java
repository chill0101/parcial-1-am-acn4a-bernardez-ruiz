package com.example.react_io;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View;
import android.widget.FrameLayout;
import android.content.Intent;




import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FrameLayout bgContainer;
    private ImageView bg1, bg2;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bgContainer = findViewById(R.id.bgContainer);
        bg1 = findViewById(R.id.bg1);
        bg2 = findViewById(R.id.bg2);

        View content = findViewById(R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // No sabía bien si esto va acá o más adelante
        mAuth = FirebaseAuth.getInstance();


        // Configurar animación después de que el layout mida la pantalla
        // TODO: Extract this to a reusable class e.e
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




        Button btn = findViewById(R.id.btnEmpezar);
        btn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GameMenuActivity.class));
        });
    }
}
