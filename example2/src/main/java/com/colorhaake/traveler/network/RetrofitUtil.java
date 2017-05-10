package com.colorhaake.traveler.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by colorhaake on 2017/3/25.
 */

public class RetrofitUtil {
    public static OkHttpClient genericClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    request = request.newBuilder()
                            // TODO fix hard code for header info
                            .addHeader("Accept-Language", "zh_TW")
                            .addHeader("currency", "TWD")
                            .addHeader("Klook-App-Version", "2.5.0")
                            .addHeader("Klook-App-Version-Code", "52")
                            .addHeader("X-Platform", "android")
                            .addHeader("Gateway", "android")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return httpClient;
    }
}
