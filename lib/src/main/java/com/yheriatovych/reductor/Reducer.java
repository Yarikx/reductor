package com.yheriatovych.reductor;

/**
 * Pure function to produce (reduce) state with given actions
 * <p>
 * (previousState, action) =&gt; newState
 */
public interface Reducer<State> {
    /**
     * Produce new state based on current state and action to dispatch.
     * <p>
     * Reducer is responsible to populate initial state if null is passed as 'state' argument
     * <p>
     * Note:
     * This function should be pure. No side effects, no API calls!
     *
     * @param state  state to be reduced
     * @param action minimum representation of change to be performed on state
     * @return a new state
     */
    State reduce(State state, Action action);
}
