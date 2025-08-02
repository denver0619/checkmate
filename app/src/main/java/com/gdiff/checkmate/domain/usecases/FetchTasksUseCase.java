package com.gdiff.checkmate.domain.usecases;

import android.util.Log;

import com.gdiff.checkmate.domain.datatransferobjects.TaskGroupListsDTO;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.repositories.BaseRepository;
import com.gdiff.checkmate.domain.repositories.RepositoryListFetchResultCallback;

import java.util.ArrayList;
import java.util.List;

public class FetchTasksUseCase {
    private final BaseRepository _repository;

    public interface Callback {
        public void onResult(TaskGroupListsDTO taskGroupListsDTO);
    }

    public FetchTasksUseCase(BaseRepository repository) {
        this._repository = repository;
    }

    public void getTasks(FetchTasksUseCase.Callback callback) {
        _repository.getAll(
                new RepositoryListFetchResultCallback() {
                    @Override
                    public void onFetch(List<? extends TaskModel<?>> modelList) {
                        TaskGroupListsDTO result = new TaskGroupListsDTO();
                        List<TaskModel<?>> unfinishedTasks = new ArrayList<>();
                        List<TaskModel<?>> finishedTasks = new ArrayList<>();
                        List<TaskModel<?>> expiredTasks = new ArrayList<>();

                        for (TaskModel<?> model : modelList) {
                            if (model.isExpired()) {
                                expiredTasks.add(model);
                            } else if (model.isDone()) {
                                finishedTasks.add(model);
                            } else {
                                unfinishedTasks.add(model);
                            }
                        }

                        result.setUnfinishedTasks(unfinishedTasks);
                        result.setFinishedTasks(finishedTasks);
                        result.setExpiredTasks(expiredTasks);
                        callback.onResult(result);
                    }
                }
        );
    }
}
