package com.colorhaake.traveler.network;

import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.plain_object.Response;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by josephcheng on 2017/3/25.
 */
public interface TravelerService {
    @GET("v2/home")
    Observable<Response<HomeData>> fetchHomeData(
            @Query("lat") double lat, @Query("lng") double lng, @Query("time") long time
    );
}
