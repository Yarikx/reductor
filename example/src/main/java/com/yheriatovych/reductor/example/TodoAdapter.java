package com.yheriatovych.reductor.example;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.yheriatovych.reductor.example.model.Note;
import rx.functions.Action1;

import java.util.List;

class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.NoteViewHolder> {
    private List<Note> notes;
    private final Action1<Note> onClickListener;

    public TodoAdapter(List<Note> notes, Action1<Note> onClickListener) {
        this.notes = notes;
        this.onClickListener = onClickListener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        final Note note = notes.get(position);
        holder.content.setText(note.note);
        holder.content.setChecked(note.checked);
        holder.itemView.setOnClickListener(view -> onClickListener.call(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).id;
    }

    public void setNotes(List<Note> notes) {
        List<Note> oldNotes = this.notes;
        this.notes = notes;
        DiffUtil.calculateDiff(new NotesDiffCallback(oldNotes, notes), false).dispatchUpdatesTo(this);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        public CheckBox content;

        public NoteViewHolder(View itemView) {
            super(itemView);
            content = (CheckBox) itemView;
        }
    }

}
