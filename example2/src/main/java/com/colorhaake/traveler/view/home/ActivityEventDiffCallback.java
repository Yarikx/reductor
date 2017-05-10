package com.colorhaake.traveler.view.home;

import android.support.v7.util.DiffUtil;

import com.colorhaake.traveler.plain_object.ActivityEvent;

import java.util.List;


/**
 * Created by josephcheng on 2017/3/25.
 */
// TODO should be combined with ActivityGroupDiffCallback
public class ActivityEventDiffCallback extends DiffUtil.Callback {
    private final List<ActivityEvent> oldList;
    private final List<ActivityEvent> newList;

    public ActivityEventDiffCallback(List<ActivityEvent> oldList, List<ActivityEvent> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        boolean isTheSame = oldList.get(oldItemPosition).id == newList.get(newItemPosition).id;
        return isTheSame;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        boolean isTheSame = oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        return isTheSame;
    }
}
