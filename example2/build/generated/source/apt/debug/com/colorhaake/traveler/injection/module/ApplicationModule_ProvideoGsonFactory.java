package com.colorhaake.traveler.injection.module;

import com.google.gson.Gson;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ApplicationModule_ProvideoGsonFactory implements Factory<Gson> {
  private final ApplicationModule module;

  public ApplicationModule_ProvideoGsonFactory(ApplicationModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public Gson get() {
    return Preconditions.checkNotNull(
        module.provideoGson(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Gson> create(ApplicationModule module) {
    return new ApplicationModule_ProvideoGsonFactory(module);
  }

  /** Proxies {@link ApplicationModule#provideoGson()}. */
  public static Gson proxyProvideoGson(ApplicationModule instance) {
    return instance.provideoGson();
  }
}
