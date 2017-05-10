package com.colorhaake.traveler.reducer.home;

import android.util.Log;

import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.network.NetworkApi;
import com.yheriatovych.reductor.Actions;
import com.yheriatovych.reductor.observable.Epic;
import com.yheriatovych.reductor.observable.Epics;

import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.rx.ObservableFactory;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by josephcheng on 2017/3/12.
 */
public class HomeAsyncActions {
    public static final String TAG = NetworkApi.class.getName();
    static HomeActions homeActions = Actions.from(HomeActions.class);
    public static Epic<AppState> viewReady = (actions, store) ->
        actions.filter(Epics.ofType(HomeActions.VIEW_READY))
            .flatMap(action -> ObservableFactory.from(
                    SmartLocation.with(action.getValue(0)).location()
            ))
            .flatMap(location -> NetworkApi.fetchHomeData(
                    location.getLatitude(),
                    location.getLongitude(),
                    System.currentTimeMillis() / 1000
            ))
            .map(payload -> (Object) homeActions.fetchHomeDataRes(payload))
            .observeOn(AndroidSchedulers.mainThread());
}
