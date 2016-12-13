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

    /**
     * Creates a Cursor by applying a specified function to state.
     *
     * @param mapper the function that will be applied to a state. Must be free of side effects.
     * @param <T>    the output type
     * @return Cursor that holds state of type T produced by mapper function
     */
    <T> Cursor<T> map(Function<State, T> mapper);
}
