package com.colorhaake.traveler.injection.module;

import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.reducer.home.HomeActions;
import com.colorhaake.traveler.view.main.MainPresenter;
import com.yheriatovych.reductor.Store;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import io.reactivex.Observable;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ActivityModule_ProvidePresenterFactory implements Factory<MainPresenter> {
  private final ActivityModule module;

  private final Provider<Observable<AppState>> stateProvider;

  private final Provider<Store<AppState>> storeProvider;

  private final Provider<HomeActions> actionsProvider;

  public ActivityModule_ProvidePresenterFactory(
      ActivityModule module,
      Provider<Observable<AppState>> stateProvider,
      Provider<Store<AppState>> storeProvider,
      Provider<HomeActions> actionsProvider) {
    assert module != null;
    this.module = module;
    assert stateProvider != null;
    this.stateProvider = stateProvider;
    assert storeProvider != null;
    this.storeProvider = storeProvider;
    assert actionsProvider != null;
    this.actionsProvider = actionsProvider;
  }

  @Override
  public MainPresenter get() {
    return Preconditions.checkNotNull(
        module.providePresenter(stateProvider.get(), storeProvider.get(), actionsProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<MainPresenter> create(
      ActivityModule module,
      Provider<Observable<AppState>> stateProvider,
      Provider<Store<AppState>> storeProvider,
      Provider<HomeActions> actionsProvider) {
    return new ActivityModule_ProvidePresenterFactory(
        module, stateProvider, storeProvider, actionsProvider);
  }

  /** Proxies {@link ActivityModule#providePresenter(Observable, Store, HomeActions)}. */
  public static MainPresenter proxyProvidePresenter(
      ActivityModule instance,
      Observable<AppState> state,
      Store<AppState> store,
      HomeActions actions) {
    return instance.providePresenter(state, store, actions);
  }
}
