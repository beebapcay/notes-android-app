package com.beebapcay.notesapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.beebapcay.notesapp.dao.NoteDao;
import com.beebapcay.notesapp.models.Note;

@Database(entities = Note.class, version = 3, exportSchema = true)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase sNotesDatabase;

    public static synchronized NotesDatabase getDatabase(Context context) {
        if (sNotesDatabase == null) {
            sNotesDatabase = Room.databaseBuilder(
                    context,
                    NotesDatabase.class,
                    "notes_db"
            ).build();
        }
        return sNotesDatabase;
    }

    public abstract NoteDao noteDao();
}
