package com.yheriatovych.reductor.observable;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Store;
import rx.Observable;

public interface Epic<T> {
    Observable<Object> run(Store<T> store, Observable<Action> actions);
}
