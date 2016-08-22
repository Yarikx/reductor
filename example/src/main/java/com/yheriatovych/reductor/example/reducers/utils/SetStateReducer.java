package com.yheriatovych.reductor.example.reducers.utils;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Reducer;


/**
 * Reducer which wrap other {@link com.yheriatovych.reductor.Reducer} and add one action:
 * "SET_GLOBAL_STATE" to be able to replace state with provided value
 */
public class SetStateReducer<T> implements Reducer<T> {
    public static final String SET_GLOBAL_STATE = "SET_GLOBAL_STATE";
    private final Reducer<T> source;

    public SetStateReducer(Reducer<T> source) {
        this.source = source;
    }

    public static <T> Action setStateAction(T value) {
        return new Action(SET_GLOBAL_STATE, value);
    }

    @Override
    public T reduce(T state, Action action) {
        if (action.type.equals(SET_GLOBAL_STATE)) {
            return (T) action.value;
        }
        return source.reduce(state, action);
    }
}
