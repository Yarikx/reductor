package com.yheriatovych.reductor.observable;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Store;

import io.reactivex.Observable;

/**
 * Core primitive to process and dispatch actions asynchronously
 * It is a function which takes a stream of actions and returns a stream of actions.
 * <p>
 * Actions emitted by returning stream will be dispatched back to {@link Store}
 * <p>
 * Ping-Pong example:
 * <pre><code>
 * Epic&lt;String&gt; pingPong = (actions, store) -&gt;
 *         actions.filter(Epics.ofType("PING"))
 *                 .delay(1, TimeUnit.SECONDS)
 *                 .map(action -&gt; Action.create("PONG"));
 * </code></pre>
 *
 * @param <T> state type of {@link Store}
 */
public interface Epic<T> {
    /**
     * Functions that will be called by {@link Store} once it's created.
     *
     * @param actions an Observable of actions dispatched to the Store
     * @param store   a Store object that Epic can use to query the state
     * @return an Observable of actions that will be dispatched back to Store
     */
    Observable<Object> run(Observable<Action> actions, Store<T> store);
}
