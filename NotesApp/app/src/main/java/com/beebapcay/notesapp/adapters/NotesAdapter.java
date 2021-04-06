package com.beebapcay.notesapp.adapters;

import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.beebapcay.notesapp.R;
import com.beebapcay.notesapp.listeners.NotesListener;
import com.beebapcay.notesapp.entities.Note;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> mNotes;
    private NotesListener mNotesListener;
    private Timer mTimer;
    private List<Note> mNotesSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        mNotes = notes;
        mNotesListener = notesListener;
        mNotesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(mNotes.get(position));
        holder.mViewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotesListener.onNoteClicked(mNotes.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void searchNote(final String searchKeyWord) {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyWord.trim().isEmpty()) {
                    mNotes = mNotesSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : mNotesSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyWord.toLowerCase())
                                || note.getContent().toLowerCase().contains(searchKeyWord.toLowerCase())
                                || note.getTag().toLowerCase().contains(searchKeyWord.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    mNotes = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }


    public void cancelTimer() {
        if (mTimer != null)
            mTimer.cancel();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle, mDateTime, mContent;
        ConstraintLayout mViewNote;
        RoundedImageView mImage;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.text_title);
            mDateTime = itemView.findViewById(R.id.text_date_time);
            mContent = itemView.findViewById(R.id.text_content);
            mViewNote = itemView.findViewById(R.id.view_note);
            mImage = itemView.findViewById(R.id.img_image);
        }

        void setNote(Note note) {
            if (note.getTitle().trim().isEmpty())
                mTitle.setVisibility(View.GONE);
            else {
                mTitle.setVisibility(View.VISIBLE);
                mTitle.setText(note.getTitle());
            }


            mDateTime.setText(note.getDateTime());

            if (note.getContent().trim().isEmpty())
                mContent.setVisibility(View.GONE);
            else {
                mContent.setVisibility(View.VISIBLE);
                mContent.setText(note.getContent());
            }

            mViewNote.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(note.getColor())));

            if (note.getImagePath() != null) {
                mImage.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                mImage.setVisibility(View.VISIBLE);
            } else {
                mImage.setVisibility(View.GONE);
            }
        }
    }
}
