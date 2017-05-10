package com.colorhaake.traveler.injection.module;

import android.app.Application;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_GetApplicationFactory implements Factory<Application> {
  private final ApplicationModule module;

  public ApplicationModule_GetApplicationFactory(ApplicationModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public Application get() {
    return Preconditions.checkNotNull(
        module.getApplication(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Application> create(ApplicationModule module) {
    return new ApplicationModule_GetApplicationFactory(module);
  }

  /** Proxies {@link ApplicationModule#getApplication()}. */
  public static Application proxyGetApplication(ApplicationModule instance) {
    return instance.getApplication();
  }
}
