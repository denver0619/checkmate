package com.gdiff.checkmate.presentation.fragments.main;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gdiff.checkmate.MainApplication;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.repositories.BaseRepository;
import com.gdiff.checkmate.infrastructure.repositories.TodoTasksRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoTaskViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private final ExecutorService executorService;
    private final List<BaseRepository.RepositoryCallback> _callbacks = new ArrayList<>();

    private final Application applicationContext;

    public TodoTaskViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
        this.applicationContext = application;
    }

    private final MutableLiveData<List<? extends TaskModel>> modelList = new MutableLiveData<>();


    public LiveData<List<? extends TaskModel>> getModelList() {
        return modelList;
    };

    public void loadData() {
        BaseRepository.RepositoryCallback callback = new BaseRepository.RepositoryCallback() {
            @Override
            public void onCallback() {
                TodoTaskViewModel.this.reloadData();
            }
        };

        this._callbacks.add(callback);

        TodoTasksRepositoryImpl.getInstance(this.applicationContext).registerCallback(callback);
        reloadData();
    }

    public void reloadData() {
        executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        modelList.postValue(
                                TodoTasksRepositoryImpl.getInstance(TodoTaskViewModel.this.applicationContext).getAll()
                        );
                    }
                }
        );
    }

    @Override
    public void onCleared() {
        for(BaseRepository.RepositoryCallback callback: _callbacks) {
            TodoTasksRepositoryImpl.getInstance(this.applicationContext).unregisterCallback(callback);
        }
        if (executorService!=null) {
            executorService.shutdown();
        }
    }
}