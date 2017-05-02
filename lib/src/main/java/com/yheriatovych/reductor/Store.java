package com.yheriatovych.reductor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * State container which dispatches actions with provided reducer
 * <p>
 * Note: Having immutable state is strongly recommended
 *
 * @param <State> type of state to be stored and manipulated
 */
public class Store<State> implements Dispatcher, Cursor<State> {
    public static final String INIT_ACTION = "@@reductor/INIT";

    private final Reducer<State> reducer;
    private final Dispatcher dispatcher;
    private final List<StateChangeListener<State>> listeners = new CopyOnWriteArrayList<>();
    private volatile State state;

    private Store(Reducer<State> reducer, State initialState, Middleware<State>[] middlewares) {
        this.reducer = reducer;
        this.state = initialState;

        Dispatcher dispatcher = this::dispatchAction;
        for (int i = middlewares.length - 1; i >= 0; i--) {
            Middleware<State> middleware = middlewares[i];
            dispatcher = middleware.create(Store.this, dispatcher);
    }
        this.dispatcher = dispatcher;
        dispatchAction(Action.create(INIT_ACTION));
    }

    private void dispatchAction(final Object actionObject) {
        if (actionObject instanceof Action) {
            final Action action = (Action) actionObject;
            synchronized (this) {
                Pair<State, Commands> pair = reducer.reduce(state, action);
                state = pair.first;
                Commands cmd = pair.second;
                if (cmd != null) {
                    cmd.run(Store.this);
                }
            }
            // TODO change to rxStore
            for (StateChangeListener<State> listener : listeners) {
                listener.onStateChanged(state);
            }
        } else {
            throw new IllegalArgumentException(String.format("action %s of %s is not instance of %s, use custom Middleware to dispatch another types of actions", actionObject, actionObject.getClass(), Action.class));
        }
    }

    /**
     * Create store with given {@link Reducer} and optional array of {@link Middleware}
     *
     * @param reducer     Reducer of type S which will be used to dispatch actions
     * @param middlewares array of middlewares to be used to dispatch actions in the same order as provided
     *                    look {@link Middleware} for more information
     * @param <S>         type of state to hold and maintain
     * @return Store initialised with initialState
     */
    @SafeVarargs
    public static <S> Store<S> create(Reducer<S> reducer, Middleware<S>... middlewares) {
        return create(reducer, null, middlewares);
    }

    /**
     * Create store with given {@link Reducer}, initalState and optional array of {@link Middleware}
     *
     * @param reducer      Reducer of type S which will be used to dispatch actions
     * @param initialState state to be initial state of create store
     * @param middlewares  array of middlewares to be used to dispatch actions in the same order as provided
     *                     look {@link Middleware} for more information
     * @param <S>          type of state to hold and maintain
     * @return Store initialised with initialState
     */
    @SafeVarargs
    public static <S> Store<S> create(Reducer<S> reducer, S initialState, Middleware<S>... middlewares) {
        return new Store<>(reducer, initialState, middlewares);
    }

    /**
     * Dispatch action through {@link Reducer} and store the next state
     *
     * @param action action to be dispatched, usually instance of {@link Action}
     *               but custom {@link Middleware} can be used to support other types of actions
     */
    public void dispatch(final Object action) {
        dispatcher.dispatch(action);
    }

    /**
     * {@inheritDoc}
     */
    public State getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    public Cancelable subscribe(final StateChangeListener<State> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }
}
