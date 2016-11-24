package com.yheriatovych.reductor;

/**
 * Middleware provides a third-party extension point between
 * dispatching an action, and the moment it reaches the reducer
 * <p>
 * Can be useful to support actions different from {@link Action} or to add logging or crash reporting
 * <p>
 * Implementation of Middleware may:
 * <ul>
 * <li> Call {@code nextDispatcher.call(action)} to proceed with dispatch chain
 * <li> Call {@code nextDispatcher.call(action)} with potentially different action to proceed with dispatch chain
 * <li> Not call {@code nextDispatcher.call(action)} to discard action
 * <li> Use store to obtain state via {@link Store#getState()}
 * <li> Dispatch one or more events to Store via {@link Store#dispatch(Object)} to dispatch through full middleware chain
 * </ul>
 */
public interface Middleware<State> {
    /**
     * Create and initialize Dispatcher.
     * Called only once during {@link Store} creation.
     *
     * @param store          original store object
     * @param nextDispatcher callback to be called by middleware to proceed with dispatch chain
     * @return Dispatcher functional interface which will be called for each action dispatched to Store
     */
    Dispatcher create(Store<State> store, Dispatcher nextDispatcher);

    /**
     * Middleware part which is invoked for each action dispatched to Store.
     * To proceed with the action and deliver it to the Store,
     * dispatcher may invoke {@code nextDispatcher} passed from {@link #create(Store, Dispatcher)}
     */
    interface Dispatcher {
        void call(Object action);
    }
}
