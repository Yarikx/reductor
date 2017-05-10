package com.colorhaake.traveler.injection.component;

import com.colorhaake.traveler.view.main.MainActivity;
import com.colorhaake.traveler.injection.PerActivity;
import com.colorhaake.traveler.injection.module.ActivityModule;
import com.colorhaake.traveler.reducer.home.HomeActions;
import com.colorhaake.traveler.view.home.ActivityHeader;
import com.colorhaake.traveler.view.main.MainPresenter;

import dagger.Component;

/**
 * Created by colorhaake on 2017/3/30.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity mainActivity);
    HomeActions getHomeActions();
    ActivityHeader getActivityHeader();
    MainPresenter getPresenter();
}
