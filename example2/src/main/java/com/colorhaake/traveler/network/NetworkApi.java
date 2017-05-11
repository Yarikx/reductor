package com.colorhaake.traveler.network;

import android.util.Log;

import com.colorhaake.traveler.Config;
import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.plain_object.Response;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by josephcheng on 2017/3/12.
 */
public class NetworkApi {
    public static final String TAG = NetworkApi.class.getName();
    private static Retrofit retrofit = null;
    private static TravelerService travelerService = null;
    static {
        retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            // TODO check create/createAsync difference
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(RetrofitUtil.genericClient())
            .baseUrl(Config.BASE_API_URL)
            .build();

        travelerService = retrofit.create(TravelerService.class);
    }

    public static Observable<Response<HomeData>> fetchHomeData(double lat, double lng, long time) {
        return travelerService.fetchHomeData(lat, lng, time)
            // TODO try to handle all subscribeOn in one place
            .subscribeOn(Schedulers.io());
    }
}
