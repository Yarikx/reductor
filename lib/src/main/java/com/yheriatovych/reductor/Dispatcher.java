package com.yheriatovych.reductor;

/**
 * Interface that represents component that can dispatch events (most likely to {@link Store}).
 * <p>
 * Dispatcher is used to customize (or wrap) dispatched events before dispatching them to Store.
 * Customization can be applied globally (via {@link Middleware})
 * or ad-hoc (by just wrapping other Dispatcher (or Store) with custom implementation).
 * Note: {@link Store} implements Dispatcher
 */
public interface Dispatcher {
    void dispatch(Object action);
}
