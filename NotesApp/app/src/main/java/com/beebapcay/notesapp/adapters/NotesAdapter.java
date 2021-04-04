package com.beebapcay.notesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beebapcay.notesapp.R;
import com.beebapcay.notesapp.models.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> mNotes;


    public NotesAdapter(List<Note> notes) {
        mNotes = notes;
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
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle, mDateTime, mContent;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.text_title);
            mDateTime = itemView.findViewById(R.id.text_date_time);
            mContent = itemView.findViewById(R.id.text_content);
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

        }
    }
}
