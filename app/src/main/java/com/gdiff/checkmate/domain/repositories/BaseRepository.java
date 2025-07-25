package com.gdiff.checkmate.domain.repositories;

public interface BaseRepository {
    void registerCallback(GeneralRepositoryCallback callback);
    void unregisterCallback();
}
