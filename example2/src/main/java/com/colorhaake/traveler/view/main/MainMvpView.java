package com.colorhaake.traveler.view.main;

import com.colorhaake.traveler.plain_object.ActivityGroup;
import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.view.base.MvpView;

import java.util.List;

/**
 * Created by colorhaake on 2017/3/30.
 */

public interface MainMvpView extends MvpView {
    void showHomePage(List<ActivityGroup> list);
    void updateHeaderView(HomeData headerInfo);
}
