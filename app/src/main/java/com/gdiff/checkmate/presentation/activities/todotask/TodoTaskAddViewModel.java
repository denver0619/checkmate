package com.gdiff.checkmate.presentation.activities.todotask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gdiff.checkmate.application.callbacks.GeneralCallback;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.infrastructure.repositories.TodoTasksRepositoryImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoTaskAddViewModel extends AndroidViewModel {
    private final ExecutorService executorService;
    private final Application _context;

    public TodoTaskAddViewModel(@NonNull Application application) {
        super(application);
        this._context = application;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void addTodoTask(TodoTask todoTask, GeneralCallback callback) {
        executorService.submit(
            new Runnable() {
                @Override
                public void run() {
                    TodoTasksRepositoryImpl
                            .getInstance(TodoTaskAddViewModel.this._context)
                            .add(todoTask);
                    callback.onFinish();
                }
            }
        );
    }
}
