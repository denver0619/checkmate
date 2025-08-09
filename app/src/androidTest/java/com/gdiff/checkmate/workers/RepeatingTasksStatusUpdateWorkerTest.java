package com.gdiff.checkmate.workers;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.TestDriver;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.gdiff.checkmate.application.workers.RepeatingTasksStatusUpdateWorker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class RepeatingTasksStatusUpdateWorkerTest {
    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();

        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(new SynchronousExecutor())
                .build();

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config);
    }

    @Test
    public void testPeriodicWorkSuccessfully() throws Exception {

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                RepeatingTasksStatusUpdateWorker.class,
                1, TimeUnit.HOURS
        ).build();

        WorkManager wm = WorkManager.getInstance(context);
        wm.enqueue(request).getResult().get();

        TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver();

        // Tell the test environment that initial delay has passed
        assertNotNull(testDriver);
        testDriver.setInitialDelayMet(request.getId());

        // Now, get the WorkInfo
        WorkInfo info = wm.getWorkInfoById(request.getId()).get();

        assertThat(info.getState(), is(WorkInfo.State.ENQUEUED));
    }
}
