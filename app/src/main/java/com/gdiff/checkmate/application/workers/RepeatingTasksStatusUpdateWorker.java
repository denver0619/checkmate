package com.gdiff.checkmate.application.workers;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.gdiff.checkmate.domain.repositories.RepeatingTasksRepository;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.domain.usecases.UpdateRepeatingTasksUseCase;
import com.gdiff.checkmate.infrastructure.repositories.RepeatingTasksRepositoryImpl;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

public class RepeatingTasksStatusUpdateWorker extends ListenableWorker {
    public RepeatingTasksStatusUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        SettableFuture<Result> future = SettableFuture.create();
        RepeatingTasksRepository repeatingTasksRepository = (RepeatingTasksRepository) RepeatingTasksRepositoryImpl.getInstance((Application) getApplicationContext());
        UpdateRepeatingTasksUseCase updateRepeatingTasksUseCase = new UpdateRepeatingTasksUseCase(
                repeatingTasksRepository);
        try {
            updateRepeatingTasksUseCase.updateTasksStatus(
                    new Runnable() {
                        @Override
                        public void run() {
                            future.set(Result.success());
                        }
                    }
            );
        } catch (Exception e) {
            future.set(Result.failure());  // Or Result.retry() depending on use case
        }
        return future;
    }
}
