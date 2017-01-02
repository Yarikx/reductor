package com.yheriatovych.reductor.observable;

import com.yheriatovych.reductor.*;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Middleware that runs provided {@link Epic} after {@link Store} created
 * and dispatches actions produced by Epic back to the Store.
 *
 * @param <T> state type of {@link Store}
 */
public class EpicMiddleware<T> implements Middleware<T>, Subscription {

    private final Epic<T> epic;
    private Subscription subscription;

    private EpicMiddleware(Epic<T> rootEpic) {
        this.epic = rootEpic;
    }

    /**
     * Factory method to create EpicMiddleware.
     * <p>
     * This method takes only one epic.
     * However, several epics can be combined with {@link Epics#combineEpics(Iterable)}.
     *
     * @param rootEpic epic to run once store is created
     * @param <T>      state type of {@link Store}
     * @return instance of EpicMiddleware to be passed to {@link Store#create(Reducer, Middleware[])}
     */
    public static <T> EpicMiddleware<T> create(Epic<T> rootEpic) {
        return new EpicMiddleware<>(rootEpic);
    }

    @Override
    public Dispatcher create(Store<T> store, Dispatcher nextDispatcher) {
        PublishSubject<Action> actions = PublishSubject.create();
        subscription = epic.run(actions, store).subscribe(store::dispatch);
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
