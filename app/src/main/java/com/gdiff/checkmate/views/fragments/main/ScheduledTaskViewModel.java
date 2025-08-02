package com.gdiff.checkmate.views.fragments.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gdiff.checkmate.domain.datatransferobjects.TaskGroupListsDTO;
import com.gdiff.checkmate.domain.models.ScheduledTask;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.repositories.BaseRepository;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.domain.usecases.FetchTasksUseCase;
import com.gdiff.checkmate.infrastructure.repositories.ScheduledTasksRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class ScheduledTaskViewModel extends AndroidViewModel {
    private final Application _applicationContext;
    private RepositoryOnDataChangedCallback _onDataChangedCallback;
    private FetchTasksUseCase fetchTasksUseCase;


    public ScheduledTaskViewModel(@NonNull Application application) {
        super(application);
        this._applicationContext = application;
    }

    private final MutableLiveData<TaskGroupListsDTO> _taskGroupList = new MutableLiveData<>();

    public LiveData<TaskGroupListsDTO> getTaskGroupList() {return this._taskGroupList;}

    public void loadData(RepositoryOnDataChangedCallback onDataChangedCallback) {
        this._onDataChangedCallback = onDataChangedCallback;
        BaseRepository baseRepository = ScheduledTasksRepositoryImpl.getInstance(this._applicationContext);
        this.fetchTasksUseCase = new FetchTasksUseCase(baseRepository);
        ScheduledTasksRepositoryImpl.getInstance(this._applicationContext).registerCallback(this._onDataChangedCallback);
        reloadData();
    }

    public void reloadData() {
        this.fetchTasksUseCase
                .getTasks(
                        new FetchTasksUseCase.Callback() {
                            @Override
                            public void onResult(TaskGroupListsDTO taskGroupListsDTO) {
                                ScheduledTaskViewModel.this._taskGroupList.postValue(taskGroupListsDTO);
                            }
                        }
                );
    }

    public void updateTask(ScheduledTask scheduledTask) {
        ScheduledTasksRepositoryImpl.getInstance(this._applicationContext)
                .update(scheduledTask);
    }

    public void deleteTask(ScheduledTask scheduledTask) {
        ScheduledTasksRepositoryImpl.getInstance(this._applicationContext)
                .delete(scheduledTask);
    }

    public void deleteAllTasks(List<? extends TaskModel<?>> taskModels) {
        List<ScheduledTask> scheduledTasks = new ArrayList<>();
        if (!taskModels.isEmpty()) {
            for (TaskModel<?> taskModel : taskModels) {
                scheduledTasks.add((ScheduledTask) taskModel);
            }
        }

        ScheduledTasksRepositoryImpl.getInstance(this._applicationContext).deleteAll(scheduledTasks);
    }

    @Override
    public void onCleared() {
        if (this._onDataChangedCallback != null) {
            ScheduledTasksRepositoryImpl.getInstance(this._applicationContext).unregisterCallback(this._onDataChangedCallback);
        }
    }
}