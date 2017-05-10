package com.colorhaake.traveler.reducer;

import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.reducer.home.HomeAsyncActions;
import com.yheriatovych.reductor.observable.Epic;
import com.yheriatovych.reductor.observable.EpicMiddleware;
import com.yheriatovych.reductor.observable.Epics;

import java.util.Arrays;
import java.util.List;

/**
 * Created by josephcheng on 2017/3/12.
 */
public class ReductorAsync {
    public static List<Epic<AppState>> asyncActions = Arrays.asList(
            HomeAsyncActions.viewReady
    );
    public static EpicMiddleware<AppState> middleware = EpicMiddleware.create(
            Epics.combineEpics(asyncActions)
    );
}
