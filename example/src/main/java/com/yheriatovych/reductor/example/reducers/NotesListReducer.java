package com.yheriatovych.reductor.example.reducers;

import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.annotations.AutoReducer.Action;
import com.yheriatovych.reductor.example.model.Note;
import org.pcollections.TreePVector;

import java.util.List;

@AutoReducer
public abstract class NotesListReducer implements Reducer<List<Note>> {
    public static final String ADD_ACTION = "ADD_ITEM";
    public static final String TOGGLE = "TOGGLE";
    public static final String REMOVE_ITEM = "REMOVE_ITEM";

    @AutoReducer.InitialState
    List<Note> initialState() {
        return TreePVector.empty();
    }

    @Action(ADD_ACTION)
    public List<Note> add(List<Note> state, int id, String content) {
        return TreePVector.from(state).plus(new Note(id, content, false));
    }

    @Action(TOGGLE)
    public List<Note> toggle(List<Note> notes, int noteId) {
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            if (note.id == noteId)
                return TreePVector.from(notes).with(i, new Note(noteId, note.note, !note.checked));
        }
        return notes;
    }

    @Action(REMOVE_ITEM)
    public List<Note> remove(List<Note> notes, int id) {
        for (int i = 0, notesSize = notes.size(); i < notesSize; i++) {
            Note note = notes.get(i);
            if (note.id == id) {
                return TreePVector.from(notes).minus(i);
            }
        }
        return notes;
    }

    public static NotesListReducer create() {
        return new NotesListReducerImpl();
    }
}
