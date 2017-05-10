package com.colorhaake.traveler.view.home.activity_event;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.colorhaake.traveler.R;
import com.colorhaake.traveler.plain_object.ActivityEvent;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by colorhaake on 2017/3/26.
 */

public class ActivityAdapterDelegate extends AbsListItemAdapterDelegate
        <ActivityEvent, ActivityEvent, ActivityAdapterDelegate.ActivityViewHolder> {

    public static final String TAG = ActivityAdapterDelegate.class.getName();

    @Override
    protected boolean isForViewType(
            @NonNull ActivityEvent item, @NonNull List<ActivityEvent> items, int position
    ) {
        return item.type.equals(ActivityEventType.ACTIVITY) ||
                item.type.equals(ActivityEventType.TOPIC);
    }

    @NonNull
    @Override
    protected ActivityAdapterDelegate.ActivityViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent
    ) {
        return new ActivityViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.activity_event_list_item_for_activity, parent, false)
        );
    }

    private int getStarImageViewByIndex(int index, float score) {
        if (index >= score) {
            return R.drawable.ic_star_border_black_18dp;
        }

        if (Math.floor(score) == index) {
            return R.drawable.ic_star_half_black_18dp;
        }

        return R.drawable.ic_star_black_18dp;
    }

    private void setScoreViewByScore(
            float score, ActivityAdapterDelegate.ActivityViewHolder holder
    ) {
        if (score <= 0) {
            holder.scoreContainer.setVisibility(View.INVISIBLE);
            return;
        }

        holder.scoreContainer.setVisibility(View.VISIBLE);
        for (int i = 0; i < holder.starts.size(); i++) {
            ImageView v = holder.starts.get(i);
            v.setBackgroundResource(getStarImageViewByIndex(i, score));
        }

        holder.scoreView.setText(String.valueOf(score));
    }

    @Override
    protected void onBindViewHolder(
            @NonNull ActivityEvent item,
            @NonNull ActivityAdapterDelegate.ActivityViewHolder holder,
            @NonNull List<Object> payloads
    ) {
        holder.nameView.setText(item.name);
        holder.imageView.setImageURI(item.image_url);
        holder.subNameView.setText(item.subname);
        holder.participantsView.setText(item.participants_format);
        holder.cityView.setText(item.city_name);
        holder.marketPriceView.setText(
                item.market_price.equals(item.selling_price) ? "" : item.market_price
        );
        holder.sellingPriceView.setText(item.selling_price);
        holder.videoIcon.setVisibility(
                item.video ? View.VISIBLE : View.INVISIBLE
        );

        setScoreViewByScore(item.score, holder);
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.activity_name) TextView nameView;
        @BindView(R.id.activity_image) SimpleDraweeView imageView;
        @BindView(R.id.activity_sub_name) TextView subNameView;
        @BindView(R.id.participants) TextView participantsView;
        @BindView(R.id.city) TextView cityView;
        @BindView(R.id.market_price) TextView marketPriceView;
        @BindView(R.id.selling_price) TextView sellingPriceView;
        @BindView(R.id.activity_video) ImageView videoIcon;

        @BindView(R.id.score_container) LinearLayout scoreContainer;
        @BindViews({
                R.id.activity_start1,
                R.id.activity_start2,
                R.id.activity_start3,
                R.id.activity_start4,
                R.id.activity_start5
        })
        public List<ImageView> starts = new ArrayList<>();

        @BindView(R.id.activity_score) public TextView scoreView;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            marketPriceView.setPaintFlags(
                    marketPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        }
    }
}
