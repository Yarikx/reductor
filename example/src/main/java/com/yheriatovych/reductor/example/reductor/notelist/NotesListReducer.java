package com.yheriatovych.reductor.example.reductor.notelist;

import com.yheriatovych.reductor.Commands;
import com.yheriatovych.reductor.Pair;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.annotations.AutoReducer.Action;
import com.yheriatovych.reductor.example.model.AppState;
import com.yheriatovych.reductor.example.model.Note;
import org.pcollections.TreePVector;

import java.util.List;

@AutoReducer
public abstract class NotesListReducer implements Reducer<AppState> {

    @Action(value = NotesActions.ADD_ACTION,
            from = NotesActions.class)
    public Pair<AppState, Commands> add(AppState state, int id, String content) {
        return Pair.create(state.withNotes(
                TreePVector.from(state.notes()).plus(new Note(id, content, false))
        ));
    }

    @Action(value = NotesActions.TOGGLE,
            from = NotesActions.class)
    public Pair<AppState, Commands> toggle(AppState state, int noteId) {
        List<Note> notes = state.notes();
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            if (note.id == noteId)
                return Pair.create(state.withNotes(
                        TreePVector.from(notes).with(i, new Note(noteId, note.note, !note.checked))
                ));

        }
        return Pair.create(state);
    }

    @Action(value = NotesActions.REMOVE_ITEM,
            from = NotesActions.class)
    public Pair<AppState, Commands> remove(AppState state, int id) {
        List<Note> notes = state.notes();
        for (int i = 0, notesSize = notes.size(); i < notesSize; i++) {
            Note note = notes.get(i);
            if (note.id == id) {
                return Pair.create(state.withNotes(
                        TreePVector.from(notes).minus(i)
                ));
            }
        }
        return Pair.create(state);
    }

    public static NotesListReducer create() {
        return new NotesListReducerImpl();
    }
}
