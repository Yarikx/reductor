package com.yheriatovych.reductor.example;

import android.app.Application;
import android.os.Handler;
import android.util.Log;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.console.RuntimeReplFactory;
import com.facebook.stetho.rhino.JsRuntimeReplFactoryBuilder;
import com.google.gson.Gson;
import com.yheriatovych.reductor.Store;
import com.yheriatovych.reductor.example.model.AppState;
import com.yheriatovych.reductor.example.model.AppStateImpl;
import com.yheriatovych.reductor.example.model.AppStateReducer;
import com.yheriatovych.reductor.example.reducers.NotesFilterReducerImpl;
import com.yheriatovych.reductor.example.reducers.NotesListReducer;
import com.yheriatovych.reductor.example.reducers.utils.SetStateReducer;
import com.yheriatovych.reductor.example.reducers.utils.UndoableReducer;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class ReductorApp extends Application {

    public Store<AppState> store;
    Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();

        final AppStateReducer vanilaReducer = AppStateReducer.builder()
                .notesReducer(NotesListReducer.create())
                .filterReducer(new NotesFilterReducerImpl())
                .build();
        store = Store.create(
                new SetStateReducer<>(
                        new UndoableReducer<>(vanilaReducer)), null);

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(() -> new Stetho.DefaultInspectorModulesBuilder(ReductorApp.this)
                        .runtimeRepl(createRuntimeRepl())
                        .finish())
                .build());

    }

    Handler handler = new Handler();
    private RuntimeReplFactory createRuntimeRepl() {
        return new JsRuntimeReplFactoryBuilder(this)
                .addFunction("getState", new BaseFunction() {
                    @Override
                    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                        final String jsonString = gson.toJson(store.getState());
                        Scriptable json = (Scriptable) scope.get("JSON", scope);
                        Function parseFunction = (Function) json.get("parse", scope);
                        return parseFunction.call(cx, json, scope, new Object[]{jsonString});
                    }
                })
                .addFunction("setState", new BaseFunction() {
                    @Override
                    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                        Scriptable json = (Scriptable) scope.get("JSON", scope);
                        Function stringifyFunction = (Function) json.get("stringify", scope);
                        String jsonString = (String) stringifyFunction.call(cx, json, scope, new Object[]{args[0]});

                        final AppState arg = gson.fromJson(jsonString, AppStateImpl.class);
                        Log.d("ReductorApp", arg.toString());
                        handler.post(() -> store.dispatch(SetStateReducer.setStateAction(arg)));
                        return arg;
                    }
                })
                .build();
    }
}
