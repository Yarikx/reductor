package com.yheriatovych.reductor.rxjava2;

import com.yheriatovych.reductor.Cancelable;
import com.yheriatovych.reductor.Store;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

public final class RxStore {

    /**
     * Create observable of state changes from specified {@link Store}
     * <p>
     * Note: This method will emit current sate immediately after subscribe
     */
    public static <State> Observable<State> asObservable(final Store<State> store) {
        return Observable.create(emitter -> {
            emitter.onNext(store.getState());
            final Cancelable cancelable = store.subscribe(emitter::onNext);
            emitter.setCancellable(cancelable::cancel);
        });
    }

    /**
     * Create flowable of state changes from specified {@link Store}
     * <p>
     * Note: This method will emit current sate immediately after subscribe
     */
    public static <State> Flowable<State> asFlowable(final Store<State> store) {
        return Flowable.create(emitter -> {
            emitter.onNext(store.getState());
            final Cancelable cancelable = store.subscribe(emitter::onNext);
            emitter.setCancellable(cancelable::cancel);
        }, BackpressureStrategy.LATEST);
    }
}
