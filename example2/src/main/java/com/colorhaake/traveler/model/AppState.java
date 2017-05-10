package com.colorhaake.traveler.model;

import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.plain_object.Response;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.yheriatovych.reductor.annotations.CombinedState;

/**
 * Created by josephcheng on 2017/3/12.
 */

@CombinedState
@AutoValue
public abstract class AppState {
    public abstract Response<HomeData> homeData();

    public static TypeAdapter<AppState> typeAdapter(Gson gson) {
        return new AutoValue_AppState.GsonTypeAdapter(gson);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_AppState.Builder();
    }

    public AppState withResponseHomeData(Response<HomeData> value) {
        return toBuilder().setHomeData(value).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setHomeData(Response<HomeData> value);
        public abstract AppState build();
    }
}
