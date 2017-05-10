package com.colorhaake.traveler.view.home;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by colorhaake on 2017/3/27.
 */

public class FrescoImageLoader extends ImageLoader {

    public static final String TAG = FrescoImageLoader.class.getName();

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Uri uri = Uri.parse((String) path);
        imageView.setImageURI(uri);
    }

    @Override
    public ImageView createImageView(Context context) {
        return new SimpleDraweeView(context);
    }
}
