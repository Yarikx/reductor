package com.yheriatovych.reductor;

import java.util.concurrent.atomic.AtomicReference;

class MappedCursor<State, SubState> implements Cursor<SubState> {
    private final Cursor<State> source;
    private final Function<State, SubState> mapper;

    MappedCursor(Cursor<State> source, Function<State, SubState> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public SubState getState() {
        return mapper.apply(source.getState());
    }

    @Override
    public Cancelable subscribe(StateChangeListener<SubState> listener) {
        AtomicReference<SubState> atomicReference = new AtomicReference<>();
        return source.subscribe(state -> {
            SubState subState = mapper.apply(state);
            SubState previous = atomicReference.getAndSet(subState);
            if (!eq(previous, subState)) {
                listener.onStateChanged(subState);
            }
        });
    }

    private static boolean eq(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
