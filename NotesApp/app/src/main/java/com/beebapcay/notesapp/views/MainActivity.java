package com.beebapcay.notesapp.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.beebapcay.notesapp.R;
import com.beebapcay.notesapp.adapters.NotesAdapter;
import com.beebapcay.notesapp.adapters.TagsAdapter;
import com.beebapcay.notesapp.database.NotesDatabase;
import com.beebapcay.notesapp.entities.Note;
import com.beebapcay.notesapp.entities.Tag;
import com.beebapcay.notesapp.listeners.NotesListener;
import com.beebapcay.notesapp.listeners.RecyclerViewSwipeListener;
import com.beebapcay.notesapp.listeners.TagsListener;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static com.beebapcay.notesapp.utils.ActivityUtils.hideKeyboard;

public class MainActivity extends AppCompatActivity implements NotesListener, TagsListener {

    public static final String EXTRA_IS_VIEW_OR_UPDATE = "com.beebapcay.notesapp.views.MainActivity.EXTRA_IS_VIEW_OR_UPDATE";
    public static final String EXTRA_DATA_NOTE = "com.beebapcay.notesapp.views.MainActivity.EXTRA_DATA_NOTE";
    private static final int REQUEST_CODE_ADD_NODE = 1;
    private static final int REQUEST_CODE_UPDATE_NOTE = 2;
    private static final int REQUEST_CODE_SHOW_ALL_NOTE = 3;
    private static final String TAG = "com.beebapcay.notesapp.views.MainActivity";
    private Context mContext;
    private RecyclerView mNotesRecyclerView;
    private RecyclerView mTagsRecyclerView;
    private List<Note> mNoteList;
    private List<Tag> mTagList;
    private NotesAdapter mNotesAdapter;
    private TagsAdapter mTagsAdapter;
    private TextView mNumberNotes;
    private ImageButton mSearchBtn, mBackBtn;
    private EditText mSearchInput;
    private int noteClickedPosition = -1;
    private int tagClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_main);

        ImageButton btnAddNote = findViewById(R.id.btn_add_note);
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NODE);
            }
        });

        mNotesRecyclerView = findViewById(R.id.view_notes_list);
        mNotesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        

        mTagsRecyclerView = findViewById(R.id.view_tags_list);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
//        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        mTagsRecyclerView.setLayoutManager(
//                new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
                flexboxLayoutManager
        );

        mNumberNotes = findViewById(R.id.text_number_notes);

        mNoteList = new ArrayList<>();
        mNotesAdapter = new NotesAdapter(mNoteList, this);
        mNotesRecyclerView.setAdapter(mNotesAdapter);


        mTagList = new ArrayList<>();
        mTagList.add(new Tag(""));
        mTagsAdapter = new TagsAdapter(mTagList, this);
        mTagsRecyclerView.setAdapter(mTagsAdapter);

        mSearchInput = findViewById(R.id.text_search);

        mSearchBtn = findViewById(R.id.btn_search);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_menu_drawer).setVisibility(View.GONE);
                findViewById(R.id.btn_more).setVisibility(View.GONE);
                findViewById(R.id.text_title).setVisibility(View.GONE);
                findViewById(R.id.text_number_notes).setVisibility(View.GONE);

                getTags();
                findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
                mSearchInput.setVisibility(View.VISIBLE);
                mTagsRecyclerView.setVisibility(View.VISIBLE);

                mSearchInput.requestFocus();
//                showKeyboard(mContext);
            }
        });

        mBackBtn = findViewById(R.id.btn_back);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(mContext);
                findViewById(R.id.btn_menu_drawer).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_more).setVisibility(View.VISIBLE);
                findViewById(R.id.text_title).setVisibility(View.VISIBLE);
                findViewById(R.id.text_number_notes).setVisibility(View.VISIBLE);

                findViewById(R.id.btn_back).setVisibility(View.GONE);
                findViewById(R.id.text_search).setVisibility(View.GONE);
                mSearchInput.setText("");
                mTagsRecyclerView.setVisibility(View.GONE);
            }
        });

        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNotesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mNoteList.size() != 0) {
                    mNotesAdapter.searchNote(s.toString());
                }
            }
        });

        getNotes(REQUEST_CODE_SHOW_ALL_NOTE, false);

//        ConstraintLayout mQuickActionBar = findViewById(R.id.view_quick_actions_bar);
//        ImageButton mMoreBtn = findViewById(R.id.btn_more);
//        mMoreBtn.setOnClickListener(v -> {
//            int [] location = new int[2];
//            mQuickActionBar.getLocationOnScreen(location);
//            Toast.makeText(mContext, "Location Y:" + location[1], Toast.LENGTH_SHORT).show();
//        });
    }


    private void getNotes(final int requestCode, final boolean isNoteDeleted) {
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
                if (requestCode == REQUEST_CODE_SHOW_ALL_NOTE) {
                    mNoteList.addAll(notes);
                    mNotesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NODE) {
                    mNoteList.add(0, notes.get(0));
                    mNotesAdapter.notifyItemInserted(0);
                    mNotesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    mNoteList.remove(noteClickedPosition);


                    if (isNoteDeleted) {
                        mNotesAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        mNoteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        mNotesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }

                mNumberNotes.setText(mNoteList.size() + " notes");
            }
        }

        new GetNotesTask().execute();
    }

    private void getTags() {
        class GetTagsTask extends AsyncTask<Void, Void, List<Tag>> {
            @Override
            protected List<Tag> doInBackground(Void... voids) {
                ArrayList<String> tagNameList = new ArrayList<>();
                for (Note note : mNoteList)
                    if (!note.getTag().trim().isEmpty())
                        tagNameList.add(note.getTag());
                tagNameList = new ArrayList<>(new TreeSet<>(tagNameList));
                List<Tag> temp = new ArrayList<>();
                for (String tagName : tagNameList)
                    temp.add(new Tag(tagName));
                return temp;

            }

            @SuppressLint("LongLogTag")
            @Override
            protected void onPostExecute(List<Tag> tags) {
                super.onPostExecute(tags);
                Log.d(TAG + ".Tag: ", tags.toString());
                mTagList.clear();
                mTagList.add(new Tag(""));
                mTagList.addAll(tags);
                mTagsAdapter.notifyDataSetChanged();
            }

        }

        new GetTagsTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NODE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NODE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra(CreateNoteActivity.EXTRA_IS_NOTE_DELETE, false));
            }
        }
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra(EXTRA_IS_VIEW_OR_UPDATE, true);
        intent.putExtra(EXTRA_DATA_NOTE, note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    @Override
    public void onTagClicked(Tag tag, int position) {
        tagClickedPosition = position;
        mSearchInput.setText(mTagList.get(position).getName());
    }
}