package com.gdiff.checkmate.domain.repositories;

public interface BaseRepository {
    interface RepositoryCallback{
        void onCallback();
    }
    void registerCallback(RepositoryCallback callback);
    void unregisterCallback(RepositoryCallback callback);
    void unregisterAllCallback();
    void notifyCallbacks();

}
