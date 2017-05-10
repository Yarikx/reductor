package com.colorhaake.traveler.view.home.activity_event;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colorhaake.traveler.R;
import com.colorhaake.traveler.plain_object.ActivityEvent;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by colorhaake on 2017/3/26.
 */

public class CityAdapterDelegate extends AbsListItemAdapterDelegate
        <ActivityEvent, ActivityEvent, CityAdapterDelegate.CityViewHolder> {

    public static final String TAG = CityAdapterDelegate.class.getName();

    @Override
    protected boolean isForViewType(
            @NonNull ActivityEvent item, @NonNull List<ActivityEvent> items, int position
    ) {
        return item.type.equals(ActivityEventType.CITY);
    }

    @NonNull
    @Override
    protected CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new CityViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.activity_event_list_item_for_city, parent, false)
        );
    }

    @Override
    protected void onBindViewHolder(
            @NonNull ActivityEvent item,
            @NonNull CityViewHolder holder,
            @NonNull List<Object> payloads
    ) {
        holder.bgImage.setImageURI(item.image_url);
        holder.cityView.setText(item.name);
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.activity_image) SimpleDraweeView bgImage;
        @BindView(R.id.activity_city) public TextView cityView;

        public CityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
