package com.gdiff.checkmate.presentation.fragments.main;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gdiff.checkmate.databinding.ItemGroupHeaderBinding;
import com.gdiff.checkmate.databinding.ItemTaskListBinding;
import com.gdiff.checkmate.domain.models.TaskModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//reference material
//https://www.digitalocean.com/community/tutorials/android-recyclerview-example
public class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnTaskItemClickListener _onTaskItemClickListener;
    private OnDeleteAllItemClickListener _onDeleteAllItemClickListener;
    private OnTaskDoneClickListener _onOnTaskDoneClickListener;
    private List<TasksAdapterViewType> _viewList;
    private List<? extends TaskModel> _unfinishedModelList;
    private List<? extends TaskModel> _finishedModelList;
    private List<? extends TaskModel> _expiredModelList;

    private final class HeaderTitles {
        private static final String UNFINISHED_TITLE = "Unfinished Tasks";
        private static final String FINISHED_TITLE = "Finished Tasks";
        private static final String EXPIRED_TITLE = "Expired Tasks";
    }

    public TasksAdapter() {
        this._viewList = new ArrayList<>();
        this._unfinishedModelList = new ArrayList<>();
        this._finishedModelList = new ArrayList<>();
        this._expiredModelList = new ArrayList<>();
    }

    public interface OnTaskItemClickListener {
        void onTaskClick(TaskModel taskModel);
    }

    public interface OnDeleteAllItemClickListener {
        void onDeleteAllClick(List<?extends TaskModel> taskModels, String taskGroupTitle);
    }

    public interface OnTaskDoneClickListener {
        void onTaskDone(TaskModel taskModel, boolean isDone);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskListBinding _taskListItemBinding;
        private TaskModel _taskModel;

        public ItemViewHolder(@NonNull View itemView, ItemTaskListBinding taskListItemBinding) {
            super(itemView);
            this._taskListItemBinding = taskListItemBinding;
        }

        public void bind(TaskModel taskModel, int itemType) {
            this._taskModel = taskModel;
            this._taskListItemBinding.content.setText(taskModel.content());
            this._taskListItemBinding.checkBox.setChecked(taskModel.isDone());

            switch (itemType) {
                case TasksAdapterViewType.TASK_ITEM_FINISHED:
                case TasksAdapterViewType.TASK_ITEM_EXPIRED:
                    this._taskListItemBinding.content.setPaintFlags(
                            this._taskListItemBinding.content.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
                    );
                    break;
                default:
            }
        }


        public void bind(TaskModel taskModel, OnTaskItemClickListener listener, int itemType) {
            this._taskModel = taskModel;
            this._taskListItemBinding.content.setText(taskModel.content());
            this._taskListItemBinding.checkBox.setChecked(taskModel.isDone());
            this._taskListItemBinding.fgCard.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onTaskClick(taskModel);
                        }
                    }
            );

            switch (itemType) {
                case TasksAdapterViewType.TASK_ITEM_FINISHED:
                case TasksAdapterViewType.TASK_ITEM_EXPIRED:
                    this._taskListItemBinding.content.setPaintFlags(
                            this._taskListItemBinding.content.getPaintFlags()
                                    | Paint.STRIKE_THRU_TEXT_FLAG
                    );
                    break;
                default:
            }
        }
        public ItemTaskListBinding getBinding() {
            return this._taskListItemBinding;
        }

        public TaskModel getTaskModel() {
            return this._taskModel;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemGroupHeaderBinding _groupHeaderBinding;
        private List<? extends TaskModel> _taskModels;

        public HeaderViewHolder(@NonNull View itemView, ItemGroupHeaderBinding groupHeaderBinding) {
            super(itemView);
            this._groupHeaderBinding = groupHeaderBinding;
        }

        public void bind(List<? extends TaskModel> taskModels, String headerTitle, int headerType) {
            this._taskModels = taskModels;
            this._groupHeaderBinding.headerTitle.setText(headerTitle);
        }

        public void bind(List<? extends TaskModel> taskModels, String headerTitle, int headerType, OnDeleteAllItemClickListener listener) {
            this._taskModels = taskModels;
            this._groupHeaderBinding.headerTitle.setText(headerTitle);
            this._groupHeaderBinding.buttonDeleteAll.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (headerType == TasksAdapterViewType.TASK_HEADER_UNFINISHED) {
                                listener.onDeleteAllClick(TasksAdapter.this._unfinishedModelList, headerTitle);
                            } else if (headerType ==  TasksAdapterViewType.TASK_HEADER_FINISHED) {
                                listener.onDeleteAllClick(TasksAdapter.this._finishedModelList, headerTitle);
                            } else if (headerType == TasksAdapterViewType.TASK_HEADER_EXPIRED) {
                                listener.onDeleteAllClick(TasksAdapter.this._expiredModelList, headerTitle);
                            }
                        }
                    }
            );
        }

        public ItemGroupHeaderBinding getBinding() {
            return this._groupHeaderBinding;
        }

        public List<? extends TaskModel> getTaskModels() {
            return this._taskModels;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TasksAdapterViewType.TASK_HEADER_UNFINISHED
        || viewType == TasksAdapterViewType.TASK_HEADER_FINISHED
        || viewType == TasksAdapterViewType.TASK_HEADER_EXPIRED) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemGroupHeaderBinding itemGroupHeaderBinding = ItemGroupHeaderBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(itemGroupHeaderBinding.getRoot(), itemGroupHeaderBinding);
        }
        else  {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemTaskListBinding itemTaskListBinding = ItemTaskListBinding.inflate(inflater, parent, false);
            return new ItemViewHolder(itemTaskListBinding.getRoot(), itemTaskListBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        TasksAdapterViewType currentViewType = this._viewList.get(adapterPosition);
        if (currentViewType != null ) {
            switch (currentViewType.getType()) {
                case TasksAdapterViewType.TASK_HEADER_UNFINISHED:
                    HeaderViewHolder unfinishedHeaderHolder = ((HeaderViewHolder) holder);
                    unfinishedHeaderHolder.getBinding().buttonDeleteAll.setVisibility(View.GONE);
                    unfinishedHeaderHolder.bind(this._unfinishedModelList, currentViewType.getHeaderTitle(), TasksAdapterViewType.TASK_HEADER_UNFINISHED);
                    break;
                case TasksAdapterViewType.TASK_HEADER_FINISHED:
                    HeaderViewHolder finishedHeaderHolder = ((HeaderViewHolder) holder);
                    finishedHeaderHolder.getBinding().buttonDeleteAll.setVisibility(View.VISIBLE);
                    if (this._onDeleteAllItemClickListener == null) {
                        finishedHeaderHolder.bind(this._finishedModelList, currentViewType.getHeaderTitle(), TasksAdapterViewType.TASK_HEADER_FINISHED);
                    }
                    else {
                        finishedHeaderHolder.bind(this._finishedModelList, currentViewType.getHeaderTitle(), TasksAdapterViewType.TASK_HEADER_FINISHED, this._onDeleteAllItemClickListener);
                    }
                    break;
                case TasksAdapterViewType.TASK_HEADER_EXPIRED:
                    HeaderViewHolder expiredHeaderHolder = ((HeaderViewHolder) holder);
                    expiredHeaderHolder.getBinding().buttonDeleteAll.setVisibility(View.VISIBLE);
                    if (this._onDeleteAllItemClickListener == null) {
                        expiredHeaderHolder.bind(this._expiredModelList, currentViewType.getHeaderTitle(), TasksAdapterViewType.TASK_HEADER_EXPIRED);
                    }
                    else {
                        expiredHeaderHolder.bind(this._expiredModelList, currentViewType.getHeaderTitle(), TasksAdapterViewType.TASK_HEADER_EXPIRED, this._onDeleteAllItemClickListener);
                    }
                    break;
                case TasksAdapterViewType.TASK_ITEM_UNFINISHED:
                    ItemViewHolder unfinishedItemViewHolder = ((ItemViewHolder) holder);
                    unfinishedItemViewHolder.getBinding().fgCard.animate().translationX(0f).setDuration(300).start();
                    unfinishedItemViewHolder.getBinding().checkBox.setOnCheckedChangeListener(null);
                    unfinishedItemViewHolder.getBinding().checkBox.setChecked(
                            this._viewList.get(adapterPosition).getTaskModel().isDone()
                    );
                    if (this._onOnTaskDoneClickListener != null) {
                        if (adapterPosition == RecyclerView.NO_POSITION) return;
                        TaskModel model = _viewList.get(adapterPosition).getTaskModel();
                        unfinishedItemViewHolder.getBinding().checkBox.setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {
                                        TasksAdapter.this._onOnTaskDoneClickListener.onTaskDone(
                                                model,
                                                b
                                        );
                                    }
                                }
                        );
                    }
                    if (this._onTaskItemClickListener == null) {
                        unfinishedItemViewHolder.bind(this._viewList.get(adapterPosition).getTaskModel(), TasksAdapterViewType.TASK_ITEM_UNFINISHED);
                    }
                    else {
                        unfinishedItemViewHolder.bind(this._viewList.get(adapterPosition).getTaskModel(), this._onTaskItemClickListener, TasksAdapterViewType.TASK_ITEM_UNFINISHED);
                    }
                    break;
                case TasksAdapterViewType.TASK_ITEM_FINISHED:
                    ItemViewHolder finishedItemViewHolder = ((ItemViewHolder) holder);
                    finishedItemViewHolder.getBinding().fgCard.animate().translationX(0f).setDuration(300).start();
                    finishedItemViewHolder.getBinding().checkBox.setOnCheckedChangeListener(null);
                    finishedItemViewHolder.getBinding().checkBox.setChecked(
                            this._viewList.get(adapterPosition).getTaskModel().isDone()
                    );
                    if (this._onOnTaskDoneClickListener != null) {
                        if (adapterPosition == RecyclerView.NO_POSITION) return;
                        TaskModel model = _viewList.get(adapterPosition).getTaskModel();
                        finishedItemViewHolder.getBinding().checkBox.setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {
                                        TasksAdapter.this._onOnTaskDoneClickListener.onTaskDone(
                                                model,
                                                b
                                        );
                                    }
                                }
                        );
                    }
                    if (this._onTaskItemClickListener == null) {
                        finishedItemViewHolder.bind(this._viewList.get(adapterPosition).getTaskModel(), TasksAdapterViewType.TASK_ITEM_FINISHED);
                    }
                    else {
                        finishedItemViewHolder.bind(this._viewList.get(adapterPosition).getTaskModel(), this._onTaskItemClickListener, TasksAdapterViewType.TASK_ITEM_FINISHED);
                    }
                    break;
                case TasksAdapterViewType.TASK_ITEM_EXPIRED:
                    ItemViewHolder expiredItemViewHolder = ((ItemViewHolder) holder);
                    expiredItemViewHolder.getBinding().fgCard.animate().translationX(0f).setDuration(300).start();
                    expiredItemViewHolder.getBinding().checkBox.setChecked(
                            this._viewList.get(adapterPosition).getTaskModel().isDone()
                    );
                    expiredItemViewHolder.getBinding().checkBox.setEnabled(false);
                    if (this._onTaskItemClickListener == null) {
                        expiredItemViewHolder.bind(this._viewList.get(adapterPosition).getTaskModel(), TasksAdapterViewType.TASK_ITEM_EXPIRED);
                    }
                    else {
                        expiredItemViewHolder.bind(this._viewList.get(adapterPosition).getTaskModel(), this._onTaskItemClickListener, TasksAdapterViewType.TASK_ITEM_EXPIRED);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this._viewList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return _viewList.get(position).getType();
    }

    public void updateModels(List<? extends TaskModel> unfinishedModels,
                             List<? extends TaskModel> finishedModels,
                             List<? extends TaskModel> expiredModels) {
        this._unfinishedModelList = unfinishedModels;
        this._finishedModelList = finishedModels;
        this._expiredModelList = expiredModels;
        List<TasksAdapterViewType> newViewList = new ArrayList<>();

        if (!unfinishedModels.isEmpty()||!finishedModels.isEmpty()||!expiredModels.isEmpty()) {
            TasksAdapterViewType unfinishedHeader = new TasksAdapterViewType(TasksAdapterViewType.TASK_HEADER_UNFINISHED);
            unfinishedHeader.setHeaderTitle(HeaderTitles.UNFINISHED_TITLE);
            newViewList.add(unfinishedHeader);
        }
        if (!_unfinishedModelList.isEmpty()) {
            for (TaskModel unfinishedModel : unfinishedModels) {
                TasksAdapterViewType unfinishedItem = new TasksAdapterViewType(TasksAdapterViewType.TASK_ITEM_UNFINISHED);
                unfinishedItem.setTaskModel(unfinishedModel);
                newViewList.add(unfinishedItem);
            }
        }
        if (!finishedModels.isEmpty()) {
            TasksAdapterViewType finishedHeader = new TasksAdapterViewType(TasksAdapterViewType.TASK_HEADER_FINISHED);
            finishedHeader.setHeaderTitle(HeaderTitles.FINISHED_TITLE);
            newViewList.add(finishedHeader);

            for (TaskModel finishedModel : finishedModels) {
                TasksAdapterViewType finishedItem = new TasksAdapterViewType(TasksAdapterViewType.TASK_ITEM_FINISHED);
                finishedItem.setTaskModel(finishedModel);
                newViewList.add(finishedItem);
            }
        }
        if(!expiredModels.isEmpty()) {
            TasksAdapterViewType expiredHeader = new TasksAdapterViewType(TasksAdapterViewType.TASK_HEADER_EXPIRED);
            expiredHeader.setHeaderTitle(HeaderTitles.EXPIRED_TITLE);
            newViewList.add(expiredHeader);

            for (TaskModel expiredModel : expiredModels) {
                TasksAdapterViewType expiredItem = new TasksAdapterViewType(TasksAdapterViewType.TASK_ITEM_EXPIRED);
                expiredItem.setTaskModel(expiredModel);
                newViewList.add(expiredItem);
            }
        }

        //Dispatches an update animation when data is changed
        TasksDiffUtilCallback diffUtilCallback = new TasksDiffUtilCallback(this._viewList, newViewList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        this._viewList.clear();
        this._viewList.addAll(newViewList);
        diffResult.dispatchUpdatesTo(this);
    }

    public List<? extends TaskModel> getModels() {
        List<TaskModel> result = new ArrayList<>();
        result.addAll(this._unfinishedModelList);
        result.addAll(this._finishedModelList);
        result.addAll(this._expiredModelList);
        return result;
    }

    public List<TasksAdapterViewType> getViewTypeList() {
        return this._viewList;
    }

    public void setOnTaskItemClickListener (OnTaskItemClickListener onTaskItemClickListener) {
        this._onTaskItemClickListener = onTaskItemClickListener;
    }
    public void setOnDeleteAllItemClickListener(OnDeleteAllItemClickListener onDeleteAllItemClickListener) {
        this._onDeleteAllItemClickListener = onDeleteAllItemClickListener;
    }
    public void setOnTaskDoneClickListener(OnTaskDoneClickListener onTaskDoneClickListener) {
        this._onOnTaskDoneClickListener = onTaskDoneClickListener;
    }
}
