package com.exampletigon.notely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Signin extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 123;

    private FirestoreRecyclerAdapter<noteModel, MainActivity.NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);

        RelativeLayout googleSignin = findViewById(R.id.GoogleSignin);

        EditText email = findViewById(R.id.S_Email);
        EditText password = findViewById(R.id.S_Password);
        RelativeLayout signin = findViewById(R.id.SignIn);
        TextView forgotpassword = findViewById(R.id.S_ForgotPassword);
        TextView gotologin = findViewById(R.id.GoToLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String clientId = "191174685298-5ptcansb8u1njfit99mpi96ocdg86jsg.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken((clientId))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        if(firebaseUser!=null)
        {
            finish();
            startActivity(new Intent(Signin.this , MainActivity.class));
        }


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e_mail = email.getText().toString().trim();
                String key = password.getText().toString().trim();

                if(e_mail.isEmpty() || key.isEmpty())
                Toast.makeText(Signin.this, "All feilds are required", Toast.LENGTH_SHORT).show();
                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(e_mail , key).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Signin.this, "Registration Successful !", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }
                            else Toast.makeText(Signin.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                            
                        }
                    });
                }
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signin.this , forgot_password.class);
                startActivity(i);
            }
        });

        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signin.this , Login.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View , String>(gotologin , "sigin_to_login");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signin.this , pairs);
                startActivity(i , options.toBundle());
                finish();
            }
        });
    }

    public void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signin.this, "Verification E-mail sent, Verify and Log In again", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Signin.this, Login.class));
                    } else {
                        Toast.makeText(Signin.this, "Failed to send verification E-mail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // If firebaseUser is null, handle the situation accordingly
            Toast.makeText(this, "User not found. Please log in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Signin.this, Login.class));
        }
    }

    public void signInWithGoogle()
    {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(this, "Google Sign In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            startActivity(new Intent(Signin.this , MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Signin.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}