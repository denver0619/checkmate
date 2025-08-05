package com.gdiff.checkmate.views.fragments.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gdiff.checkmate.domain.datatransferobjects.TaskGroupListsDTO;
import com.gdiff.checkmate.domain.models.RepeatingTask;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.repositories.BaseRepository;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.domain.usecases.FetchTasksUseCase;
import com.gdiff.checkmate.infrastructure.repositories.RepeatingTasksRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class RepeatingTaskViewModel extends AndroidViewModel {
    private final Application _context;
    private RepositoryOnDataChangedCallback _onDataChangedCallback;
    private FetchTasksUseCase _feFetchTasksUseCase;

    public RepeatingTaskViewModel(@NonNull Application application) {
        super(application);
        this._context = application;
    }

    private final MutableLiveData<TaskGroupListsDTO> _taskGroupList = new MutableLiveData<>();

    public LiveData<TaskGroupListsDTO> getTaskGroupList() {return this._taskGroupList;}

    public void loadData(RepositoryOnDataChangedCallback onDataChangedCallback) {
        this._onDataChangedCallback = onDataChangedCallback;
        BaseRepository baseRepository = RepeatingTasksRepositoryImpl.getInstance(this._context);
        this._feFetchTasksUseCase = new FetchTasksUseCase(baseRepository);
        RepeatingTasksRepositoryImpl.getInstance(this._context)
                .registerCallback(this._onDataChangedCallback);
        this.reloadData();
    }

    public void reloadData() {
        this._feFetchTasksUseCase.getTasks(
                new FetchTasksUseCase.Callback() {
                    @Override
                    public void onResult(TaskGroupListsDTO taskGroupListsDTO) {
                        RepeatingTaskViewModel.this._taskGroupList.postValue(taskGroupListsDTO);
                    }
                }
        );
    }

    public void updateTask(RepeatingTask repeatingTask) {
        RepeatingTasksRepositoryImpl.getInstance(this._context)
                .update(repeatingTask);
    }

    public void deleteTask(RepeatingTask repeatingTask) {
        RepeatingTasksRepositoryImpl.getInstance(this._context)
                .delete(repeatingTask);
    }

    public void deleteAllTasks(List<? extends TaskModel<?>> taskModels) {
        List<RepeatingTask> repeatingTasks = new ArrayList<>();
        if (!taskModels.isEmpty()) {
            for (TaskModel<?> taskModel : taskModels) {
                repeatingTasks.add((RepeatingTask) taskModel);
            }
        }
        RepeatingTasksRepositoryImpl.getInstance(this._context)
                .deleteAll(repeatingTasks);
    }

    @Override
    public void onCleared() {
        if (this._onDataChangedCallback != null) {
            RepeatingTasksRepositoryImpl.getInstance(this._context)
                    .unregisterCallback(this._onDataChangedCallback);
        }
    }
}