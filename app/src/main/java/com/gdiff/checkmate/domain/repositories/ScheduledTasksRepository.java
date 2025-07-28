package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.ScheduledTask;

import java.util.List;

public interface ScheduledTasksRepository extends BaseRepository {
    void add(ScheduledTask scheduledTask);
    void update(ScheduledTask scheduledTask);
    void delete(ScheduledTask scheduledTask);
    void deleteAll(List<ScheduledTask> scheduledTasks);
}
