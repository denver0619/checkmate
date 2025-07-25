package com.gdiff.checkmate.presentation.fragments.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.repositories.GeneralRepositoryCallback;
import com.gdiff.checkmate.domain.repositories.RepositoryListFetchCallback;
import com.gdiff.checkmate.infrastructure.repositories.TodoTasksRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoTaskViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private final ExecutorService executorService;

    private final Application applicationContext;

    public TodoTaskViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
        this.applicationContext = application;
    }

    private final MutableLiveData<List<? extends TaskModel>> _finishedModelList = new MutableLiveData<>();
    private final MutableLiveData<List<? extends TaskModel>> _unfinishedModelList = new MutableLiveData<>();


    public LiveData<List<? extends TaskModel>> getFinishedModelList() {
        return _finishedModelList;
    };
    public LiveData<List<? extends TaskModel>> getUnfinishedModelList() {
        return _unfinishedModelList;
    };

    public void loadData(GeneralRepositoryCallback repositoryCallback) {
        TodoTasksRepositoryImpl.getInstance(this.applicationContext).registerCallback(repositoryCallback);
        reloadData();
    }

    public void reloadData() {
        executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        List<TaskModel> unfinished = new ArrayList<>();
                        List<TaskModel> finished = new ArrayList<>();
                        TodoTasksRepositoryImpl.getInstance(TodoTaskViewModel.this.applicationContext).getAll(
                                new RepositoryListFetchCallback() {
                                    @Override
                                    public void onFetch(List<? extends TaskModel> modelList) {
                                        for (TaskModel model: modelList
                                             ) {
                                            if (model.isDone()) {
                                                finished.add(model);
                                            } else {
                                                unfinished.add(model);
                                            }
                                        }
                                        TodoTaskViewModel.this._finishedModelList.postValue(finished);
                                        TodoTaskViewModel.this._unfinishedModelList.postValue(unfinished);
                                    }
                                }
                        );
                    }
                }
        );
    }

    @Override
    public void onCleared() {
        TodoTasksRepositoryImpl.getInstance(applicationContext).unregisterCallback();
        if (executorService!=null) {
            executorService.shutdown();
        }
    }
}