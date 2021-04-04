package com.beebapcay.notesapp.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beebapcay.notesapp.R;
import com.beebapcay.notesapp.database.NotesDatabase;
import com.beebapcay.notesapp.models.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.beebapcay.notesapp.utils.ActivityUtils.hideKeyboard;

public class CreateNoteActivity extends AppCompatActivity {

    private static final String TAG = "com.beebapcay.notesapp.views.CreateNoteActivity";

    private EditText mTitleNoteInput, mContentNoteInput;
    private TextView mNoteDateTime;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        mContext = this;

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitleNoteInput = findViewById(R.id.text_note_title);
        mContentNoteInput = findViewById(R.id.text_note_content);
        mNoteDateTime = findViewById(R.id.text_date_time);

        mNoteDateTime.setText(
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault())
                        .format(new Date())
        );

        ImageButton btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

    }

    private void saveNote() {
        if (mTitleNoteInput.getText().toString().trim().isEmpty()
                && mContentNoteInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(mTitleNoteInput.getText().toString());
        note.setContent(mContentNoteInput.getText().toString());
        note.setDateTime(mNoteDateTime.getText().toString());

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideKeyboard(mContext);
                setResult(RESULT_OK, new Intent());
                finish();
            }
        }

        new SaveNoteTask().execute();
    }



}