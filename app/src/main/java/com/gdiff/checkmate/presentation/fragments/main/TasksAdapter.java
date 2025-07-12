package com.gdiff.checkmate.presentation.fragments.main;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gdiff.checkmate.R;
import com.gdiff.checkmate.databinding.TaskListItemBinding;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.models.TodoTask;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private OnTaskItemClickListener _onTaskItemClickListener;
    private List<? extends TaskModel> _modelList;

    public TasksAdapter(List<? extends TaskModel> modelList, OnTaskItemClickListener onTaskItemClickListener) {
        this._modelList = modelList;
        this._onTaskItemClickListener = onTaskItemClickListener;
    }

    public interface OnTaskItemClickListener {
        void onTaskClick(TaskModel taskModel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TaskListItemBinding _taskListItemBinding;

        public ViewHolder(@NonNull View itemView, TaskListItemBinding taskListItemBinding) {
            super(itemView);
            this._taskListItemBinding = taskListItemBinding;
        }

        public void bind(TaskModel taskModel) {
            _taskListItemBinding.content.setText(taskModel.content());
            _taskListItemBinding.checkBox.setChecked(taskModel.isDone());
        }


        public void bind(TaskModel taskModel, OnTaskItemClickListener listener) {
            _taskListItemBinding.content.setText(taskModel.content());
            _taskListItemBinding.checkBox.setChecked(taskModel.isDone());
            _taskListItemBinding.itemTile.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onTaskClick(taskModel);
                        }
                    }
            );
        }

        public TaskListItemBinding getBinding() {
            return this._taskListItemBinding;
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        TaskListItemBinding taskListItemBinding = TaskListItemBinding.inflate(inflater, viewGroup, false);
        return new ViewHolder(taskListItemBinding.getRoot(), taskListItemBinding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (_onTaskItemClickListener == null) {
            viewHolder.bind(_modelList.get(position));
        } else {
            viewHolder.bind(_modelList.get(position), _onTaskItemClickListener);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _modelList.size();
    }

    public void updateModels(List<? extends TaskModel> modelList) {
        this._modelList = modelList;
    }

    public void setOnTaskItemClickListener (OnTaskItemClickListener onTaskItemClickListener) {
        this._onTaskItemClickListener = onTaskItemClickListener;
    }


}
