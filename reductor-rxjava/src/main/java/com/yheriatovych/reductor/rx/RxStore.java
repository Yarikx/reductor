package com.yheriatovych.reductor.rx;

import com.yheriatovych.reductor.Cancelable;
import com.yheriatovych.reductor.Store;
import rx.Emitter;
import rx.Observable;

public final class RxStore {

    /**
     * Create observable of state changes from specified {@link Store}
     * <p>
     * Note: This method will emit current sate immediately after subscribe
     */
    public static <State> Observable<State> asObservable(final Store<State> store) {
        return Observable.fromEmitter(emitter -> {
            emitter.onNext(store.getState());
            final Cancelable cancelable = store.subscribe(emitter::onNext);
            emitter.setCancellation(cancelable::cancel);
        }, Emitter.BackpressureMode.LATEST);
    }
}
