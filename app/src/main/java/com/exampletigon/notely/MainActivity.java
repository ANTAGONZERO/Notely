package com.exampletigon.notely;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<noteModel, NoteViewHolder> noteAdapter;

    private RecyclerView recyclerView;

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.note_recycler);


        RelativeLayout layout = findViewById(R.id.relative_layout_main);


        VideoView videoView = findViewById(R.id.VideoBack);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.fluid_gradient_background;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
            }
        });

        SearchView searchView = findViewById(R.id.note_search);
        searchView.clearFocus();

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);

                searchView.requestFocus();
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });


        FloatingActionButton addNewNote = findViewById(R.id.add_new_note);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();



        addNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View , String>(layout , "main_to_new_note" );
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this , pairs);
                startActivity(new Intent(MainActivity.this, NewNote.class) , options.toBundle());
            }
        });

        executorService = Executors.newFixedThreadPool(2);

        if(firebaseUser != null) {

                Query query = firebaseFirestore.collection("Notes")
                        .document(firebaseUser.getUid())
                        .collection("User notes")
                        .orderBy("title", Query.Direction.DESCENDING);

                FirestoreRecyclerOptions<noteModel> allUserNotes = new FirestoreRecyclerOptions.Builder<noteModel>()
                        .setQuery(query, noteModel.class)
                        .build();

                    noteAdapter = new FirestoreRecyclerAdapter<noteModel, NoteViewHolder>(allUserNotes) {

                        @Override
                        protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull noteModel model) {

                            holder.noteTitle.setText(model.getTitle());
                            holder.noteContent.setText(model.getContent());
                            holder.s.setText(model.getDMY());
                            MaterialCardView cardView = holder.itemView.findViewById(R.id.note_card);
                            int color = cardView.getCardBackgroundColor().getDefaultColor();
                            int alpha = 150;
                            int newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
                            cardView.setCardBackgroundColor(newColor);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String noteId = noteAdapter.getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                                    Intent intent = new Intent(MainActivity.this, editNote.class);
                                    intent.putExtra("noteId", noteId);
                                    intent.putExtra("title", model.getTitle());
                                    intent.putExtra("content", model.getContent());
                                    startActivity(intent);
                                }
                            });

                            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    // Show a confirmation dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Delete Note");
                                    builder.setMessage("Are you sure you want to delete this note?");
                                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String noteId = noteAdapter.getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                                            firebaseFirestore.collection("Notes")
                                                    .document(firebaseUser.getUid())
                                                    .collection("User notes")
                                                    .document(noteId)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Note deleted successfully
                                                            Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Failed to delete note
                                                            Toast.makeText(MainActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", null);
                                    builder.show();
                                    return true; // consume the long click event
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
                            return new NoteViewHolder(view);
                        }


                        @Override
                        public void onDataChanged() {
                            super.onDataChanged();
                            noteAdapter.notifyDataSetChanged();
                        }
                    };

                    recyclerView.setHasFixedSize(true);
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(noteAdapter);
        }else{
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this , Signin.class));
        }
    }




    protected void onDestroy() {
        super.onDestroy();
        // Shutdown ExecutorService when the activity is destroyed
        if (executorService != null) {
            executorService.shutdown();
            noteAdapter.stopListening();
        }
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        private final TextView noteTitle;
        private final TextView noteContent;
        private final TextView s;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteContent = itemView.findViewById(R.id.note_content);
            s = itemView.findViewById(R.id.note_DMY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int ID = item.getItemId();

        if (ID == R.id.logout) {
            if (firebaseAuth != null) {
                try {
                    firebaseAuth.signOut();
                    Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(MainActivity.this, Login.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Logout Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart called");
        Toast.makeText(this, "Start !", Toast.LENGTH_SHORT).show();
        if(firebaseUser != null && noteAdapter != null) {
            noteAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop called");
        Toast.makeText(this, "Stop !", Toast.LENGTH_SHORT).show();
        if (firebaseUser != null && noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume called");
        Toast.makeText(this, "Resume !", Toast.LENGTH_SHORT).show();
        if (firebaseUser != null && noteAdapter != null) {
            noteAdapter.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause called");
        Toast.makeText(this, "Pause !", Toast.LENGTH_SHORT).show();
        if (firebaseUser != null && noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }



    private void performSearch(String newText) {
        if (noteAdapter != null && firebaseUser != null) {
            Query baseQuery = firebaseFirestore.collection("Notes")
                    .document(firebaseUser.getUid())
                    .collection("User notes");

            if (!newText.isEmpty()) {
                // Perform case-insensitive search operation
                String userInput = newText.toLowerCase(Locale.getDefault());
                baseQuery = baseQuery.whereGreaterThanOrEqualTo("title", userInput)
                        .whereLessThanOrEqualTo("title", userInput + "\uf8ff");
            }

            baseQuery = baseQuery.orderBy("title", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<noteModel> options = new FirestoreRecyclerOptions.Builder<noteModel>()
                    .setQuery(baseQuery, noteModel.class)
                    .build();

            // Execute adapter update on the main thread
            runOnUiThread(() -> {
                noteAdapter.updateOptions(options);
            });
        }
    }





}
