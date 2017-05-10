package com.colorhaake.traveler.injection.module;

import android.app.Application;
import android.content.Context;

import com.colorhaake.traveler.injection.ApplicationContext;
import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.model.AppStateReducer;
import com.colorhaake.traveler.plain_object.Response;
import com.colorhaake.traveler.reducer.home.HomeReducer;
import com.colorhaake.traveler.reducer.utils.SetStateReducer;
import com.colorhaake.traveler.reducer.utils.UndoableReducer;
import com.colorhaake.traveler.util.MyAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yheriatovych.reductor.Store;
import com.yheriatovych.reductor.rxjava2.RxStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by colorhaake on 2017/3/30.
 */

@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application getApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    AppStateReducer provideReducer() {
        return AppStateReducer.builder()
                .homeDataReducer(HomeReducer.create())
                .build();
    }

    @Singleton
    @Provides
    Store<AppState> provideStore(AppStateReducer reducer) {
        return Store.create(
                new SetStateReducer<>(new UndoableReducer<>(reducer)),
                AppState.builder().setHomeData(new Response<>()).build()
        );
    }

    @Provides
    Observable<AppState> provideState(Store<AppState> store) {
        return RxStore.asObservable(store)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Provides
    Gson provideoGson() {
        return new GsonBuilder()
            .registerTypeAdapterFactory(MyAdapterFactory.create())
            .create();
    }
}
