package com.gdiff.checkmate.views.activities.todotask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.infrastructure.repositories.TodoTasksRepositoryImpl;

public class TodoTaskEditViewModel extends AndroidViewModel {
    private final Application _application;
    private RepositoryOnDataChangedCallback _onDataChangedCallback;

    public TodoTaskEditViewModel(@NonNull Application application) {
        super(application);
        this._application = application;
    }

    public void editTask(TodoTask todoTask, RepositoryOnDataChangedCallback onDataChangedCallback) {
        this._onDataChangedCallback = onDataChangedCallback;
        TodoTasksRepositoryImpl.getInstance(this._application)
                .registerCallback(this._onDataChangedCallback);
        TodoTasksRepositoryImpl.getInstance(this._application)
                .update(todoTask);
    }

    @Override
    public void onCleared() {
        if (this._onDataChangedCallback != null) {
            TodoTasksRepositoryImpl.getInstance(_application)
                    .unregisterCallback(this._onDataChangedCallback);
        }
    }
}
