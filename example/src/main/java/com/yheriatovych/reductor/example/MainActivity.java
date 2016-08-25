package com.yheriatovych.reductor.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.*;
import android.widget.*;
import com.yheriatovych.reductor.Cancelable;
import com.yheriatovych.reductor.Store;
import com.yheriatovych.reductor.example.model.AppState;
import com.yheriatovych.reductor.example.model.Note;
import com.yheriatovych.reductor.example.model.NotesFilter;
import com.yheriatovych.reductor.example.reducers.NotesFilterReducerImpl;
import com.yheriatovych.reductor.example.reducers.NotesListReducerImpl;
import com.yheriatovych.reductor.example.reducers.utils.UndoableReducer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {


    Store<AppState> store;
    private AtomicInteger idGenerator = new AtomicInteger();
    private Cancelable mCancelable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = ((ReduxApp) getApplicationContext()).store;
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        setupSpinner(spinner);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final TodoAdapter adapter = new TodoAdapter();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = store.getState().notes().get(position);
                store.dispatch(NotesListReducerImpl.ActionCreator.remove(note.id));
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        mCancelable = store.subscribe(state -> {
            adapter.setNotes(Utils.getFilteredNotes(state));
            spinner.setSelection(state.filter().ordinal());
        });

        final EditText editText = (EditText) findViewById(R.id.note_edit_text);
        findViewById(R.id.add).setOnClickListener(view -> {
            String note = editText.getText().toString();
            int id = idGenerator.getAndIncrement();
            store.dispatch(NotesListReducerImpl.ActionCreator.add(id, note));
            editText.setText(null);
        });
    }

    private void setupSpinner(Spinner spinner) {
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, NotesFilter.values());
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                store.dispatch(NotesFilterReducerImpl.ActionCreator.setFilter(NotesFilter.values()[i]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("Undo")
                .setIcon(R.drawable.ic_undo_24dp)
                .setOnMenuItemClickListener(menuItem -> {
                    store.dispatch(UndoableReducer.pop());
                    return true;
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    protected void onDestroy() {
        mCancelable.cancel();
        super.onDestroy();
    }

    class TodoAdapter extends RecyclerView.Adapter<NoteViewHolder> {
        private List<Note> mNotes = store.getState().notes();

        @Override
        public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NoteViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_item, parent, false));
        }

        @Override
        public void onBindViewHolder(NoteViewHolder holder, int position) {
            final Note note = mNotes.get(position);
            holder.content.setText(note.note);
            holder.content.setChecked(note.checked);
            holder.itemView.setOnClickListener(view -> store.dispatch(NotesListReducerImpl.ActionCreator.toggle(note.id)));

            holder.itemView.setOnLongClickListener(v -> {
                store.dispatch(NotesListReducerImpl.ActionCreator.remove(note.id));
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        @Override
        public long getItemId(int position) {
            return mNotes.get(position).id;
        }

        public void setNotes(List<Note> notes) {
            List<Note> oldNotes = mNotes;
            mNotes = notes;
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldNotes.size();
                }

                @Override
                public int getNewListSize() {
                    return notes.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldNotes.get(oldItemPosition).id == notes.get(newItemPosition).id;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldNotes.get(oldItemPosition).equals(notes.get(newItemPosition));
                }
            }, false).dispatchUpdatesTo(this);
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        public CheckBox content;

        public NoteViewHolder(View itemView) {
            super(itemView);
            content = (CheckBox) itemView;
        }
    }
}
