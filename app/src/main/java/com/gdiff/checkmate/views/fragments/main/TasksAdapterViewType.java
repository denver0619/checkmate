package com.gdiff.checkmate.views.fragments.main;

import com.gdiff.checkmate.domain.models.TaskModel;

public final class TasksAdapterViewType {
    public final static int TASK_HEADER_UNFINISHED = 0;
    public final static int TASK_HEADER_FINISHED = 1;
    public final static int TASK_HEADER_EXPIRED = 2;
    public final static int TASK_ITEM_UNFINISHED = 3;
    public final static int TASK_ITEM_FINISHED = 4;
    public final static int TASK_ITEM_EXPIRED = 5;

    private final int _type;
    private String _title;
    private TaskModel<?> _taskModel;
    public TasksAdapterViewType(int type) {
        this._type = type;
    }
    public int getType() {
        return this._type;
    }

    public void setHeaderTitle(String headerTitle) {
        this._title = headerTitle;
    }

    public String getHeaderTitle() {
        return (this._title!= null) ? this._title : "";
    }

    public void setTaskModel(TaskModel taskModel) {
        this._taskModel = taskModel;
    }

    public TaskModel<?> getTaskModel() {
        return _taskModel;
    }
}
