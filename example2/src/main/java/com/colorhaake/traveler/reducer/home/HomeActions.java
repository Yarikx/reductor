package com.colorhaake.traveler.reducer.home;

import android.content.Context;

import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.plain_object.Response;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.ActionCreator;

/**
 * Created by josephcheng on 2017/3/12.
 */

@ActionCreator
public interface HomeActions {
    String VIEW_READY = "VIEW_READY";
    @ActionCreator.Action(VIEW_READY)
    Action viewReady(Context context);

    String FETCH_HOME_DATA_RES = "FETCH_HOME_DATA_RES";
    @ActionCreator.Action(FETCH_HOME_DATA_RES)
    Action fetchHomeDataRes(Response<HomeData> data);
}
