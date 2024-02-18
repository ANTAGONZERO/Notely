package com.exampletigon.notely;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;

public class launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLaunch = preferences.getBoolean("isFirstLaunch", true);

        LottieAnimationView animationView = findViewById(R.id.launcher_lottie_animation);
        LottieAnimationView animationViewtext = findViewById(R.id.text_lottie_animation);

        if (isFirstLaunch) {

            Intent i = new Intent(launcher.this, getStarted.class);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Play animationView and set its repeat count to 0 (play once)
                    animationView.playAnimation();
                    animationViewtext.playAnimation();
                    animationViewtext.setProgress(0.6f);

                    startActivity(i);
                    finish();
                }
            }, 3000);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();
        }else {
            startActivity(new Intent(launcher.this , MainActivity.class));
            finish();
        }


    }
}
