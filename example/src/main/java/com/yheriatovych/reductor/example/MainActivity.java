package com.yheriatovych.reductor.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.yheriatovych.reductor.Cancelable;
import com.yheriatovych.reductor.Store;
import com.yheriatovych.reductor.example.model.AppState;
import com.yheriatovych.reductor.example.model.Note;
import com.yheriatovych.reductor.example.model.NotesFilter;
import com.yheriatovych.reductor.example.reducers.NotesFilterReducerImpl;
import com.yheriatovych.reductor.example.reducers.NotesListReducerImpl;
import com.yheriatovych.reductor.example.reducers.utils.UndoableReducer;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    Store<AppState> store;
    private AtomicInteger idGenerator = new AtomicInteger();
    private Cancelable mCancelable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = ((ReductorApp) getApplicationContext()).store;
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        setupSpinner(spinner, store);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final TodoAdapter adapter = new TodoAdapter(store.getState().notes(),
                note -> store.dispatch(NotesListReducerImpl.ActionCreator.toggle(note.id)));

        new ItemTouchHelper(new NoteTouchCallback(position -> {
            Note note = store.getState().notes().get(position);
            store.dispatch(NotesListReducerImpl.ActionCreator.remove(note.id));
        })).attachToRecyclerView(recyclerView);


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

    private void setupSpinner(Spinner spinner, Store<AppState> store) {
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
        menu.add(R.string.undo)
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

}
