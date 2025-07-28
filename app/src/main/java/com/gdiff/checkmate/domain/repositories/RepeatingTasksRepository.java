package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.RepeatingTask;

import java.util.List;

public interface RepeatingTasksRepository extends BaseRepository {
    void add(RepeatingTask repeatingTask);
    void update(RepeatingTask repeatingTask);
    void delete(RepeatingTask repeatingTask);
    void deleteAll(List<RepeatingTask> repeatingTasks);
}
