package com.yheriatovych.reductor.example;

import org.pcollections.PStack;

public interface Utils {
    interface Predicate<T> {
        boolean call(T value);
    }

    static <T> PStack<T> filter(PStack<T> xs, Predicate<T> predicate) {
        if (xs.isEmpty()) return xs;
        else {
            T head = xs.get(0);
            PStack<T> tail = xs.subList(1);
            if (predicate.call(head)) {
                return filter(tail, predicate).plus(head);
            } else {
                return filter(tail, predicate);
            }
        }
    }

}
