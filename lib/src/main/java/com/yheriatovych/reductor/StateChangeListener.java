package com.yheriatovych.reductor;

/**
 * Listener which will be notified each time state changes.
 * <p>
 * Look {@link Cursor#subscribe(StateChangeListener)}
 */
public interface StateChangeListener<S> {
    void onStateChanged(S state);
}
