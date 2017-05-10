package com.yheriatovych.reductor;

/**
 * In Elm, commands (Cmd) are how we tell the runtime to execute things that involve side effects.
 * For example:
 * <ul>
 * <li> Generate a random number
 * <li> Make an http request
 * <li> Save something into local storage
 * </ul>
 * A Cmd can be one or a collection of things to do.
 * We use commands to gather all the things that need to happen and hand them to the runtime.
 * Then the runtime will execute them and feed the results back to the application.
 */
public interface Commands<State> {
    /**
     * Create and initialize Commands.
     * Called only once during {@link Store} creation.
     *
     * @return void
     */
    void run(Store<State> store);
}
