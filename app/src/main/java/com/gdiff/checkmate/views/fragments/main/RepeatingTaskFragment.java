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

import com.gdiff.checkmate.R;
import com.gdiff.checkmate.application.constants.IntentExtraConstantNames;
import com.gdiff.checkmate.databinding.FragmentRepeatingTaskBinding;
import com.gdiff.checkmate.domain.datatransferobjects.TaskGroupListsDTO;
import com.gdiff.checkmate.domain.models.RepeatingTask;
import com.gdiff.checkmate.domain.models.ScheduledTask;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.views.activities.repeatingtask.RepeatingTaskAddActivity;
import com.gdiff.checkmate.views.activities.repeatingtask.RepeatingTaskEditActivity;
import com.gdiff.checkmate.views.activities.scheduledtask.ScheduledTaskAddActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class RepeatingTaskFragment extends Fragment {

    private RepeatingTaskViewModel mViewModel;
    private FragmentRepeatingTaskBinding repeatingTaskBinding;
    private TasksAdapter tasksAdapter;

    public static RepeatingTaskFragment newInstance() {
        return new RepeatingTaskFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repeatingTaskBinding = FragmentRepeatingTaskBinding.inflate(inflater, container, false);
        return repeatingTaskBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RepeatingTaskViewModel.class);
        tasksAdapter = new TasksAdapter();
        tasksAdapter.setOnTaskItemClickListener(
                new TasksAdapter.OnTaskItemClickListener() {
                    @Override
                    public void onTaskClick(TaskModel<?> taskModel) {
//                        TODO: edit intent
                        Intent intent = new Intent(requireActivity(), RepeatingTaskEditActivity.class);
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
                        RepeatingTask repeatingTask = (RepeatingTask) taskModel;
                        repeatingTask.setStatus(isDone);
                        mViewModel.updateTask(repeatingTask);
                    }
                }
        );

        repeatingTaskBinding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        repeatingTaskBinding.recyclerViewTasks.setItemAnimator(new DefaultItemAnimator());
        repeatingTaskBinding.recyclerViewTasks.setAdapter(tasksAdapter);

        mViewModel.getTaskGroupList().observe(
                getViewLifecycleOwner(),
                new Observer<TaskGroupListsDTO>() {
                    @Override
                    public void onChanged(TaskGroupListsDTO taskGroupListsDTO) {

                        RepeatingTaskFragment.this.tasksAdapter.updateModels(
                                taskGroupListsDTO.getUnfinishedTasks(),
                                taskGroupListsDTO.getFinishedTasks(),
                                taskGroupListsDTO.getExpiredTasks()
                        );
                        repeatingTaskBinding.recyclerViewTasks
                                .setVisibility(
                                        (taskGroupListsDTO.getUnfinishedTasks().isEmpty()
                                                && taskGroupListsDTO.getFinishedTasks().isEmpty()
                                                && taskGroupListsDTO.getExpiredTasks().isEmpty())
                                                ? View.GONE : View.VISIBLE
                                );
                        repeatingTaskBinding.textWhenEmpty
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

        repeatingTaskBinding.fabNewTask.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo: add task intent
                        Intent intent = new Intent(requireActivity(), RepeatingTaskAddActivity.class);
                        requireActivity().startActivity(intent);
                    }
                }
        );

        getTouchHelper(repeatingTaskBinding.recyclerViewTasks);
        mViewModel.loadData(onDataChangedCallback);
    }

    private void getTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = null;
        ItemTouchHelper finalItemTouchHelper = itemTouchHelper;
        itemTouchHelper = new ItemTouchHelper(new TasksSwipeCallback(requireActivity(), new TasksSwipeCallback.TasksSwipeActionCallback() {
            @Override
            public void actionCallback(TaskModel<?> taskModel, RecyclerView.ViewHolder viewHolder) {
                RepeatingTask repeatingTask = (RepeatingTask) taskModel;

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
                                                    repeatingTask
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