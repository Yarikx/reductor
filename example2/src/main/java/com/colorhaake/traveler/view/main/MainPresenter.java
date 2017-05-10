package com.colorhaake.traveler.view.main;

import android.app.Activity;
import android.util.Log;

import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.plain_object.ActivityGroup;
import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.reducer.home.HomeActions;
import com.colorhaake.traveler.view.base.BasePresenter;
import com.yheriatovych.reductor.Store;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by colorhaake on 2017/3/30.
 */

public class MainPresenter extends BasePresenter<MainMvpView> {

    private final Observable<AppState> state;
    private final Store<AppState> store;
    private final HomeActions actions;

    public MainPresenter(
            Observable<AppState> state,
            Store<AppState> store,
            HomeActions actions
    ) {

        this.state = state;
        this.store = store;
        this.actions = actions;

        this.state.subscribe(newState -> {
            HomeData headerInfo = newState.homeData().result;
            if (headerInfo == null) return;

            getMvpView().updateHeaderView(headerInfo);

            List<ActivityGroup> groups = headerInfo.groups;
            Collections.sort(groups, (o1, o2) -> Integer.valueOf(o1.type) - Integer.valueOf(o2.type));
            getMvpView().showHomePage(groups);
        });
    }

    public void viewReady(Activity activity) {
        store.dispatch(actions.viewReady(activity));
    }
}
