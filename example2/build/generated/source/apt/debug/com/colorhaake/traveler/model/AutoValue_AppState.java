package com.colorhaake.traveler.model;

import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.plain_object.Response;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.Override;
import java.lang.String;

final class AutoValue_AppState extends $AutoValue_AppState {
  AutoValue_AppState(Response<HomeData> homeData) {
    super(homeData);
  }

  public static final class GsonTypeAdapter extends TypeAdapter<AppState> {
    private final TypeAdapter<Response<HomeData>> homeDataAdapter;
    public GsonTypeAdapter(Gson gson) {
      this.homeDataAdapter = gson.getAdapter(new TypeToken<Response<HomeData>>(){});
    }
    @Override
    public void write(JsonWriter jsonWriter, AppState object) throws IOException {
      jsonWriter.beginObject();
      jsonWriter.name("homeData");
      homeDataAdapter.write(jsonWriter, object.homeData());
      jsonWriter.endObject();
    }
    @Override
    public AppState read(JsonReader jsonReader) throws IOException {
      jsonReader.beginObject();
      Response<HomeData> homeData = null;
      while (jsonReader.hasNext()) {
        String _name = jsonReader.nextName();
        if (jsonReader.peek() == JsonToken.NULL) {
          jsonReader.skipValue();
          continue;
        }
        switch (_name) {
          case "homeData": {
            homeData = homeDataAdapter.read(jsonReader);
            break;
          }
          default: {
            jsonReader.skipValue();
          }
        }
      }
      jsonReader.endObject();
      return new AutoValue_AppState(homeData);
    }
  }
}
