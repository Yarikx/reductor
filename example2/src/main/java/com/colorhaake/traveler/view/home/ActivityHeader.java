package com.colorhaake.traveler.view.home;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.colorhaake.traveler.R;
import com.colorhaake.traveler.plain_object.HomeData;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by colorhaake on 2017/3/27.
 */

public class ActivityHeader {
    Activity activity;

    View header;

    @BindView(R.id.banner) Banner banner;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.description) TextView desc;

    boolean isInflate = false;

    private ActivityHeader(Activity activity) {
        this.activity = activity;
    }

    public static ActivityHeader getInstance(Activity activity) {
        return new ActivityHeader(activity);
    }

    public ActivityHeader inflate() {
        header = LayoutInflater.from(activity)
                .inflate(R.layout.content_main_header, null);

        ButterKnife.bind(this, header);

        isInflate = true;
        return this;
    }

    public View view() {
        if (!isInflate) {
            inflate();
        }

        return header;
    }

    public ActivityHeader update(HomeData headerInfo) {
        if (!isInflate) {
            inflate();
        }

        // TODO fix animation
        banner.setImages(headerInfo.banner_images)
            .setBannerStyle(BannerConfig.NOT_INDICATOR)
            .setImageLoader(new FrescoImageLoader())
            .setBannerAnimation(Transformer.ZoomOut)
            .isAutoPlay(true)
            .setDelayTime(5000)
            .start();

        title.setText(headerInfo.name);
        desc.setText(headerInfo.subname);
        return this;
    }

    public void startAutoPlay() {
        banner.startAutoPlay();
    }

    public void stopAutoCycle() {
        banner.stopAutoPlay();
    }
}
