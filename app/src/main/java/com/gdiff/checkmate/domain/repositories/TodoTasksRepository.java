package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.TodoTask;
import java.util.List;

public interface TodoTasksRepository extends BaseRepository{
    List<TodoTask> getAll();
    List<TodoTask> getAll(RepositoryListFetchCallback fetchCallback);
    TodoTask getById(int id);
    TodoTask getById(int id, RepositorySingleFetchCallback fetchCallback);
    void add(TodoTask todoTask);
    void edit(TodoTask todoTask);
    void delete(TodoTask todoTask);
}
