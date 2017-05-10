// Generated code from Butter Knife. Do not modify!
package com.colorhaake.traveler.view.home.activity_event;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.colorhaake.traveler.R;
import com.facebook.drawee.view.SimpleDraweeView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ActivityAdapterDelegate$ActivityViewHolder_ViewBinding implements Unbinder {
  private ActivityAdapterDelegate.ActivityViewHolder target;

  @UiThread
  public ActivityAdapterDelegate$ActivityViewHolder_ViewBinding(ActivityAdapterDelegate.ActivityViewHolder target,
      View source) {
    this.target = target;

    target.nameView = Utils.findRequiredViewAsType(source, R.id.activity_name, "field 'nameView'", TextView.class);
    target.imageView = Utils.findRequiredViewAsType(source, R.id.activity_image, "field 'imageView'", SimpleDraweeView.class);
    target.subNameView = Utils.findRequiredViewAsType(source, R.id.activity_sub_name, "field 'subNameView'", TextView.class);
    target.participantsView = Utils.findRequiredViewAsType(source, R.id.participants, "field 'participantsView'", TextView.class);
    target.cityView = Utils.findRequiredViewAsType(source, R.id.city, "field 'cityView'", TextView.class);
    target.marketPriceView = Utils.findRequiredViewAsType(source, R.id.market_price, "field 'marketPriceView'", TextView.class);
    target.sellingPriceView = Utils.findRequiredViewAsType(source, R.id.selling_price, "field 'sellingPriceView'", TextView.class);
    target.videoIcon = Utils.findRequiredViewAsType(source, R.id.activity_video, "field 'videoIcon'", ImageView.class);
    target.scoreContainer = Utils.findRequiredViewAsType(source, R.id.score_container, "field 'scoreContainer'", LinearLayout.class);
    target.scoreView = Utils.findRequiredViewAsType(source, R.id.activity_score, "field 'scoreView'", TextView.class);
    target.starts = Utils.listOf(
        Utils.findRequiredViewAsType(source, R.id.activity_start1, "field 'starts'", ImageView.class), 
        Utils.findRequiredViewAsType(source, R.id.activity_start2, "field 'starts'", ImageView.class), 
        Utils.findRequiredViewAsType(source, R.id.activity_start3, "field 'starts'", ImageView.class), 
        Utils.findRequiredViewAsType(source, R.id.activity_start4, "field 'starts'", ImageView.class), 
        Utils.findRequiredViewAsType(source, R.id.activity_start5, "field 'starts'", ImageView.class));
  }

  @Override
  @CallSuper
  public void unbind() {
    ActivityAdapterDelegate.ActivityViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.nameView = null;
    target.imageView = null;
    target.subNameView = null;
    target.participantsView = null;
    target.cityView = null;
    target.marketPriceView = null;
    target.sellingPriceView = null;
    target.videoIcon = null;
    target.scoreContainer = null;
    target.scoreView = null;
    target.starts = null;
  }
}
