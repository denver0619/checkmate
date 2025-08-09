package com.gdiff.checkmate.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.gdiff.checkmate.application.constants.WorkerConstantNames;
import com.gdiff.checkmate.application.workers.RepeatingTasksStatusUpdateWorker;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class WorkRestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                RepeatingTasksStatusUpdateWorker.class,
                1,
                TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        WorkerConstantNames.updateRepeatingTasks,
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                        workRequest
                );
    }
}
