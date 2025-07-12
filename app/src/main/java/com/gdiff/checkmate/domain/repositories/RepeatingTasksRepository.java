package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.RepeatingTask;

import java.util.List;

public interface RepeatingTasksRepository extends BaseRepository {
    List<RepeatingTask> getAll();
    RepeatingTask getById(int id);
    void add(RepeatingTask repeatingTask);
    void edit(RepeatingTask repeatingTask);
    void delete(RepeatingTask repeatingTask);
}
