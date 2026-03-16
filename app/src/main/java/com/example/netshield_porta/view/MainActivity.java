package com.example.netshield_porta.view;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netshield_porta.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imgLogo = findViewById(R.id.imgLogo);
        Button btnIniciar = findViewById(R.id.btnIniciar);

        // Brilho a cada 3 segundos — alpha vai de 1.0 para 0.3 e volta
        ObjectAnimator brilho = ObjectAnimator.ofFloat(imgLogo, "alpha", 1f, 0.3f, 1f);
        brilho.setDuration(1500);
        brilho.setRepeatCount(ValueAnimator.INFINITE);
        brilho.setRepeatMode(ValueAnimator.RESTART);
        brilho.setStartDelay(3000); // espera 3 segundos antes de cada brilho

        // Pulso leve de escala junto com o brilho
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imgLogo, "scaleX", 1f, 1.08f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgLogo, "scaleY", 1f, 1.08f, 1f);
        scaleX.setDuration(1500);
        scaleY.setDuration(1500);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setStartDelay(3000);
        scaleY.setStartDelay(3000);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(brilho, scaleX, scaleY);
        animSet.start();

        btnIniciar.setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class)));
    }
}