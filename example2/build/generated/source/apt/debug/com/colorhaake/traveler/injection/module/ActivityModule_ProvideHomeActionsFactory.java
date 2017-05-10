package com.colorhaake.traveler.injection.module;

import com.colorhaake.traveler.reducer.home.HomeActions;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ActivityModule_ProvideHomeActionsFactory implements Factory<HomeActions> {
  private final ActivityModule module;

  public ActivityModule_ProvideHomeActionsFactory(ActivityModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public HomeActions get() {
    return Preconditions.checkNotNull(
        module.provideHomeActions(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<HomeActions> create(ActivityModule module) {
    return new ActivityModule_ProvideHomeActionsFactory(module);
  }

  /** Proxies {@link ActivityModule#provideHomeActions()}. */
  public static HomeActions proxyProvideHomeActions(ActivityModule instance) {
    return instance.provideHomeActions();
  }
}
