package com.colorhaake.traveler.util;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

/**
 * Created by josephcheng on 2017/3/12.
 */

@GsonTypeAdapterFactory
public abstract class MyAdapterFactory implements TypeAdapterFactory {
    public static TypeAdapterFactory create() {
        return new AutoValueGson_MyAdapterFactory();
    }
}
