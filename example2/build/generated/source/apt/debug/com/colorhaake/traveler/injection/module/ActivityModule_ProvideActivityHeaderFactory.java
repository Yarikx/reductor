package com.colorhaake.traveler.injection.module;

import android.app.Activity;
import com.colorhaake.traveler.view.home.ActivityHeader;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ActivityModule_ProvideActivityHeaderFactory implements Factory<ActivityHeader> {
  private final ActivityModule module;

  private final Provider<Activity> activityProvider;

  public ActivityModule_ProvideActivityHeaderFactory(
      ActivityModule module, Provider<Activity> activityProvider) {
    assert module != null;
    this.module = module;
    assert activityProvider != null;
    this.activityProvider = activityProvider;
  }

  @Override
  public ActivityHeader get() {
    return Preconditions.checkNotNull(
        module.provideActivityHeader(activityProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<ActivityHeader> create(
      ActivityModule module, Provider<Activity> activityProvider) {
    return new ActivityModule_ProvideActivityHeaderFactory(module, activityProvider);
  }

  /** Proxies {@link ActivityModule#provideActivityHeader(Activity)}. */
  public static ActivityHeader proxyProvideActivityHeader(
      ActivityModule instance, Activity activity) {
    return instance.provideActivityHeader(activity);
  }
}
