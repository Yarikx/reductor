package com.colorhaake.traveler.reducer.utils;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Commands;
import com.yheriatovych.reductor.Pair;
import com.yheriatovych.reductor.Reducer;

/**
 * Created by josephcheng on 2017/3/12.
 *
 * Reducer which wrap other {@link com.yheriatovych.reductor.Reducer} and add one action:
 * "SET_GLOBAL_STATE" to be able to replace state with provided value.
 *
 * Used for testing
 */
public class SetStateReducer<T> implements Reducer<T> {
    public static final String SET_GLOBAL_STATE = "SET_GLOBAL_STATE";
    private final Reducer<T> source;

    public SetStateReducer(Reducer<T> source) {
        this.source = source;
    }

    public static <T> Action createSetStateAction(T value) {
        return Action.create(SET_GLOBAL_STATE, value);
    }

    @Override
    public Pair<T, Commands> reduce(T state, Action action) {
        if (action.type.equals(SET_GLOBAL_STATE)) {
            return Pair.create((T) action.values);
        }
        return source.reduce(state, action);
    }
}
