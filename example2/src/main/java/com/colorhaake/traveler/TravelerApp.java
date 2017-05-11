package com.colorhaake.traveler;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.colorhaake.traveler.injection.component.ApplicationComponent;
import com.colorhaake.traveler.injection.component.DaggerApplicationComponent;
import com.colorhaake.traveler.injection.module.ApplicationModule;
import com.colorhaake.traveler.model.AppState;
import com.colorhaake.traveler.reducer.utils.SetStateReducer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.console.RuntimeReplFactory;
import com.facebook.stetho.rhino.JsRuntimeReplFactoryBuilder;
import com.google.gson.Gson;
import com.yheriatovych.reductor.Store;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import javax.inject.Inject;

/**
 * Created by josephcheng on 2017/3/12.
 */
public class TravelerApp extends Application {
    public final String TAG = TravelerApp.class.getName();

    protected ApplicationComponent applicationComponent;

    @Inject public Store<AppState> store;
    @Inject Gson gson;

    public static TravelerApp get(Activity activity) {
        return (TravelerApp) activity.getApplication();
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);

        // for remote debugging
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(
                        () -> new Stetho.DefaultInspectorModulesBuilder(TravelerApp.this)
                            .runtimeRepl(createRuntimeRepl())
                            .finish()
                )
                .build()
        );

        // for using Fresco Image
        Fresco.initialize(this);
    }

    Handler handler = new Handler();
    private RuntimeReplFactory createRuntimeRepl() {
        return new JsRuntimeReplFactoryBuilder(this)
                .addFunction("getState", new BaseFunction() {
                    @Override
                    public Object call(
                            Context cx, Scriptable scope, Scriptable thisObj, Object[] args
                    ) {
                        final String jsonString = gson.toJson(store.getState());
                        Scriptable json = (Scriptable) scope.get("JSON", scope);
                        Function parseFunction = (Function) json.get("parse", scope);
                        return parseFunction.call(cx, json, scope, new Object[]{jsonString});
                    }
                })
                .addFunction("setState", new BaseFunction() {
                    @Override
                    public Object call(
                            Context cx, Scriptable scope, Scriptable thisObj, Object[] args
                    ) {
                        Scriptable json = (Scriptable) scope.get("JSON", scope);
                        Function stringifyFunction = (Function) json.get("stringify", scope);
                        String jsonString = (String) stringifyFunction.call(
                                cx, json, scope, new Object[]{args[0]}
                        );

                        final AppState arg = gson.fromJson(jsonString, AppState.class);
                        Log.d(TAG, arg.toString());
                        handler.post(() -> store.dispatch(SetStateReducer.createSetStateAction(arg)));
                        return arg;
                    }
                })
                .build();
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
