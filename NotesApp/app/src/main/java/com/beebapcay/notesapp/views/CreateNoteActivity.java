package com.beebapcay.notesapp.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.beebapcay.notesapp.R;
import com.beebapcay.notesapp.database.NotesDatabase;
import com.beebapcay.notesapp.models.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.beebapcay.notesapp.utils.ActivityUtils.hideKeyboard;

public class CreateNoteActivity extends AppCompatActivity {

    private static final String TAG = "com.beebapcay.notesapp.views.CreateNoteActivity";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;



    private EditText mTitleNoteInput, mContentNoteInput;
    private TextView mNoteDateTime;
    private Context mContext;
    private ImageView mIndicator;
    private ImageView mImageNote;

    private String selectedNoteColor;
    private String selectedImagePath;
    private Note alreadyAvailableNote;

    @SuppressLint("ResourceType")
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
        mNoteDateTime = findViewById(R.id.text_note_date_time);
        mIndicator = findViewById(R.id.img_indicator);
        mImageNote = findViewById(R.id.img_note_image);

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

        selectedNoteColor = getResources().getString(R.color.colorNoteBackground1);
        selectedImagePath = "";

        if (getIntent().getBooleanExtra(MainActivity.EXTRA_IS_VIEW_OR_UPDATE, false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra(MainActivity.EXTRA_DATA_NOTE);
            setViewOrUpdateNote();
        }

        initMiscellaneous();
        setIndicatorColor();
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
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);

        if (alreadyAvailableNote != null)
            note.setId(alreadyAvailableNote.getId());

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

    @SuppressLint("ResourceType")
    private void initMiscellaneous() {
        final ConstraintLayout viewMiscellaneous = findViewById(R.id.view_miscellaneous);
        final BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(viewMiscellaneous);
        viewMiscellaneous.findViewById(R.id.text_title).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        else bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
        );

        final ImageView imgColor1 = viewMiscellaneous.findViewById(R.id.img_color_1);
        final ImageView imgColor2 = viewMiscellaneous.findViewById(R.id.img_color_2);
        final ImageView imgColor3 = viewMiscellaneous.findViewById(R.id.img_color_3);
        final ImageView imgColor4 = viewMiscellaneous.findViewById(R.id.img_color_4);
        final ImageView imgColor5 = viewMiscellaneous.findViewById(R.id.img_color_5);
        final ImageView imgColor6 = viewMiscellaneous.findViewById(R.id.img_color_6);

        viewMiscellaneous.findViewById(R.id.bg_color_1).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                selectedNoteColor = getResources().getString(R.color.colorNoteBackground1);
                imgColor1.setImageResource(R.drawable.ic_done);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(0);
                imgColor6.setImageResource(0);
                setIndicatorColor();
            }
        });

        viewMiscellaneous.findViewById(R.id.bg_color_2).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                selectedNoteColor = getResources().getString(R.color.colorNoteBackground2);
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(R.drawable.ic_done);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(0);
                imgColor6.setImageResource(0);
                setIndicatorColor();
            }
        });

        viewMiscellaneous.findViewById(R.id.bg_color_3).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                selectedNoteColor = getResources().getString(R.color.colorNoteBackground3);
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(R.drawable.ic_done);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(0);
                imgColor6.setImageResource(0);
                setIndicatorColor();
            }
        });

        viewMiscellaneous.findViewById(R.id.bg_color_4).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                selectedNoteColor = getResources().getString(R.color.colorNoteBackground4);
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(R.drawable.ic_done);
                imgColor5.setImageResource(0);
                imgColor6.setImageResource(0);
                setIndicatorColor();
            }
        });

        viewMiscellaneous.findViewById(R.id.bg_color_5).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                selectedNoteColor = getResources().getString(R.color.colorNoteBackground5);
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(R.drawable.ic_done);
                imgColor6.setImageResource(0);
                setIndicatorColor();
            }
        });

        viewMiscellaneous.findViewById(R.id.bg_color_6).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                selectedNoteColor = getResources().getString(R.color.colorNoteBackground6);
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(0);
                imgColor6.setImageResource(R.drawable.ic_done);
                setIndicatorColor();
            }
        });

        if (alreadyAvailableNote != null && !alreadyAvailableNote.getColor().trim().isEmpty()) {
            if (alreadyAvailableNote.getColor() == getResources().getString(R.color.colorNoteBackground1)) {
                viewMiscellaneous.findViewById(R.id.bg_color_1).performClick();
            } else if (alreadyAvailableNote.getColor() == getResources().getString(R.color.colorNoteBackground2)) {
                viewMiscellaneous.findViewById(R.id.bg_color_2).performClick();
            } else if (alreadyAvailableNote.getColor() == getResources().getString(R.color.colorNoteBackground3)) {
                viewMiscellaneous.findViewById(R.id.bg_color_3).performClick();
            } else if (alreadyAvailableNote.getColor() == getResources().getString(R.color.colorNoteBackground4)) {
                viewMiscellaneous.findViewById(R.id.bg_color_4).performClick();
            } else if (alreadyAvailableNote.getColor() == getResources().getString(R.color.colorNoteBackground5)) {
                viewMiscellaneous.findViewById(R.id.bg_color_5).performClick();
            } else {
                viewMiscellaneous.findViewById(R.id.bg_color_6).performClick();
            }

        }

        viewMiscellaneous.findViewById(R.id.view_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateNoteActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });
    }

    private void setIndicatorColor() {
        mIndicator.setColorFilter(Color.parseColor(selectedNoteColor));
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        mImageNote.setImageBitmap(bitmap);
                        mImageNote.setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (FileNotFoundException fnfe) {
                        Toast.makeText(this, fnfe.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null,null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void setViewOrUpdateNote() {
        mTitleNoteInput.setText(alreadyAvailableNote.getTitle());
        mContentNoteInput.setText(alreadyAvailableNote.getContent());
        mNoteDateTime.setText(alreadyAvailableNote.getDateTime());
        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            mImageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            mImageNote.setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }
    }
}