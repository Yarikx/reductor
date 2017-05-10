package com.yheriatovych.reductor.example.reductor.utils;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Commands;
import com.yheriatovych.reductor.Pair;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.Store;

import java.util.LinkedList;

/**
 * Reducer which add 'Undo' action to existing reducer.
 */
public class UndoableReducer<State> implements Reducer<State> {
    private final Reducer<State> sourceReducer;

    public static Action pop() {
        return Action.create("POP");
    }

    private LinkedList<State> stack = new LinkedList<>();

    public UndoableReducer(Reducer<State> sourceReducer) {
        this.sourceReducer = sourceReducer;
    }

    @Override
    public Pair<State, Commands> reduce(State state, Action action) {
        if (action.type.equals("POP")) {
            return stack.isEmpty()
                    ? Pair.create(state)
                    : Pair.create(stack.pop());
        } else if (!action.type.equals(Store.INIT_ACTION)) {
            stack.push(state);
        }
        return sourceReducer.reduce(state, action);
    }
}
