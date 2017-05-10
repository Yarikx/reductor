package com.yheriatovych.reductor;

public interface Cursor<State> {
    /**
     * @return return current state
     */
    State getState();

    /**
     * Subscribe for state changes
     * <p>
     * Note: current state will not be dispatched immediately after subscribe
     *
     * @param listener callback which will be notified each time state changes
     * @return instance of {@link Cancelable} to be used to cancel subscription (remove listener)
     */
    Cancelable subscribe(StateChangeListener<State> listener);
}
