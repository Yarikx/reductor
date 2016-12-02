package com.yheriatovych.reductor;

/**
 * Middleware provides a third-party extension point between
 * dispatching an action, and the moment it reaches the reducer
 * <p>
 * Can be useful to support actions different from {@link Action} or to add logging or crash reporting
 * <p>
 * Implementation of Middleware may:
 * <ul>
 * <li> Call {@code nextDispatcher.dispatch(action)} to proceed with dispatch chain
 * <li> Call {@code nextDispatcher.dispatch(action)} with potentially different action to proceed with dispatch chain
 * <li> Not dispatch {@code nextDispatcher.dispatch(action)} to discard action
 * <li> Use store to obtain state via {@link Store#getState()}
 * <li> Dispatch one or more events to Store via {@link Store#dispatch(Object)} to dispatch through full middleware chain
 * </ul>
 * <p>
 * Logging middleware example:
 * <pre><code>
 * public class LoggingMiddleware&lt;T&gt; implements Middleware&lt;T&gt; {
 *     &#64;Override
 *     public Dispatcher create(Store&lt;T&gt; store, Dispatcher nextDispatcher) {
 *         return action -&gt; {
 *             log("dispatching action: " + action);
 *             nextDispatcher.dispatch(action);
 *             log("new state: " + store.getState());
 *         };
 *     }
 * }
 * </code></pre>
 */
public interface Middleware<State> {
    /**
     * Create and initialize Dispatcher.
     * Called only once during {@link Store} creation.
     * <p>
     * {@code nextDispatcher} is intended to be used by returned Dispatcher
     * to proceed with dispatch chain
     *
     * @param store          original store object
     * @param nextDispatcher callback to be called by middleware to proceed with dispatch chain
     * @return Dispatcher functional interface which will be called for each action dispatched to Store
     */
    Dispatcher create(Store<State> store, Dispatcher nextDispatcher);

}
