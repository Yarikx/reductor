package com.yheriatovych.reductor.example;

import android.support.v7.util.DiffUtil;
import com.yheriatovych.reductor.example.model.Note;

import java.util.List;

class NotesDiffCallback extends DiffUtil.Callback {
    private final List<Note> oldNotes;
    private final List<Note> newNotes;

    public NotesDiffCallback(List<Note> oldNotes, List<Note> newNotes) {
        this.oldNotes = oldNotes;
        this.newNotes = newNotes;
    }

    @Override
    public int getOldListSize() {
        return oldNotes.size();
    }

    @Override
    public int getNewListSize() {
        return newNotes.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldNotes.get(oldItemPosition).id == newNotes.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldNotes.get(oldItemPosition).equals(newNotes.get(newItemPosition));
    }
}
