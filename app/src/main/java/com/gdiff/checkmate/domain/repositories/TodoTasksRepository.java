package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.TodoTask;

public interface TodoTasksRepository extends BaseRepository{
    void add(TodoTask todoTask);
    void edit(TodoTask todoTask);
    void delete(TodoTask todoTask);
}
