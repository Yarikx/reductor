package com.colorhaake.traveler.injection.module;

import android.app.Activity;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ActivityModule_ProvideActivityFactory implements Factory<Activity> {
  private final ActivityModule module;

  public ActivityModule_ProvideActivityFactory(ActivityModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public Activity get() {
    return Preconditions.checkNotNull(
        module.provideActivity(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Activity> create(ActivityModule module) {
    return new ActivityModule_ProvideActivityFactory(module);
  }

  /** Proxies {@link ActivityModule#provideActivity()}. */
  public static Activity proxyProvideActivity(ActivityModule instance) {
    return instance.provideActivity();
  }
}
