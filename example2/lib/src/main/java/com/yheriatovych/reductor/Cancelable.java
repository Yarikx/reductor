package com.yheriatovych.reductor;

/**
 * Returned from {@link Cursor#subscribe(StateChangeListener)} to allow unsubscribing.
 */
public interface Cancelable {
    void cancel();
}
