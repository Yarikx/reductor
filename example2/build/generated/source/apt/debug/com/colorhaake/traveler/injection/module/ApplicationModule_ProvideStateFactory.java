package com.colorhaake.traveler.injection.module;

import com.colorhaake.traveler.model.AppState;
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
public final class ApplicationModule_ProvideStateFactory implements Factory<Observable<AppState>> {
  private final ApplicationModule module;

  private final Provider<Store<AppState>> storeProvider;

  public ApplicationModule_ProvideStateFactory(
      ApplicationModule module, Provider<Store<AppState>> storeProvider) {
    assert module != null;
    this.module = module;
    assert storeProvider != null;
    this.storeProvider = storeProvider;
  }

  @Override
  public Observable<AppState> get() {
    return Preconditions.checkNotNull(
        module.provideState(storeProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Observable<AppState>> create(
      ApplicationModule module, Provider<Store<AppState>> storeProvider) {
    return new ApplicationModule_ProvideStateFactory(module, storeProvider);
  }

  /** Proxies {@link ApplicationModule#provideState(Store)}. */
  public static Observable<AppState> proxyProvideState(
      ApplicationModule instance, Store<AppState> store) {
    return instance.provideState(store);
  }
}
