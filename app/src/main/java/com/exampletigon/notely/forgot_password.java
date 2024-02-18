package com.exampletigon.notely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class forgot_password extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextView back_btn = findViewById(R.id.back_to_login);
        EditText email = findViewById(R.id.Email);
        RelativeLayout recoverPassword  = findViewById(R.id.next);

        firebaseAuth = FirebaseAuth.getInstance();



        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(forgot_password.this , Login.class);
                startActivity(i);
                finish();
            }
        });

        recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e_mail = email.getText().toString();
                if(e_mail.isEmpty())
                {
                    Toast.makeText(forgot_password.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }
                else{
                    // we write furthur code
                    firebaseAuth.sendPasswordResetEmail(e_mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(forgot_password.this, "Mail sent to recover password", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgot_password.this , MainActivity.class));
                            }
                            else {
                                Toast.makeText(forgot_password.this, "Mail not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });









    }
}