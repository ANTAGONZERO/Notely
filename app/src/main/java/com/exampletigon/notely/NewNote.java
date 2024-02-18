package com.exampletigon.notely;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewNote extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<noteModel, MainActivity.NoteViewHolder> noteAdapter;

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        EditText note_title = findViewById(R.id.new_title);
        EditText note_content = findViewById(R.id.new_content);
        ImageButton create_note = findViewById(R.id.done);
        ImageButton back_to_main = findViewById(R.id.back_to_main);

        executorService = Executors.newSingleThreadExecutor();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(NewNote.this , MainActivity.class));
                finish();
                noteAdapter.notifyDataSetChanged();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        create_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = note_title.getText().toString();
                String content = note_content.getText().toString();
                Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH); // Month starts from 0 (January is 0)
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                String monthName = monthFormat.format(calendar.getTime());

                String s = monthName + " " + day + ", " + year;

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(NewNote.this, "Both fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    executorService.execute(() -> {
                        DocumentReference documentReference = firestore.collection("Notes")
                                .document(firebaseUser.getUid())
                                .collection("User notes")
                                .document();

                        Map<String, Object> note = new HashMap<>();
                        note.put("title", title);
                        note.put("content", content);
                        note.put("DMY", s);

                        documentReference.set(note)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        runOnUiThread(() -> {
                                            startActivity(new Intent(NewNote.this, MainActivity.class));
                                            finish();
                                            noteAdapter.notifyDataSetChanged();
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        runOnUiThread(() -> {
                                            String errorMessage = "Failed to create note. Error: " + e.getMessage();
                                            Log.e("Firestore", errorMessage);
                                            Toast.makeText(NewNote.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                    });
                }
            }
        });

        back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewNote.this, MainActivity.class));
                noteAdapter.notifyDataSetChanged();
                finish();
            }
        });
    }
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown ExecutorService when the activity is destroyed
        if (executorService != null) {
            executorService.shutdown();
        }
    }

}
