package com.colorhaake.traveler.injection.component;

import android.app.Activity;
import com.colorhaake.traveler.injection.module.ActivityModule;
import com.colorhaake.traveler.injection.module.ActivityModule_ProvideActivityFactory;
import com.colorhaake.traveler.injection.module.ActivityModule_ProvideActivityHeaderFactory;
import com.colorhaake.traveler.injection.module.ActivityModule_ProvideHomeActionsFactory;
import com.colorhaake.traveler.injection.module.ActivityModule_ProvidePresenterFactory;
import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.reducer.home.HomeActions;
import com.colorhaake.traveler.view.home.ActivityHeader;
import com.colorhaake.traveler.view.main.MainActivity;
import com.colorhaake.traveler.view.main.MainActivity_MembersInjector;
import com.colorhaake.traveler.view.main.MainPresenter;
import com.yheriatovych.reductor.Store;
import dagger.MembersInjector;
import dagger.internal.Preconditions;
import io.reactivex.Observable;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerActivityComponent implements ActivityComponent {
  private Provider<Observable<AppState>> getStateProvider;

  private Provider<Store<AppState>> getStoreProvider;

  private Provider<HomeActions> provideHomeActionsProvider;

  private Provider<MainPresenter> providePresenterProvider;

  private Provider<Activity> provideActivityProvider;

  private Provider<ActivityHeader> provideActivityHeaderProvider;

  private MembersInjector<MainActivity> mainActivityMembersInjector;

  private DaggerActivityComponent(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.getStateProvider =
        new com_colorhaake_traveler_injection_component_ApplicationComponent_getState(
            builder.applicationComponent);

    this.getStoreProvider =
        new com_colorhaake_traveler_injection_component_ApplicationComponent_getStore(
            builder.applicationComponent);

    this.provideHomeActionsProvider =
        ActivityModule_ProvideHomeActionsFactory.create(builder.activityModule);

    this.providePresenterProvider =
        ActivityModule_ProvidePresenterFactory.create(
            builder.activityModule, getStateProvider, getStoreProvider, provideHomeActionsProvider);

    this.provideActivityProvider =
        ActivityModule_ProvideActivityFactory.create(builder.activityModule);

    this.provideActivityHeaderProvider =
        ActivityModule_ProvideActivityHeaderFactory.create(
            builder.activityModule, provideActivityProvider);

    this.mainActivityMembersInjector =
        MainActivity_MembersInjector.create(
            providePresenterProvider, provideActivityHeaderProvider);
  }

  @Override
  public void inject(MainActivity mainActivity) {
    mainActivityMembersInjector.injectMembers(mainActivity);
  }

  @Override
  public HomeActions getHomeActions() {
    return provideHomeActionsProvider.get();
  }

  @Override
  public ActivityHeader getActivityHeader() {
    return provideActivityHeaderProvider.get();
  }

  @Override
  public MainPresenter getPresenter() {
    return providePresenterProvider.get();
  }

  public static final class Builder {
    private ActivityModule activityModule;

    private ApplicationComponent applicationComponent;

    private Builder() {}

    public ActivityComponent build() {
      if (activityModule == null) {
        throw new IllegalStateException(ActivityModule.class.getCanonicalName() + " must be set");
      }
      if (applicationComponent == null) {
        throw new IllegalStateException(
            ApplicationComponent.class.getCanonicalName() + " must be set");
      }
      return new DaggerActivityComponent(this);
    }

    public Builder activityModule(ActivityModule activityModule) {
      this.activityModule = Preconditions.checkNotNull(activityModule);
      return this;
    }

    public Builder applicationComponent(ApplicationComponent applicationComponent) {
      this.applicationComponent = Preconditions.checkNotNull(applicationComponent);
      return this;
    }
  }

  private static class com_colorhaake_traveler_injection_component_ApplicationComponent_getState
      implements Provider<Observable<AppState>> {
    private final ApplicationComponent applicationComponent;

    com_colorhaake_traveler_injection_component_ApplicationComponent_getState(
        ApplicationComponent applicationComponent) {
      this.applicationComponent = applicationComponent;
    }

    @Override
    public Observable<AppState> get() {
      return Preconditions.checkNotNull(
          applicationComponent.getState(),
          "Cannot return null from a non-@Nullable component method");
    }
  }

  private static class com_colorhaake_traveler_injection_component_ApplicationComponent_getStore
      implements Provider<Store<AppState>> {
    private final ApplicationComponent applicationComponent;

    com_colorhaake_traveler_injection_component_ApplicationComponent_getStore(
        ApplicationComponent applicationComponent) {
      this.applicationComponent = applicationComponent;
    }

    @Override
    public Store<AppState> get() {
      return Preconditions.checkNotNull(
          applicationComponent.getStore(),
          "Cannot return null from a non-@Nullable component method");
    }
  }
}
