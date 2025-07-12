package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.TodoTask;

import java.util.List;

public interface TodoTasksRepository extends BaseRepository{
    List<TodoTask> getAll();
    TodoTask getById(int id);
    void add(TodoTask todoTask);
    void edit(TodoTask todoTask);
    void delete(TodoTask todoTask);
}
