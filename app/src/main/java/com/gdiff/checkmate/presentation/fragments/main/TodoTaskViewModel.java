package com.gdiff.checkmate.presentation.fragments.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gdiff.checkmate.domain.datatransferobjects.TaskGroupListsDTO;
import com.gdiff.checkmate.domain.repositories.BaseRepository;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.domain.usecases.FetchTasksUseCase;
import com.gdiff.checkmate.infrastructure.repositories.TodoTasksRepositoryImpl;

public class TodoTaskViewModel extends AndroidViewModel {

    private final Application applicationContext;

    public TodoTaskViewModel(@NonNull Application application) {
        super(application);
        this.applicationContext = application;
    }

    private final MutableLiveData<TaskGroupListsDTO> _taskGroupLists = new MutableLiveData<>();


    public LiveData<TaskGroupListsDTO> getTaskGroupLists() {
        return _taskGroupLists;
    };

    public void loadData(RepositoryOnDataChangedCallback repositoryCallback) {
        TodoTasksRepositoryImpl.getInstance(this.applicationContext).registerCallback(repositoryCallback);
        reloadData();
    }

    public void reloadData() {
        BaseRepository baseRepository = TodoTasksRepositoryImpl.getInstance(applicationContext);
        FetchTasksUseCase fetchTasksUseCase = new FetchTasksUseCase(baseRepository);
        fetchTasksUseCase.getTasks(
                new FetchTasksUseCase.Callback() {
                    @Override
                    public void onResult(TaskGroupListsDTO taskGroupListsDTO) {
                        TodoTaskViewModel.this._taskGroupLists.postValue(taskGroupListsDTO);
                    }
                }
        );

    }

    @Override
    public void onCleared() {
        TodoTasksRepositoryImpl.getInstance(applicationContext).unregisterCallback();
        TodoTasksRepositoryImpl.getInstance(applicationContext).onDestroy();
    }
}