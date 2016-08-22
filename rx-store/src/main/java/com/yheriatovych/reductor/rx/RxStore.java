package com.yheriatovych.reductor.rx;

import com.yheriatovych.reductor.Cancelable;
import com.yheriatovych.reductor.Store;
import rx.AsyncEmitter;
import rx.Observable;

public final class RxStore {

    /**
     * Create observable of state changes from specified {@link Store}
     * <p>
     * Note: This method will emit current sate immediately after subscribe
     */
    public static <State> Observable<State> asObservable(final Store<State> store) {
        return Observable.fromAsync(stateAsyncEmitter -> {
            stateAsyncEmitter.onNext(store.getState());
            final Cancelable cancelable = store.subscribe(stateAsyncEmitter::onNext);
            stateAsyncEmitter.setCancellation(cancelable::cancel);
        }, AsyncEmitter.BackpressureMode.LATEST);
    }
}
