package com.colorhaake.traveler.view.main;

import com.colorhaake.traveler.view.home.ActivityHeader;
import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<MainPresenter> mMainPresenterProvider;

  private final Provider<ActivityHeader> headerProvider;

  public MainActivity_MembersInjector(
      Provider<MainPresenter> mMainPresenterProvider, Provider<ActivityHeader> headerProvider) {
    assert mMainPresenterProvider != null;
    this.mMainPresenterProvider = mMainPresenterProvider;
    assert headerProvider != null;
    this.headerProvider = headerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<MainPresenter> mMainPresenterProvider, Provider<ActivityHeader> headerProvider) {
    return new MainActivity_MembersInjector(mMainPresenterProvider, headerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.mMainPresenter = mMainPresenterProvider.get();
    instance.header = headerProvider.get();
  }

  public static void injectMMainPresenter(
      MainActivity instance, Provider<MainPresenter> mMainPresenterProvider) {
    instance.mMainPresenter = mMainPresenterProvider.get();
  }

  public static void injectHeader(MainActivity instance, Provider<ActivityHeader> headerProvider) {
    instance.header = headerProvider.get();
  }
}
