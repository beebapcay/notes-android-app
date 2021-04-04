package com.beebapcay.notesapp.listeners;

import com.beebapcay.notesapp.models.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
