package com.colorhaake.traveler.injection.component;

import android.app.Application;
import android.content.Context;

import com.colorhaake.traveler.TravelerApp;
import com.colorhaake.traveler.injection.ApplicationContext;
import com.colorhaake.traveler.injection.module.ApplicationModule;
import com.colorhaake.traveler.model.AppState;
import com.google.gson.Gson;
import com.yheriatovych.reductor.Store;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.Observable;

/**
 * Created by colorhaake on 2017/3/30.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(TravelerApp app);

    @ApplicationContext Context getContext();

    Application getApplication();

    Store<AppState> getStore();

    Observable<AppState> getState();

    Gson getGson();
}
