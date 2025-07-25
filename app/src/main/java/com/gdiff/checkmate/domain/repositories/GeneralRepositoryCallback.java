package com.gdiff.checkmate.domain.repositories;

public interface GeneralRepositoryCallback {
    void onAdd();
    void onUpdate(int id);
    void onDelete(int id);
}
