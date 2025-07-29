package com.gdiff.checkmate.presentation.fragments.main;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gdiff.checkmate.domain.datatransferobjects.TaskGroupListsDTO;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.BaseRepository;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.domain.usecases.FetchTasksUseCase;
import com.gdiff.checkmate.infrastructure.repositories.TodoTasksRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class TodoTaskViewModel extends AndroidViewModel {

    private final Application _applicationContext;
    private RepositoryOnDataChangedCallback _repositoryCallback;
    FetchTasksUseCase _fetchTasksUseCase;

    public TodoTaskViewModel(@NonNull Application application) {
        super(application);
        this._applicationContext = application;
    }

    private final MutableLiveData<TaskGroupListsDTO> _taskGroupLists = new MutableLiveData<>();


    public LiveData<TaskGroupListsDTO> getTaskGroupLists() {
        return _taskGroupLists;
    };

    public void loadData(RepositoryOnDataChangedCallback repositoryCallback) {
        this._repositoryCallback = repositoryCallback;
        BaseRepository baseRepository = TodoTasksRepositoryImpl.getInstance(_applicationContext);
        this._fetchTasksUseCase = new FetchTasksUseCase(baseRepository);
        TodoTasksRepositoryImpl.getInstance(this._applicationContext).registerCallback(repositoryCallback);
        reloadData();
    }

    public void reloadData() {
        this._fetchTasksUseCase.getTasks(
                new FetchTasksUseCase.Callback() {
                    @Override
                    public void onResult(TaskGroupListsDTO taskGroupListsDTO) {
                        TodoTaskViewModel.this._taskGroupLists.postValue(taskGroupListsDTO);
                    }
                }
        );

    }

    public void removeTask(TodoTask todoTask) {
        TodoTasksRepositoryImpl.getInstance(_applicationContext)
                .delete(todoTask);
    }

    public void updateTask(TodoTask todoTask) {
        TodoTasksRepositoryImpl.getInstance(_applicationContext)
                .update(todoTask);
    }

    public void removeAllTasks(List<? extends TaskModel> taskModels) {
        List<TodoTask> todoTasks = new ArrayList<>();
        if (taskModels != null) {
            for (TaskModel taskModel : taskModels) {
                todoTasks.add((TodoTask) taskModel);
            }
        }

        TodoTasksRepositoryImpl.getInstance(_applicationContext)
                .deleteAll(todoTasks);
    }

    @Override
    public void onCleared() {
        if (this._repositoryCallback != null) {
            TodoTasksRepositoryImpl.getInstance(_applicationContext).unregisterCallback(_repositoryCallback);
        }
    }
}