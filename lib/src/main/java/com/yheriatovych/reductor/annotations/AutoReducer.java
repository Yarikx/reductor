package com.yheriatovych.reductor.annotations;

import com.yheriatovych.reductor.Reducer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotate Implementation of {@link Reducer} to generate child class with generated
 * {@link Reducer#reduce(Object, com.yheriatovych.reductor.Action)} method based on existed handler methods
 * per each {@link com.yheriatovych.reductor.Action#type}
 * <p>
 * Generated class will have the same class name but with "Impl" suffix
 * <p>
 * Example:
 * <pre><code>
 * &#64;AutoReducer
 * abstract class NumberReducer implements Reducer&lt;Integer&gt; {
 *     &#64;AutoReducer.Action("NUMBER_ADD")
 *     public Integer add(Integer state, int number) {
 *         return state + number;
 *     }
 *
 *     &#64;AutoReducer.Action("NUMBER_SUB")
 *     public Integer sub(Integer state, int number) {
 *         return state - number;
 *     }
 *
 *     public static NumberReducer create() {
 *         return new NumberReducerImpl();
 *     }
 * }
 * </code></pre>
 */
@Target(ElementType.TYPE)
public @interface AutoReducer {

    /**
     * Annotation to mark particular method in class annotated with {@link AutoReducer} as action handler
     * <p>
     * Note: annotated method should conform to next rules
     * <ul>
     * <li> Return 'next' state
     * <li> Take 'current' state as first argument
     * <li> can have additional arguments as action values
     * (they will be bundled an passed in {@link com.yheriatovych.reductor.Action#values} automatically)
     * </ul>
     */
    @Target(ElementType.METHOD)
    @interface Action {
        /**
         * @return String type which will be {@link com.yheriatovych.reductor.Action#type}
         */
        String value();

        /**
         * Action creator interface, where corresponding action creator can be found.
         * This information is used to validate arguments types and order to validate correspondence.
         *
         * @return class object of interface with action creators
         * @see ActionCreator
         */
        Class<?> from() default Void.class;

        /**
         * If true corresponding action creator interface will be generated
         * <p>
         * Generated action creator interface will have class name the same as reducer with 'Actions' suffix.
         * Note that for most of cases explicitly defined Action creators are preferred.
         *
         * @return true if action creator should be generated.
         * @see ActionCreator
         */
        boolean generateActionCreator() default false;
    }

    /**
     * Optional annotation to mark method as initial state creator.
     * <p>
     * If null is passed as a 'state' to {@link Reducer#reduce(Object, com.yheriatovych.reductor.Action)}
     * method, annotated method will be called to generate state, before dispatching it to appropriate action handler.
     * <p>
     * Example:
     * <pre><code>
     * &#64;AutoReducer
     * abstract class NumberReducer implements Reducer&lt;Integer&gt; {
     *     &#64;AutoReducer.InitialState
     *     public Integer init() {
     *         return 0;
     *     }
     *
     *     &#64;AutoReducer.Action("NUMBER_ADD")
     *     public Integer add(Integer state, int number) {
     *         return state + number;
     *     }
     * }
     * </code></pre>
     */
    @Target(ElementType.METHOD)
    @interface InitialState {
    }
}
