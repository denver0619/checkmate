package com.gdiff.checkmate.views.activities.todotask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.infrastructure.repositories.TodoTasksRepositoryImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoTaskAddViewModel extends AndroidViewModel {
    private final ExecutorService executorService;
    private RepositoryOnDataChangedCallback _repositoryCallback;
    private final Application _context;

    public TodoTaskAddViewModel(@NonNull Application application) {
        super(application);
        this._context = application;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void addTodoTask(TodoTask todoTask, RepositoryOnDataChangedCallback callback) {
        TodoTasksRepositoryImpl
                .getInstance(this._context)
                .add(todoTask);
        this._repositoryCallback = callback;
        TodoTasksRepositoryImpl
                .getInstance(this._context)
                        .registerCallback(callback);
    }

    @Override
    public void onCleared() {
        if (this._repositoryCallback != null) {
            TodoTasksRepositoryImpl.getInstance(_context).unregisterCallback(_repositoryCallback);
        }
    }
}
