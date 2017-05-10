// Generated code from Butter Knife. Do not modify!
package com.colorhaake.traveler.view.home;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.colorhaake.traveler.R;
import com.youth.banner.Banner;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ActivityHeader_ViewBinding implements Unbinder {
  private ActivityHeader target;

  @UiThread
  public ActivityHeader_ViewBinding(ActivityHeader target, View source) {
    this.target = target;

    target.banner = Utils.findRequiredViewAsType(source, R.id.banner, "field 'banner'", Banner.class);
    target.title = Utils.findRequiredViewAsType(source, R.id.title, "field 'title'", TextView.class);
    target.desc = Utils.findRequiredViewAsType(source, R.id.description, "field 'desc'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ActivityHeader target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.banner = null;
    target.title = null;
    target.desc = null;
  }
}
