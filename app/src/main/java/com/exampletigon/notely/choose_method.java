package com.exampletigon.notely;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;

public class choose_method extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_method);

        LottieAnimationView animationView = findViewById(R.id.getstarted_lottie_animation);
        animationView.playAnimation();

        Button login = findViewById(R.id.ch_login);
        Button signin = findViewById(R.id.ch_signin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(choose_method.this , Login.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View , String>(login, "login_trans");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(choose_method.this , pairs);
                startActivity(i , options.toBundle());
                finish();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(choose_method.this , Signin.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View , String>(signin, "signin_trans");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(choose_method.this , pairs);
                startActivity(i , options.toBundle());
                finish();
            }
        });

    }

}