package com.yheriatovych.reductor.example.reducers.utils;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Reducer;

import java.util.LinkedList;

/**
 * Reducer which add 'Undo' action to existing reducer.
 */
public class UndoableReducer<State> implements Reducer<State> {
    private final Reducer<State> sourceReducer;

    public static Action pop() {
        return new Action("POP", null);
    }

    private LinkedList<State> stack = new LinkedList<>();

    public UndoableReducer(Reducer<State> sourceReducer) {
        this.sourceReducer = sourceReducer;
    }

    @Override
    public State reduce(State state, Action action) {
        if (action.type.equals("POP")) {
            return stack.isEmpty()
                    ? state
                    : stack.pop();
        }
        stack.push(state);
        return sourceReducer.reduce(state, action);
    }
}
