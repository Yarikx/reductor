package com.yheriatovych.reductor;

/**
 * Helper methods and utilities for creating and working with {@link Cursor} objects
 */
public class Cursors {
    private Cursors(){}

    /**
     * Creates a Cursor by applying a specified function to state.
     *
     * @param cursor source Cursor to be mapped
     * @param mapper the function that will be applied to a state. Must be free of side effects.
     * @param <State> source state type
     * @param <R>    the output state type
     * @return Cursor that holds state of type T produced by mapper function
     */
    public static <State, R> Cursor<R> map(Cursor<State> cursor, Function<State, R> mapper) {
        return new MappedCursor<>(cursor, mapper);
    }

    /**
     * Notify listener for every state in Store.
     * <p>
     * Note: equivalent to {@link Cursor#subscribe(StateChangeListener)} but current state will be propagated too
     *
     * @param cursor source Cursor
     * @param listener callback which will be notified
     * @param <State> source state type
     * @return instance of {@link Cancelable} to be used to cancel subscription (remove listener)
     */
    public static <State> Cancelable forEach(Cursor<State> cursor, StateChangeListener<State> listener) {
        Cancelable cancelable = cursor.subscribe(listener);
        listener.onStateChanged(cursor.getState());
        return cancelable;
    }
}
