package com.gdiff.checkmate.presentation.fragments.main;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gdiff.checkmate.databinding.ItemGroupHeaderBinding;
import com.gdiff.checkmate.databinding.ItemTaskListBinding;
import com.gdiff.checkmate.domain.models.TaskModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//reference material
//https://www.digitalocean.com/community/tutorials/android-recyclerview-example
public class TasksAdapter extends RecyclerView.Adapter {
    private OnTaskItemClickListener _onTaskItemClickListener;
    private OnDeleteAllItemClickListener _onDeleteAllItemClickListener;
    private List<TasksAdapterViewType> _viewList;
    private List<? extends TaskModel> _unfinishedModelList;
    private List<? extends TaskModel> _finishedModelList;
    private List<? extends TaskModel> _expiredModelList;

    private final class HeaderTitles {
        private static final String UNFINISHED_TITLE = "";
        private static final String FINISHED_TITLE = "";
        private static final String EXPIRED_TITLE = "";
    }

    public TasksAdapter(OnTaskItemClickListener onTaskItemClickListener) {
        this._viewList = Collections.emptyList();
        this._unfinishedModelList = Collections.emptyList();
        this._finishedModelList = Collections.emptyList();
        this._expiredModelList = Collections.emptyList();
        this._onTaskItemClickListener = onTaskItemClickListener;
    }

    public interface OnTaskItemClickListener {
        void onTaskClick(TaskModel taskModel);
    }

    public interface OnDeleteAllItemClickListener{
        void onDeleteAllClick(List<?extends TaskModel> taskModels);
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

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemGroupHeaderBinding _groupHeaderBinding;
        private List<? extends TaskModel> _taskModels;

        public HeaderViewHolder(@NonNull View itemView, ItemGroupHeaderBinding groupHeaderBinding) {
            super(itemView);
            this._groupHeaderBinding = groupHeaderBinding;
        }

        public void bind(List<? extends TaskModel> taskModels, String headerTitle) {
            this._groupHeaderBinding.headerTitle.setText(headerTitle);
        }

        public void bind(List<? extends TaskModel> taskModels, String headerTitle, OnDeleteAllItemClickListener listener) {
            this._groupHeaderBinding.headerTitle.setText(headerTitle);
            this._groupHeaderBinding.buttonDeleteAll.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onDeleteAllClick(HeaderViewHolder.this._taskModels);
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

//    // Create new views (invoked by the layout manager)
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        // Create a new view, which defines the UI of the list item
//        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//        ItemTaskListBinding taskListItemBinding = ItemTaskListBinding.inflate(inflater, viewGroup, false);
//        return new ViewHolder(taskListItemBinding.getRoot(), taskListItemBinding);
//    }

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
        TasksAdapterViewType currentViewType = this._viewList.get(position);
        if (currentViewType != null ) {
            switch (currentViewType.getType()) {
                case TasksAdapterViewType.TASK_HEADER_UNFINISHED:
                    HeaderViewHolder unfinishedHeaderHolder = ((HeaderViewHolder) holder);
                    unfinishedHeaderHolder.getBinding().buttonDeleteAll.setVisibility(View.GONE);
                    unfinishedHeaderHolder.bind(this._unfinishedModelList, currentViewType.getHeaderTitle());
                    break;
                case TasksAdapterViewType.TASK_HEADER_FINISHED:
                    HeaderViewHolder finishedHeaderHolder = ((HeaderViewHolder) holder);
                    finishedHeaderHolder.getBinding().buttonDeleteAll.setVisibility(View.VISIBLE);
                    if (this._onDeleteAllItemClickListener == null) {
                        finishedHeaderHolder.bind(this._finishedModelList, currentViewType.getHeaderTitle());
                    }
                    else {
                        finishedHeaderHolder.bind(this._finishedModelList, currentViewType.getHeaderTitle(), this._onDeleteAllItemClickListener);
                    }
                    break;
                case TasksAdapterViewType.TASK_HEADER_EXPIRED:
                    HeaderViewHolder expiredHeaderHolder = ((HeaderViewHolder) holder);
                    expiredHeaderHolder.getBinding().buttonDeleteAll.setVisibility(View.VISIBLE);
                    if (this._onDeleteAllItemClickListener == null) {
                        expiredHeaderHolder.bind(this._expiredModelList, currentViewType.getHeaderTitle());
                    }
                    else {
                        expiredHeaderHolder.bind(this._expiredModelList, currentViewType.getHeaderTitle(), this._onDeleteAllItemClickListener);
                    }
                    break;
                case TasksAdapterViewType.TASK_ITEM_UNFINISHED:
                    ItemViewHolder unfinishedItemViewHolder = ((ItemViewHolder) holder);
                    unfinishedItemViewHolder.getBinding().fgCard.animate().translationX(0f).setDuration(300).start();
                    if (this._onTaskItemClickListener == null) {
                        unfinishedItemViewHolder.bind(this._viewList.get(position).getTaskModel(), TasksAdapterViewType.TASK_ITEM_UNFINISHED);
                    }
                    else {
                        unfinishedItemViewHolder.bind(this._viewList.get(position).getTaskModel(), this._onTaskItemClickListener, TasksAdapterViewType.TASK_ITEM_UNFINISHED);
                    }
                    break;
                case TasksAdapterViewType.TASK_ITEM_FINISHED:
                    ItemViewHolder finishedItemViewHolder = ((ItemViewHolder) holder);
                    finishedItemViewHolder.getBinding().fgCard.animate().translationX(0f).setDuration(300).start();
                    if (this._onTaskItemClickListener == null) {
                        finishedItemViewHolder.bind(this._viewList.get(position).getTaskModel(), TasksAdapterViewType.TASK_ITEM_FINISHED);
                    }
                    else {
                        finishedItemViewHolder.bind(this._viewList.get(position).getTaskModel(), this._onTaskItemClickListener, TasksAdapterViewType.TASK_ITEM_FINISHED);
                    }
                    break;
                case TasksAdapterViewType.TASK_ITEM_EXPIRED:
                    ItemViewHolder expiredItemViewHolder = ((ItemViewHolder) holder);
                    expiredItemViewHolder.getBinding().fgCard.animate().translationX(0f).setDuration(300).start();
                    if (this._onTaskItemClickListener == null) {
                        expiredItemViewHolder.bind(this._viewList.get(position).getTaskModel(), TasksAdapterViewType.TASK_ITEM_EXPIRED);
                    }
                    else {
                        expiredItemViewHolder.bind(this._viewList.get(position).getTaskModel(), this._onTaskItemClickListener, TasksAdapterViewType.TASK_ITEM_EXPIRED);
                    }
                    break;
                default:
                    break;
            }
        }
    }

//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
//
//        // Get element from your dataset at this position and replace the
//        // contents of the view with that element
//        viewHolder.getBinding().fgCard.animate().translationX(0f).setDuration(300).start();
//        if (_onTaskItemClickListener == null) {
//            viewHolder.bind(_modelList.get(position));
//        } else {
//            viewHolder.bind(_modelList.get(position), _onTaskItemClickListener);
//        }
//    }

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
        if (!unfinishedModels.isEmpty()||!finishedModels.isEmpty()||!expiredModels.isEmpty()) {
            TasksAdapterViewType unfinishedHeader = new TasksAdapterViewType(TasksAdapterViewType.TASK_HEADER_UNFINISHED);
            unfinishedHeader.setHeaderTitle(HeaderTitles.UNFINISHED_TITLE);
            this._viewList.add(unfinishedHeader);
        }
        if (!_unfinishedModelList.isEmpty()) {
            for (TaskModel unfinishedModel : unfinishedModels) {
                TasksAdapterViewType unfinishedItem = new TasksAdapterViewType(TasksAdapterViewType.TASK_ITEM_UNFINISHED);
                unfinishedItem.setTaskModel(unfinishedModel);
                this._viewList.add(unfinishedItem);
            }
        }
        if (!finishedModels.isEmpty()) {
            TasksAdapterViewType finishedHeader = new TasksAdapterViewType(TasksAdapterViewType.TASK_HEADER_FINISHED);
            finishedHeader.setHeaderTitle(HeaderTitles.FINISHED_TITLE);
            this._viewList.add(finishedHeader);

            for (TaskModel finishedModel : finishedModels) {
                TasksAdapterViewType finishedItem = new TasksAdapterViewType(TasksAdapterViewType.TASK_ITEM_FINISHED);
                finishedItem.setTaskModel(finishedModel);
                this._viewList.add(finishedItem);
            }
        }
        if(!expiredModels.isEmpty()) {
            TasksAdapterViewType expiredHeader = new TasksAdapterViewType(TasksAdapterViewType.TASK_HEADER_EXPIRED);
            expiredHeader.setHeaderTitle(HeaderTitles.EXPIRED_TITLE);
            this._viewList.add(expiredHeader);

            for (TaskModel expiredModel : expiredModels) {
                TasksAdapterViewType expiredItem = new TasksAdapterViewType(TasksAdapterViewType.TASK_ITEM_EXPIRED);
                expiredItem.setTaskModel(expiredModel);
                this._viewList.add(expiredItem);
            }
        }
    }

    public List<? extends TaskModel> getModels() {
        return Stream.of(this._unfinishedModelList, Stream.of(this._finishedModelList,this._expiredModelList)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
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
}
