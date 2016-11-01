package com.yheriatovych.reductor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate interface with actions creators.
 * <p>
 * Action creators are functions which return {@link com.yheriatovych.reductor.Action} and annotated with {@link ActionCreator.Action}
 * The implementation of such interface can be obtained by {@link com.yheriatovych.reductor.Actions#from(Class)}
 * <p>
 * Example:
 * <pre><code>
 * &#64;ActionCreator
 * interface CounterActions {
 *     String ACTION_ADD = "ACTION_ADD";
 *     String ACTION_INCREMENT = "ACTION_INCREMENT";
 *
 *     &#64;ActionCreator.Action(ACTION_ADD)
 *     Action add(int valueToAdd);
 *
 *     &#64;ActionCreator.Action(ACTION_INCREMENT)
 *     Action increment();
 * }
 * </code></pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionCreator {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Action {
        String value();
    }
}
