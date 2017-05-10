
package com.colorhaake.traveler.model;

import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.plain_object.Response;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 abstract class $AutoValue_AppState extends AppState {

  private final Response<HomeData> homeData;

  $AutoValue_AppState(
      Response<HomeData> homeData) {
    if (homeData == null) {
      throw new NullPointerException("Null homeData");
    }
    this.homeData = homeData;
  }

  @Override
  public Response<HomeData> homeData() {
    return homeData;
  }

  @Override
  public String toString() {
    return "AppState{"
        + "homeData=" + homeData
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof AppState) {
      AppState that = (AppState) o;
      return (this.homeData.equals(that.homeData()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.homeData.hashCode();
    return h;
  }

  @Override
  public AppState.Builder toBuilder() {
    return new Builder(this);
  }

  static final class Builder extends AppState.Builder {
    private Response<HomeData> homeData;
    Builder() {
    }
    Builder(AppState source) {
      this.homeData = source.homeData();
    }
    @Override
    public AppState.Builder setHomeData(Response<HomeData> homeData) {
      this.homeData = homeData;
      return this;
    }
    @Override
    public AppState build() {
      String missing = "";
      if (homeData == null) {
        missing += " homeData";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_AppState(
          this.homeData);
    }
  }

}
