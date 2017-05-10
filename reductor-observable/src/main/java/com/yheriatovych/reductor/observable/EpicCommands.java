package com.yheriatovych.reductor.observable;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.Commands;
import com.yheriatovych.reductor.Store;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class EpicCommands<T> implements Commands<T>, Disposable {

    private List<Observable<Object>> tasks;
    private Disposable disposable;

    private EpicCommands(List<Observable<Object>> tasks) {this.tasks = tasks; }

    public static <T> EpicCommands<T> create(List<Observable<Object>> list) {
        return new EpicCommands<>(list);
    }

    public static <T> EpicCommands<T> create(Observable<Object> task) {
        return create(Arrays.asList(task));
    }

    @Override
    public void run(Store<T> store) {
        if (tasks == null) return;

        // TODO check this
        disposable = Observable.fromIterable(tasks).flatMap(obs -> obs).subscribe(action -> {
            if (action instanceof Action) {
                store.dispatch(action);
            }
        });
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
