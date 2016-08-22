package com.yheriatovych.reductor.example.model;

import com.yheriatovych.reductor.annotations.CombinedState;

import java.util.List;

@CombinedState
public interface AppState {
    List<Note> notes();

    NotesFilter filter();
}
