package com.yheriatovych.reductor.example;

import com.yheriatovych.reductor.example.model.AppState;
import com.yheriatovych.reductor.example.model.Note;
import com.yheriatovych.reductor.example.model.NotesFilter;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;

import java.util.List;

public interface Utils {
    interface Predicate<T> {
        boolean call(T value);
    }

    static <T> PStack<T> filter(PStack<T> xs, Predicate<T> predicate) {
        if (xs.isEmpty()) return xs;
        else {
            T head = xs.get(0);
            PStack<T> tail = xs.subList(1);
            if (predicate.call(head)) {
                return filter(tail, predicate).plus(head);
            } else {
                return filter(tail, predicate);
            }
        }
    }

    static List<Note> getFilteredNotes(AppState appState) {
        List<Note> notes = appState.notes();
        NotesFilter filter = appState.filter();
        return filter(ConsPStack.from(notes), note ->
                filter == NotesFilter.ALL
                        || filter == NotesFilter.CHECKED && note.checked
                        || filter == NotesFilter.UNCHECKED && !note.checked);
    }
}
