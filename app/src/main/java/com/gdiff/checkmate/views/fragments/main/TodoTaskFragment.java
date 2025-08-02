package com.gdiff.checkmate.views.fragments.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gdiff.checkmate.application.constants.IntentExtraConstantNames;
import com.gdiff.checkmate.databinding.FragmentTodoTaskBinding;
import com.gdiff.checkmate.domain.datatransferobjects.TaskGroupListsDTO;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.views.activities.todotask.TodoTaskAddActivity;
import com.gdiff.checkmate.views.activities.todotask.TodoTaskEditActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class TodoTaskFragment extends Fragment {

    private TodoTaskViewModel mViewModel;
    private FragmentTodoTaskBinding todoTaskBinding;
    private TasksAdapter tasksAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.todoTaskBinding = FragmentTodoTaskBinding.inflate(inflater, container, false);
        return todoTaskBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(TodoTaskViewModel.class);
        tasksAdapter = new TasksAdapter();
        tasksAdapter.setOnTaskItemClickListener(
                new TasksAdapter.OnTaskItemClickListener() {
                    @Override
                    public void onTaskClick(TaskModel<?> taskModel) {
                        Intent intent = new Intent(requireActivity(), TodoTaskEditActivity.class);
                        intent.putExtra(IntentExtraConstantNames.keyTaskModel, taskModel);
                        requireActivity().startActivity(intent);
                    }
                }
        );

        tasksAdapter.setOnDeleteAllItemClickListener(
                new TasksAdapter.OnDeleteAllItemClickListener() {
                    @Override
                    public void onDeleteAllClick(List<? extends TaskModel<?>> taskModels, String taskGroupTitle) {

                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Delete all " + taskGroupTitle + " ?")
                                .setMessage("This action cannot be undone.")
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mViewModel.deleteAllTasks(taskModels);
                                    }
                                })
                                .show();

                    }
                }
        );

        tasksAdapter.setOnTaskDoneClickListener(
                new TasksAdapter.OnTaskDoneClickListener() {
                    @Override
                    public void onTaskDone(TaskModel<?> taskModel, boolean isDone) {
                        TodoTask todoTask = (TodoTask) taskModel;
                        ((TodoTask) taskModel).setStatus(isDone);
                        mViewModel.updateTask(todoTask);
                    }
                }
        );
        todoTaskBinding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        todoTaskBinding.recyclerViewTasks.setItemAnimator(new DefaultItemAnimator());
        todoTaskBinding.recyclerViewTasks.setAdapter(tasksAdapter);

        mViewModel.getTaskGroupLists()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<TaskGroupListsDTO>() {
                            @Override
                            public void onChanged(TaskGroupListsDTO taskGroupListsDTO) {
                                TodoTaskFragment.this.tasksAdapter.updateModels(
                                        taskGroupListsDTO.getUnfinishedTasks(),
                                        taskGroupListsDTO.getFinishedTasks(),
                                        taskGroupListsDTO.getExpiredTasks()
                                );
                                todoTaskBinding.recyclerViewTasks
                                        .setVisibility(
                                                (taskGroupListsDTO.getUnfinishedTasks().isEmpty()
                                                && taskGroupListsDTO.getFinishedTasks().isEmpty()
                                                && taskGroupListsDTO.getExpiredTasks().isEmpty())
                                                ? View.GONE : View.VISIBLE
                                        );

                                todoTaskBinding.textWhenEmpty
                                        .setVisibility(
                                                (!taskGroupListsDTO.getUnfinishedTasks().isEmpty()
                                                || !taskGroupListsDTO.getFinishedTasks().isEmpty()
                                                || !taskGroupListsDTO.getExpiredTasks().isEmpty())
                                                ? View.GONE : View.VISIBLE);
                            }
                        }
                );


        // Create and Register Callback to repository to notify adapter and recycler view
        RepositoryOnDataChangedCallback onDataChangedCallback =
                new RepositoryOnDataChangedCallback() {
                    @Override
                    public void onDataChanged() {
                        mViewModel.reloadData();
                    }
                };

        todoTaskBinding.fabNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch add intent
                Intent intent = new Intent(requireActivity(), TodoTaskAddActivity.class);
                requireActivity().startActivity(intent);
            }
        });

        getTouchHelper(todoTaskBinding.recyclerViewTasks);
        mViewModel.loadData(onDataChangedCallback);
    }

    private void getTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = null;
        ItemTouchHelper finalItemTouchHelper = itemTouchHelper;
        itemTouchHelper = new ItemTouchHelper(new TasksSwipeCallback(requireActivity(), new TasksSwipeCallback.TasksSwipeActionCallback() {
            @Override
            public void actionCallback(TaskModel<?> taskModel, RecyclerView.ViewHolder viewHolder) {
                //TODO: delete dialog then delete data
                TodoTask todoTask = (TodoTask) taskModel;

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Todo Task?")
                        .setMessage("This action cannot be undone.")
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                        .setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (recyclerView.getAdapter() != null) {
                                            mViewModel.deleteTask(
                                                    todoTask
                                            );
                                        }
                                    }
                                }).show().setOnDismissListener(
                                new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        if (recyclerView.getAdapter() != null){
                                            recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                                        }
                                    }
                                }
                        );
            }})

        );
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}