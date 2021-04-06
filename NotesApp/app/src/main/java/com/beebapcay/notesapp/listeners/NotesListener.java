package com.beebapcay.notesapp.listeners;

import com.beebapcay.notesapp.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
