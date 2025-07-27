package com.gdiff.checkmate.domain.datatransferobjects;

import com.gdiff.checkmate.domain.models.TaskModel;

import java.util.List;

public class TaskGroupListsDTO {
    private List<? extends TaskModel> _finishedTasks;
    private List<? extends TaskModel> _unfinishedTasks;
    private List<? extends TaskModel> _expiredTasks;

    public TaskGroupListsDTO() {}

    public TaskGroupListsDTO(List<? extends TaskModel> finishedTasks,
                             List<? extends TaskModel> unfinishedTasks,
                             List<? extends TaskModel> expiredTasks) {
        this._finishedTasks = finishedTasks;
        this._unfinishedTasks = unfinishedTasks;
        this._expiredTasks = expiredTasks;
    }

    public void setFinishedTasks(List<? extends TaskModel> finishedTasks) {
        this._finishedTasks = finishedTasks;
    }

    public List<? extends TaskModel> getFinishedTasks() {
        return this._finishedTasks;
    }

    public void setUnfinishedTasks(List<? extends TaskModel> unfinishedTasks) {
        this._unfinishedTasks = unfinishedTasks;
    }

    public List<? extends TaskModel> getUnfinishedTasks() {
        return  this._unfinishedTasks;
    }

    public void setExpiredTasks(List<? extends TaskModel> expiredTasks) {
        this._expiredTasks = expiredTasks;
    }

    public List<? extends TaskModel>  getExpiredTasks() {
        return this._expiredTasks;
    }
}
