package com.colorhaake.traveler.injection.module;

import android.app.Activity;
import android.content.Context;

import com.colorhaake.traveler.injection.ActivityContext;
import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.reducer.home.HomeActions;
import com.colorhaake.traveler.view.home.ActivityHeader;
import com.colorhaake.traveler.view.main.MainPresenter;
import com.yheriatovych.reductor.Actions;
import com.yheriatovych.reductor.Store;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;

/**
 * Created by colorhaake on 2017/3/30.
 */

@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() { return mActivity; }

    @Provides
    Activity provideActivity() { return mActivity; }

    @Provides
    HomeActions provideHomeActions() { return Actions.from(HomeActions.class); }

    @Provides
    ActivityHeader provideActivityHeader(Activity activity) {
        return ActivityHeader.getInstance(activity);
    }

    @Provides
    MainPresenter providePresenter(
            Observable<AppState> state, Store<AppState> store, HomeActions actions
    ) {
        return new MainPresenter(state, store, actions);
    }
}
