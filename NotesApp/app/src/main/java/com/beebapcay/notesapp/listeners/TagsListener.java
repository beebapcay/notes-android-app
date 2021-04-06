package com.beebapcay.notesapp.listeners;

import com.beebapcay.notesapp.entities.Tag;

public interface TagsListener {
    void onTagClicked(Tag tag, int position);
}
