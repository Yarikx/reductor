package com.colorhaake.traveler.injection.module;

import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvideContextFactory implements Factory<Context> {
  private final ApplicationModule module;

  public ApplicationModule_ProvideContextFactory(ApplicationModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public Context get() {
    return Preconditions.checkNotNull(
        module.provideContext(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Context> create(ApplicationModule module) {
    return new ApplicationModule_ProvideContextFactory(module);
  }

  /** Proxies {@link ApplicationModule#provideContext()}. */
  public static Context proxyProvideContext(ApplicationModule instance) {
    return instance.provideContext();
  }
}
