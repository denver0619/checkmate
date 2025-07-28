package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.TodoTask;

import java.util.List;

public interface TodoTasksRepository extends BaseRepository{
    void add(TodoTask todoTask);
    void update(TodoTask todoTask);
    void delete(TodoTask todoTask);
    void deleteAll(List<TodoTask> todoTasks);
}
