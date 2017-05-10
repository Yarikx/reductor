// Generated code from Butter Knife. Do not modify!
package com.colorhaake.traveler.view.home.activity_event;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.colorhaake.traveler.R;
import com.facebook.drawee.view.SimpleDraweeView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CityAdapterDelegate$CityViewHolder_ViewBinding implements Unbinder {
  private CityAdapterDelegate.CityViewHolder target;

  @UiThread
  public CityAdapterDelegate$CityViewHolder_ViewBinding(CityAdapterDelegate.CityViewHolder target,
      View source) {
    this.target = target;

    target.bgImage = Utils.findRequiredViewAsType(source, R.id.activity_image, "field 'bgImage'", SimpleDraweeView.class);
    target.cityView = Utils.findRequiredViewAsType(source, R.id.activity_city, "field 'cityView'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CityAdapterDelegate.CityViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.bgImage = null;
    target.cityView = null;
  }
}
