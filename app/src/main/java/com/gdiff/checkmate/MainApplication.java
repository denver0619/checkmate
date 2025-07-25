package com.gdiff.checkmate;

import android.app.Application;
import android.content.Context;

import com.google.android.material.color.DynamicColors;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
