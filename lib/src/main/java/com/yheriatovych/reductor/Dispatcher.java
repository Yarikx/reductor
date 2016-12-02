package com.yheriatovych.reductor;

/**
 * Middleware part which is invoked for each action dispatched to Store.
 * To proceed with the action and deliver it to the Store,
 * dispatcher may invoke {@code nextDispatcher} passed from {@link #create(Store, Dispatcher)}
 */
public interface Dispatcher {
    void dispatch(Object action);
}
