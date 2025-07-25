package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.TaskModel;

import java.util.List;

public interface RepositoryListFetchCallback {
    void onFetch(List<? extends TaskModel> modelList);
}
