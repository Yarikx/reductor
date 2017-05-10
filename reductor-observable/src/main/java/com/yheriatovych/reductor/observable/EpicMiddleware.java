package com.yheriatovych.reductor.observable;

import com.yheriatovych.reductor.*;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Middleware that runs provided {@link Epic} after {@link Store} created
 * and dispatches actions produced by Epic back to the Store.
 *
 * @param <T> state type of {@link Store}
 */
public class EpicMiddleware<T> implements Middleware<T>, Disposable {

    private final Epic<T> epic;
    private Disposable disposable;

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
        disposable = epic.run(actions, store).subscribe(store::dispatch);
        return action -> {
            nextDispatcher.dispatch(action);
            if(action instanceof Action) {
                actions.onNext((Action) action);
            }
        };
    }

    @Override
    public void dispose() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public boolean isDisposed() {
        return disposable != null && disposable.isDisposed();
    }
}
