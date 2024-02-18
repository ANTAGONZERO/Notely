package com.exampletigon.notely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class editNote extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    FirestoreRecyclerAdapter<noteModel, MainActivity.NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        String noteId = getIntent().getStringExtra("noteId");

        EditText editTitle = findViewById(R.id.edit_title);
        EditText editContent = findViewById(R.id.edit_content);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference noteRef = db.collection("Notes").document(firebaseUser.getUid()).collection("User notes").document(noteId);
        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    noteModel note = documentSnapshot.toObject(noteModel.class);
                    editTitle.setText(note.getTitle());
                    editContent.setText(note.getContent());
                } else {
                    Toast.makeText(editNote.this, "Note not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editNote.this, "Error fetching note", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        ImageButton saveButton = findViewById(R.id.edit_done);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String content = editContent.getText().toString();
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    Map<String, Object> noteData = new HashMap<>();

                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH); // Month starts from 0 (January is 0)
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                    String monthName = monthFormat.format(calendar.getTime());
                    String s = monthName + " " + day + ", " + year;



                    noteData.put("title", title);
                    noteData.put("content", content);
                    noteData.put("DMY", s);
                    noteRef.set(noteData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(editNote.this, "Note saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(editNote.this , MainActivity.class));
                            finish();
                            noteAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(editNote.this, "Error saving note", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(editNote.this, "Please enter a title and content", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}