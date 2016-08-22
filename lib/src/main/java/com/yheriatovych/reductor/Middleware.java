package com.yheriatovych.reductor;

/**
 * Middleware provides a third-party extension point between
 * dispatching an action, and the moment it reaches the reducer
 * <p>
 * Can be useful to support actions different from {@link Action} or to add logging or crash reporting
 */
public interface Middleware<State> {
    /**
     * This function is called by {@link Store} to dispatch given action
     * <p>
     * Implementation of Middleware may:
     * <ul>
     * <li> Call {@code nextDispatcher.call(action)} with potentially different action to proceed with dispatch chain
     * <li> Not call {@code nextDispatcher.call(action)} to discard action
     * <li> Use store to obtain state via {@link Store#getState()}
     * <li> Dispatch one or more events to Store via {@link Store#dispatch(Object)}
     * </ul>
     *
     * @param store          original store object
     * @param action         action to dispatch
     * @param nextDispatcher callback to be called by middleware to proceed with dispatch chain
     */
    void dispatch(Store<State> store, Object action, NextDispatcher nextDispatcher);

    /**
     * Received as argument in {@link Middleware#dispatch(Store, Object, NextDispatcher)}
     */
    interface NextDispatcher {
        void call(Object action);
    }
}
