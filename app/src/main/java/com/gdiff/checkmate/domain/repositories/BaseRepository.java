package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.RepeatingTask;
import com.gdiff.checkmate.domain.models.TaskModel;

import java.util.List;

public interface BaseRepository<T extends TaskModel<?>> {
    void registerCallback(RepositoryOnDataChangedCallback callback);
    void unregisterCallback(RepositoryOnDataChangedCallback callback);
    void getAll(RepositoryListFetchResultCallback fetchCallback);
    void getById(int id, RepositorySingleFetchResultCallback fetchCallback);
    void add(T task);
    void update(T task);
    void updateAll(List<T> tasks);
    void delete(T task);
    void deleteAll(List<T> tasks);
    void onDestroy();
}
