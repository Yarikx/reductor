package com.colorhaake.traveler.reducer.utils;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Commands;
import com.yheriatovych.reductor.Pair;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.Store;

import java.util.LinkedList;

/**
 * Created by josephcheng on 2017/3/12.
 *
 * Reducer which add 'Undo' action to existing reducer.
 */
public class UndoableReducer<State> implements Reducer<State> {
    public static final String POP = "POP";
    private final Reducer<State> source;

    public static Action createPopAction() {
        return Action.create(POP);
    }

    private LinkedList<State> stack = new LinkedList<>();

    public UndoableReducer(Reducer<State> source) {
        this.source = source;
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
        return source.reduce(state, action);
    }
}
