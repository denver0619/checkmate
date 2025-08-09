package com.gdiff.checkmate.domain.usecases;

import androidx.annotation.Nullable;

import com.gdiff.checkmate.domain.models.RepeatingTask;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.repositories.RepeatingTasksRepository;
import com.gdiff.checkmate.domain.repositories.RepositoryListFetchResultCallback;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateRepeatingTasksUseCase {
    private RepeatingTasksRepository _repeatingTasksRepository;

    public UpdateRepeatingTasksUseCase(RepeatingTasksRepository repeatingTasksRepository) {
        this._repeatingTasksRepository = repeatingTasksRepository;
    }

    public void updateTasksStatus(@Nullable Runnable onFinish) {
        this._repeatingTasksRepository.getAll(
                new RepositoryListFetchResultCallback() {
                    @Override
                    public void onFetch(List<? extends TaskModel<?>> modelList) {
                        LocalDate localDate = LocalDate.now();
                        List<RepeatingTask> repeatingTasksUpdate = new ArrayList<>();
                        for (TaskModel<?> taskModel : modelList) {
                            RepeatingTask repeatingTask = (RepeatingTask) taskModel;
                            if (repeatingTask.getCurrentCompleted() == null) continue;
                            if (isDueForReset(localDate, repeatingTask.getCurrentCompleted(), repeatingTask.getInterval())) {
                                repeatingTask.setLastCompleted(repeatingTask.getCurrentCompleted());
                                repeatingTask.setStatus(false);
                                repeatingTasksUpdate.add(repeatingTask);
                            }
                        }
                        UpdateRepeatingTasksUseCase.this
                                ._repeatingTasksRepository
                                .updateAll(
                                        repeatingTasksUpdate, onFinish
                                );
                    }
                }
        );
    }

    public static Boolean isDueForReset(LocalDate dateNow,Date modelCurrentCompleted, int interval) {
        LocalDate currentCompleted = modelCurrentCompleted.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return !dateNow.isBefore(currentCompleted.plusDays(interval));
    };

//    public void updateTest() {
//        this._repeatingTasksRepository.getAll(
//                new RepositoryListFetchResultCallback() {
//                    @Override
//                    public void onFetch(List<? extends TaskModel<?>> modelList) {
//                        for (TaskModel<?> taskModel : modelList) {
//                            RepeatingTask repeatingTask = (RepeatingTask) taskModel;
//                            repeatingTask.setStatus(!repeatingTask.getStatus());
//                            _repeatingTasksRepository.update(repeatingTask);
//                        }
//                    }
//                }
//        );
//    }
}
