package com.colorhaake.traveler;

import com.colorhaake.traveler.model.AppState;
import com.google.gson.Gson;
import com.yheriatovych.reductor.Store;
import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class TravelerApp_MembersInjector implements MembersInjector<TravelerApp> {
  private final Provider<Store<AppState>> storeProvider;

  private final Provider<Gson> gsonProvider;

  public TravelerApp_MembersInjector(
      Provider<Store<AppState>> storeProvider, Provider<Gson> gsonProvider) {
    assert storeProvider != null;
    this.storeProvider = storeProvider;
    assert gsonProvider != null;
    this.gsonProvider = gsonProvider;
  }

  public static MembersInjector<TravelerApp> create(
      Provider<Store<AppState>> storeProvider, Provider<Gson> gsonProvider) {
    return new TravelerApp_MembersInjector(storeProvider, gsonProvider);
  }

  @Override
  public void injectMembers(TravelerApp instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.store = storeProvider.get();
    instance.gson = gsonProvider.get();
  }

  public static void injectGson(TravelerApp instance, Provider<Gson> gsonProvider) {
    instance.gson = gsonProvider.get();
  }
}
