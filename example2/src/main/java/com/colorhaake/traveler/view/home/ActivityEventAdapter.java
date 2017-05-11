package com.colorhaake.traveler.view.home;

import com.colorhaake.traveler.plain_object.ActivityEvent;
import com.colorhaake.traveler.view.home.activity_event.ActivityAdapterDelegate;
import com.colorhaake.traveler.view.home.activity_event.CityAdapterDelegate;
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;

import java.util.List;

/**
 * Created by josephcheng on 2017/3/12.
 */
public class ActivityEventAdapter extends ListDelegationAdapter<List<ActivityEvent>> {

    public static final String TAG = ActivityEventAdapter.class.getName();

    public ActivityEventAdapter(List<ActivityEvent> items) {
        delegatesManager
                .addDelegate(new ActivityAdapterDelegate())
                .addDelegate(new CityAdapterDelegate());

        setItems(items);
    }
}
