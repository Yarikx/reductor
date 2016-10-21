package com.yheriatovych.reductor.example.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.yheriatovych.reductor.annotations.CombinedState;
import com.yheriatovych.reductor.example.Utils;
import org.pcollections.ConsPStack;

import java.util.List;

@CombinedState
@AutoValue
public abstract class AppState {
    public abstract List<Note> notes();

    public abstract NotesFilter filter();

    public static TypeAdapter<AppState> typeAdapter(Gson gson) {
        return new AutoValue_AppState.GsonTypeAdapter(gson);
    }

    public List<Note> getFilteredNotes() {
        List<Note> notes = this.notes();
        NotesFilter filter = this.filter();
        return Utils.filter(ConsPStack.from(notes), note ->
                filter == NotesFilter.ALL
                        || filter == NotesFilter.CHECKED && note.checked
                        || filter == NotesFilter.UNCHECKED && !note.checked);
    }
}
