package com.yheriatovych.reductor.example.reductor.filter;

import com.yheriatovych.reductor.Commands;
import com.yheriatovych.reductor.Pair;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.annotations.AutoReducer.Action;
import com.yheriatovych.reductor.example.model.AppState;
import com.yheriatovych.reductor.example.model.NotesFilter;

@AutoReducer
public abstract class NotesFilterReducer implements Reducer<AppState> {
    @Action(value = FilterActions.SET_FILTER,
            from = FilterActions.class)
    Pair<AppState, Commands> setFilter(AppState state, NotesFilter value) {
        return Pair.create(
                AppState.builder(state).setFilter(value).build(),
                null
        );
    }

    public static NotesFilterReducer create() {
        return new NotesFilterReducerImpl();
    }
}
