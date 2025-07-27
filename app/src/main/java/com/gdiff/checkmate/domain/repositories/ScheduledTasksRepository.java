package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.ScheduledTask;

public interface ScheduledTasksRepository extends BaseRepository {
    void add(ScheduledTask scheduledTask);
    void edit(ScheduledTask scheduledTask);
    void delete(ScheduledTask scheduledTask);
}
