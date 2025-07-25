package com.gdiff.checkmate.presentation.fragments.main;

import androidx.recyclerview.widget.DiffUtil;

import com.gdiff.checkmate.domain.models.TaskModel;

import java.util.List;
import java.util.Objects;

//https://stackoverflow.com/questions/71441531/how-to-use-diffutils-in-recyclerviewadapter-on-android
//note: override .equals() and .hasCode method of models or any objects that will be used for comparison;

public class TasksDiffUtilCallback extends DiffUtil.Callback {
    private List<TasksAdapterViewType> _oldList;
    private List<TasksAdapterViewType> _newList;

    public TasksDiffUtilCallback(List<TasksAdapterViewType> oldList, List<TasksAdapterViewType> newList) {
        this._oldList = oldList;
        this._newList = newList;
    }

    @Override
    public int getOldListSize() {
        return this._oldList.size();
    }

    @Override
    public int getNewListSize() {
        return this._newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        TasksAdapterViewType oldItem = this._oldList.get(oldItemPosition);
        TasksAdapterViewType newItem = this._newList.get(newItemPosition);
        if (oldItem.getType() != newItem.getType()) return false;

        if (oldItem.getType() >= TasksAdapterViewType.TASK_ITEM_UNFINISHED) {
            TaskModel oldTaskModel = oldItem.getTaskModel();
            TaskModel newTaskModel = newItem.getTaskModel();
            return oldTaskModel != null && oldTaskModel.equals(newTaskModel);
        } else {
            return Objects.equals(oldItem.getHeaderTitle(), newItem.getHeaderTitle());
        }
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        TasksAdapterViewType oldItem = this._oldList.get(oldItemPosition);
        TasksAdapterViewType newItem = this._newList.get(newItemPosition);
        if (oldItem.getType()>= TasksAdapterViewType.TASK_ITEM_UNFINISHED){
            return oldItem.getTaskModel().equals(newItem.getTaskModel());
        } else {
            return Objects.equals(oldItem.getHeaderTitle(), newItem.getHeaderTitle());
        }
    }
}
