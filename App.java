package com.healbe.healbe_example_andorid;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // just timber debug tree here
        // we can also init out sdk in a blocking way, but we won't
        // for sdk we use splash screen and background initializing, not in ui thread
        Timber.plant(new Timber.DebugTree());
    }
}
