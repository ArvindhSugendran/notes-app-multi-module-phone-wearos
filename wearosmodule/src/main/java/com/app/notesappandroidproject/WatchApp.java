package com.app.notesappandroidproject;

import android.app.Application;

import com.app.notesappandroidproject.utils.AppExecutors;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class WatchApp extends Application {
    private static WatchApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static WatchApp getInstance() {
        return instance;
    }

}
