package com.gdiff.checkmate.domain.repositories;

import com.gdiff.checkmate.domain.models.RepeatingTask;

public interface RepeatingTasksRepository extends BaseRepository {
    void add(RepeatingTask repeatingTask);
    void edit(RepeatingTask repeatingTask);
    void delete(RepeatingTask repeatingTask);
}
