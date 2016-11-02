package com.yheriatovych.reductor.example.reductor.filter;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.ActionCreator;
import com.yheriatovych.reductor.example.model.NotesFilter;

@ActionCreator
public interface FilterActions {
    String SET_FILTER = "SET_FILTER";

    @ActionCreator.Action(SET_FILTER)
    Action setFilter(NotesFilter filter);
}
