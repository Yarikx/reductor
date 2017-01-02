package com.yheriatovych.reductor.observable;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Dispatcher;
import com.yheriatovych.reductor.Middleware;
import com.yheriatovych.reductor.Store;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class EpicMiddleware<T> implements Middleware<T>, Subscription {

    private final Epic<T> epic;
    private Subscription subscription;

    public EpicMiddleware(Epic<T> rootEpic) {
        this.epic = rootEpic;
    }

    public static <T> EpicMiddleware<T> create(Epic<T> rootEpic) {
        return new EpicMiddleware<>(rootEpic);
    }

    @Override
    public Dispatcher create(Store<T> store, Dispatcher nextDispatcher) {
        PublishSubject<Action> actions = PublishSubject.create();
        subscription = epic.run(store, actions).subscribe(store::dispatch);
        return action -> {
            nextDispatcher.dispatch(action);
            if(action instanceof Action) {
                actions.onNext((Action) action);
            }
        };
    }

    @Override
    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public boolean isUnsubscribed() {
        return subscription != null && subscription.isUnsubscribed();
    }
}
