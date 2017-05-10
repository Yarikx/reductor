package com.yheriatovych.reductor.annotations;

import com.yheriatovych.reductor.Reducer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotate interface which will be used as container to combine few sub-states into single object
 * <p>
 * Annotated interface should only have accessor methods (methods with return type without arguments)
 * <p>
 * Two classes will be generated:
 * <ul>
 * <li>1. Simple implementation of this interface with the same name + "Impl" suffix
 * <li>2. Implementation of {@link Reducer} for this class which holds sub-reducers to
 * reduce all sub-states into single object
 * </ul>
 * <p>
 * Example:
 * <pre><code>
 * &#64;CombinedState
 * interface Person {
 *     String name();
 *     int age();
 * }
 * </code></pre>
 */
@Target(ElementType.TYPE)
public @interface CombinedState {
}
