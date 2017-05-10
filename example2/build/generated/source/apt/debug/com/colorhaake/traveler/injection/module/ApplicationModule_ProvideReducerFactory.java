package com.colorhaake.traveler.injection.module;

import com.colorhaake.traveler.model.AppStateReducer;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvideReducerFactory implements Factory<AppStateReducer> {
  private final ApplicationModule module;

  public ApplicationModule_ProvideReducerFactory(ApplicationModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public AppStateReducer get() {
    return Preconditions.checkNotNull(
        module.provideReducer(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<AppStateReducer> create(ApplicationModule module) {
    return new ApplicationModule_ProvideReducerFactory(module);
  }

  /** Proxies {@link ApplicationModule#provideReducer()}. */
  public static AppStateReducer proxyProvideReducer(ApplicationModule instance) {
    return instance.provideReducer();
  }
}
