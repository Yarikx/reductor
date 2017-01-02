package com.yheriatovych.reductor.observable;

import com.yheriatovych.reductor.Action;
import rx.Observable;
import rx.functions.Func1;

public class Epics {
    public static <T> Epic<T> combineEpics(Iterable<Epic<T>> epics) {
        return (store, actions) -> Observable.from(epics)
                .flatMap(epic -> epic.run(store, actions));
    }

    public static Func1<Action, Boolean> ofType(String type) {
        return action -> type.equals(action.type);
    }
}
