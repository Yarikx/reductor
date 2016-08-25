package com.yheriatovych.reductor.example;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import rx.functions.Action1;

class NoteTouchCallback extends ItemTouchHelper.SimpleCallback {
    private final Action1<Integer> onDismissed;
    public NoteTouchCallback(Action1<Integer> onDismissed) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.onDismissed = onDismissed;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        onDismissed.call(position);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
