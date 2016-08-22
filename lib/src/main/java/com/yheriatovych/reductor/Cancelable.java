package com.yheriatovych.reductor;

/**
 * Returned from {@link Store#subscribe(Store.StateChangeListener)} to allow unsubscribing.
 */
public interface Cancelable {
    void cancel();
}
