package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.ScheduledTask;

import java.util.List;

public interface ScheduledTasksRepository extends BaseRepository {
    List<ScheduledTask> getAll();
    ScheduledTask getById(int id);
    void add(ScheduledTask scheduledTask);
    void edit(ScheduledTask scheduledTask);
    void delete(ScheduledTask scheduledTask);
}
