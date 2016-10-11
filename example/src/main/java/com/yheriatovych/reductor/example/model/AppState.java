package com.yheriatovych.reductor.example.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.yheriatovych.reductor.annotations.CombinedState;

import java.util.List;

@CombinedState
@AutoValue
public abstract class AppState {
    public abstract List<Note> notes();

    public abstract NotesFilter filter();

    public static TypeAdapter<AppState> typeAdapter(Gson gson) {
        return new AutoValue_AppState.GsonTypeAdapter(gson);
    }
}
