package com.colorhaake.traveler.reducer.home;

import android.content.Context;
import android.util.Log;

import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.network.NetworkApi;
import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.plain_object.Response;
import com.yheriatovych.reductor.Actions;
import com.yheriatovych.reductor.Commands;
import com.yheriatovych.reductor.Pair;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.observable.EpicCommands;

import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.rx.ObservableFactory;

/**
 * Created by josephcheng on 2017/3/25.
 */

@AutoReducer
public abstract class HomeReducer implements Reducer<AppState> {

    private HomeActions homeActions = Actions.from(HomeActions.class);

    @AutoReducer.Action(
            value = HomeActions.VIEW_READY,
            from = HomeActions.class
    )
    public Pair<AppState, Commands> viewReady(AppState state, Context context) {
        return Pair.create(state, EpicCommands.create((
                ObservableFactory.from(
                        SmartLocation.with(context).location()
                )
                .flatMap(location -> NetworkApi.fetchHomeData(
                        location.getLatitude(),
                        location.getLongitude(),
                        System.currentTimeMillis() / 1000
                ))
                .map(payload -> (Object) homeActions.fetchHomeDataRes(payload))
        )));
    }

    @AutoReducer.Action(
            value = HomeActions.FETCH_HOME_DATA_RES,
            from = HomeActions.class
    )
    public Pair<AppState, Commands> fetchHomeDataRes(AppState state, Response<HomeData> res) {
        if (!res.error.code.isEmpty()) {
            return Pair.create(state);
        }
        // TODO do normalization
        return Pair.create(state.withResponseHomeData(res));
    }

    public static HomeReducer create() {
        return new HomeReducerImpl();
    }
}
