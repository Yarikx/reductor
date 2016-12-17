package com.yheriatovych.reductor;

public interface Function<T, R> {
    R apply(T value);
}
