package com.yheriatovych.reductor.example.reductor.filter;

import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.annotations.AutoReducer.Action;
import com.yheriatovych.reductor.example.model.NotesFilter;

@AutoReducer
public abstract class NotesFilterReducer implements Reducer<NotesFilter> {
    @AutoReducer.InitialState
    NotesFilter initialState() {
        return NotesFilter.ALL;
    }

    @Action(value = FilterActions.SET_FILTER,
            generateActionCreator = true,
            from = FilterActions.class)
    NotesFilter setFilter(NotesFilter state, NotesFilter value) {
        return value;
    }

    public static NotesFilterReducer create() {
        return new NotesFilterReducerImpl();
    }
}
