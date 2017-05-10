package com.colorhaake.traveler.injection.module;

import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.model.AppStateReducer;
import com.yheriatovych.reductor.Store;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvideStoreFactory implements Factory<Store<AppState>> {
  private final ApplicationModule module;

  private final Provider<AppStateReducer> reducerProvider;

  public ApplicationModule_ProvideStoreFactory(
      ApplicationModule module, Provider<AppStateReducer> reducerProvider) {
    assert module != null;
    this.module = module;
    assert reducerProvider != null;
    this.reducerProvider = reducerProvider;
  }

  @Override
  public Store<AppState> get() {
    return Preconditions.checkNotNull(
        module.provideStore(reducerProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Store<AppState>> create(
      ApplicationModule module, Provider<AppStateReducer> reducerProvider) {
    return new ApplicationModule_ProvideStoreFactory(module, reducerProvider);
  }

  /** Proxies {@link ApplicationModule#provideStore(AppStateReducer)}. */
  public static Store<AppState> proxyProvideStore(
      ApplicationModule instance, AppStateReducer reducer) {
    return instance.provideStore(reducer);
  }
}
