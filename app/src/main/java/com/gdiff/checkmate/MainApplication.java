package com.gdiff.checkmate;

import android.app.Application;
import android.content.Context;

import com.google.android.material.color.DynamicColors;

public class MainApplication extends Application {
    private static Context _staticApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        this._staticApplicationContext = getApplicationContext();
    }

    public static Context getStaticApplicationContext() {
        return _staticApplicationContext;
    }
}
