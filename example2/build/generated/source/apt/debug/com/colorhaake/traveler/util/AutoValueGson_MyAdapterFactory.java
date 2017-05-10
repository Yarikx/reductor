package com.colorhaake.traveler.util;

import com.colorhaake.traveler.model.AppState;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import java.lang.Override;
import java.lang.SuppressWarnings;

public final class AutoValueGson_MyAdapterFactory extends MyAdapterFactory {
  @Override
  @SuppressWarnings("unchecked")
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    Class<T> rawType = (Class<T>) type.getRawType();
    if (AppState.class.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) AppState.typeAdapter(gson);
    } else {
      return null;
    }
  }
}
