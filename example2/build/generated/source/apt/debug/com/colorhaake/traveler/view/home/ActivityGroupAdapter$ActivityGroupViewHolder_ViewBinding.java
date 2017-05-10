// Generated code from Butter Knife. Do not modify!
package com.colorhaake.traveler.view.home;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.colorhaake.traveler.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ActivityGroupAdapter$ActivityGroupViewHolder_ViewBinding implements Unbinder {
  private ActivityGroupAdapter.ActivityGroupViewHolder target;

  @UiThread
  public ActivityGroupAdapter$ActivityGroupViewHolder_ViewBinding(ActivityGroupAdapter.ActivityGroupViewHolder target,
      View source) {
    this.target = target;

    target.nameView = Utils.findRequiredViewAsType(source, R.id.activity_event_list_name, "field 'nameView'", TextView.class);
    target.activityEventView = Utils.findRequiredViewAsType(source, R.id.activity_event_list, "field 'activityEventView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ActivityGroupAdapter.ActivityGroupViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.nameView = null;
    target.activityEventView = null;
  }
}
