package com.colorhaake.traveler.injection.component;

import android.app.Application;
import android.content.Context;
import com.colorhaake.traveler.TravelerApp;
import com.colorhaake.traveler.TravelerApp_MembersInjector;
import com.colorhaake.traveler.injection.module.ApplicationModule;
import com.colorhaake.traveler.injection.module.ApplicationModule_GetApplicationFactory;
import com.colorhaake.traveler.injection.module.ApplicationModule_ProvideContextFactory;
import com.colorhaake.traveler.injection.module.ApplicationModule_ProvideReducerFactory;
import com.colorhaake.traveler.injection.module.ApplicationModule_ProvideStateFactory;
import com.colorhaake.traveler.injection.module.ApplicationModule_ProvideStoreFactory;
import com.colorhaake.traveler.injection.module.ApplicationModule_ProvideoGsonFactory;
import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.model.AppStateReducer;
import com.google.gson.Gson;
import com.yheriatovych.reductor.Store;
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import io.reactivex.Observable;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerApplicationComponent implements ApplicationComponent {
  private Provider<AppStateReducer> provideReducerProvider;

  private Provider<Store<AppState>> provideStoreProvider;

  private Provider<Gson> provideoGsonProvider;

  private MembersInjector<TravelerApp> travelerAppMembersInjector;

  private Provider<Context> provideContextProvider;

  private Provider<Application> getApplicationProvider;

  private Provider<Observable<AppState>> provideStateProvider;

  private DaggerApplicationComponent(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.provideReducerProvider =
        ApplicationModule_ProvideReducerFactory.create(builder.applicationModule);

    this.provideStoreProvider =
        DoubleCheck.provider(
            ApplicationModule_ProvideStoreFactory.create(
                builder.applicationModule, provideReducerProvider));

    this.provideoGsonProvider =
        ApplicationModule_ProvideoGsonFactory.create(builder.applicationModule);

    this.travelerAppMembersInjector =
        TravelerApp_MembersInjector.create(provideStoreProvider, provideoGsonProvider);

    this.provideContextProvider =
        ApplicationModule_ProvideContextFactory.create(builder.applicationModule);

    this.getApplicationProvider =
        ApplicationModule_GetApplicationFactory.create(builder.applicationModule);

    this.provideStateProvider =
        ApplicationModule_ProvideStateFactory.create(
            builder.applicationModule, provideStoreProvider);
  }

  @Override
  public void inject(TravelerApp app) {
    travelerAppMembersInjector.injectMembers(app);
  }

  @Override
  public Context getContext() {
    return provideContextProvider.get();
  }

  @Override
  public Application getApplication() {
    return getApplicationProvider.get();
  }

  @Override
  public Store<AppState> getStore() {
    return provideStoreProvider.get();
  }

  @Override
  public Observable<AppState> getState() {
    return provideStateProvider.get();
  }

  @Override
  public Gson getGson() {
    return provideoGsonProvider.get();
  }

  public static final class Builder {
    private ApplicationModule applicationModule;

    private Builder() {}

    public ApplicationComponent build() {
      if (applicationModule == null) {
        throw new IllegalStateException(
            ApplicationModule.class.getCanonicalName() + " must be set");
      }
      return new DaggerApplicationComponent(this);
    }

    public Builder applicationModule(ApplicationModule applicationModule) {
      this.applicationModule = Preconditions.checkNotNull(applicationModule);
      return this;
    }
  }
}
