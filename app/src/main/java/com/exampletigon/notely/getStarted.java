package com.exampletigon.notely;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class getStarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        LottieAnimationView animationView = findViewById(R.id.getstarted_lottie_animation);
        animationView.playAnimation();

        Button get_started = findViewById(R.id.GetStarted);
        TextView welcometext = findViewById(R.id.WelcomeText);

        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getStarted.this , choose_method.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View , String>(get_started , "Get Started");
                pairs[1] = new Pair<View , String>(animationView , "image1");
                pairs[2] = new Pair<View , String>(welcometext, "startingtext");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getStarted.this , pairs);
                startActivity(i , options.toBundle());
                finish();
            }
        });


    }
}