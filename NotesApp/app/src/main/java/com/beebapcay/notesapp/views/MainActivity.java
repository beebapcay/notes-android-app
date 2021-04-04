package com.beebapcay.notesapp.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.beebapcay.notesapp.R;
import com.beebapcay.notesapp.adapters.NotesAdapter;
import com.beebapcay.notesapp.database.NotesDatabase;
import com.beebapcay.notesapp.models.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_NODE = 1;
    private static final String TAG = "com.beebapcay.notesapp.views.MainActivity";

    private Context mContext;
    private RecyclerView mNotesRecyclerView;
    private List<Note> mNoteList;
    private NotesAdapter mNotesAdapter;
    private TextView mNumberNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_main);

        ImageButton btnAddNote = findViewById(R.id.btn_add_note);
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_CREATE_NODE);
            }
        });

        mNotesRecyclerView = findViewById(R.id.view_notes_list);
        mNotesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        mNumberNotes = findViewById(R.id.text_number_notes);

        mNoteList = new ArrayList<>();
        mNotesAdapter = new NotesAdapter(mNoteList);
        mNotesRecyclerView.setAdapter(mNotesAdapter);

        getNotes();
    }


    private void getNotes() {
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase
                        .getDatabase(getApplicationContext())
                        .noteDao()
                        .getAllNotes();
            }

            @SuppressLint("LongLogTag")
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                Log.d(TAG + ".NOTE: ", notes.toString());
                if (notes.size() != 0) {
                    if (mNoteList.size() == 0) {
                        mNoteList.addAll(notes);
                        mNotesAdapter.notifyDataSetChanged();
                    } else {
                        mNoteList.add(0, notes.get(0));
                        mNotesAdapter.notifyItemInserted(0);
                    }

                    mNotesRecyclerView.smoothScrollToPosition(0);
                }

                mNumberNotes.setText(mNoteList.size() + " notes");
            }
        }

        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_NODE) {
            switch (resultCode) {
                case (RESULT_OK):
                    getNotes();
                    break;
                default:
                    break;
            }
        }
    }
}