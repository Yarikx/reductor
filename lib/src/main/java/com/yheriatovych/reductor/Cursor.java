package com.yheriatovych.reductor;

public interface Cursor<State> {
    State getState();
    Cancelable subscribe(Store.StateChangeListener<State> listener);
    <Substate> Cursor<Substate> map(Store.Function<State, Substate> func);
}
