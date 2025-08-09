package com.gdiff.checkmate.domain.repositories;

import androidx.annotation.Nullable;

import com.gdiff.checkmate.domain.models.RepeatingTask;

import java.util.List;

public interface RepeatingTasksRepository extends BaseRepository<RepeatingTask> {
    public void updateAll(List<RepeatingTask> tasks, @Nullable Runnable onFinished);
}
