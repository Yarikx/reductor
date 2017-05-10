package com.yheriatovych.reductor.observable;

import com.yheriatovych.reductor.Action;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;

public class Epics {
    private Epics() {
    }

    /**
     * Combine several Epics into one.
     * <p>
     * Returned streams will be merged with {@link Observable#merge(Iterable)}
     *
     * @param epics Epics to combine
     * @param <T>   state type
     * @return Epic that will combine all provided epics behaviour
     */
    public static <T> Epic<T> combineEpics(Iterable<Epic<T>> epics) {
        return (actions, store) -> Observable.fromIterable(epics)
                .flatMap(epic -> epic.run(actions, store));
    }

    /**
     * Useful predicate to be used with {@link Observable#filter(Predicate)} in Epic implementation
     * to filter only actions with specific {@link Action#type}
     *
     * @param type Action type filtered
     * @return Predicate that will check if {@link Action#type} equals to specified type
     */
    public static Predicate<Action> ofType(String type) {
        return action -> type.equals(action.type);
    }
}
