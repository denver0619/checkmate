package com.gdiff.checkmate.domain.repositories;

public interface BaseRepository {
    void registerCallback(RepositoryOnDataChangedCallback callback);
    void unregisterCallback(RepositoryOnDataChangedCallback callback);
    void getAll(RepositoryListFetchResultCallback fetchCallback);
    void getById(int id, RepositorySingleFetchResultCallback fetchCallback);
    void onDestroy();
}
