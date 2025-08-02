package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.TaskModel;

public interface RepositorySingleFetchResultCallback {
    void onFetch(TaskModel<?> modelList);
}
