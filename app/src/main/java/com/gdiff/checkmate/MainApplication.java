package com.gdiff.checkmate;

import android.app.Application;
import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.gdiff.checkmate.application.constants.WorkerConstantNames;
import com.gdiff.checkmate.application.workers.RepeatingTasksStatusUpdateWorker;
import com.google.android.material.color.DynamicColors;

import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                RepeatingTasksStatusUpdateWorker.class,
                15,
                TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        WorkerConstantNames.updateRepeatingTasks,
                        ExistingPeriodicWorkPolicy.KEEP,
                        workRequest
                );

//        OneTimeWorkRequest startupWork = new OneTimeWorkRequest.Builder(RepeatingTasksStatusUpdateWorker.class)
//                .setInitialDelay(10, TimeUnit.SECONDS)
//                .build();
//
//        WorkManager.getInstance(this).enqueue(startupWork);
    }


}
