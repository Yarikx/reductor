package com.yheriatovych.reductor.rxjava2;

import com.yheriatovych.reductor.Cancelable;
import com.yheriatovych.reductor.Cursor;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

public final class RxStore {

    /**
     * Create observable of state changes from specified {@link Cursor}
     * <p>
     * Note: This method will emit current sate immediately after subscribe
     */
    public static <State> Observable<State> asObservable(final Cursor<State> cursor) {
        return Observable.create(emitter -> {
            emitter.onNext(cursor.getState());
            final Cancelable cancelable = cursor.subscribe(emitter::onNext);
            emitter.setCancellable(cancelable::cancel);
        });
    }

    /**
     * Create flowable of state changes from specified {@link Cursor}
     * <p>
     * Note: This method will emit current sate immediately after subscribe
     */
    public static <State> Flowable<State> asFlowable(final Cursor<State> cursor) {
        return Flowable.create(emitter -> {
            emitter.onNext(cursor.getState());
            final Cancelable cancelable = cursor.subscribe(emitter::onNext);
            emitter.setCancellable(cancelable::cancel);
        }, BackpressureStrategy.LATEST);
    }
}
