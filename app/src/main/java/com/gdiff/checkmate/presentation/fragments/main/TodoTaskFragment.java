package com.gdiff.checkmate.presentation.fragments.main;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.gdiff.checkmate.MainApplication;
import com.gdiff.checkmate.R;
import com.gdiff.checkmate.application.constants.IntentExtraConstantNames;
import com.gdiff.checkmate.databinding.FragmentTodoTaskBinding;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.GeneralRepositoryCallback;
import com.gdiff.checkmate.presentation.activities.todotask.TodoTaskAddActivity;
import com.gdiff.checkmate.presentation.activities.todotask.TodoTaskEditActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TodoTaskFragment extends Fragment {

    private TodoTaskViewModel mViewModel;
    private FragmentTodoTaskBinding todoTaskBinding;
    private TasksAdapter unfinishedTasksAdapter;
    private TasksAdapter finishedTaskAdapter;

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
        unfinishedTasksAdapter = new TasksAdapter(Collections.emptyList(), new TasksAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskClick(TaskModel taskModel) {
                TodoTask todoTask = (TodoTask) taskModel ;
                Intent intent = new Intent(requireActivity(), TodoTaskEditActivity.class);
                intent.putExtra(IntentExtraConstantNames.keyTaskModel, taskModel);
                requireActivity().startActivity(intent);
            }
        });
        finishedTaskAdapter = new TasksAdapter(Collections.emptyList(), new TasksAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskClick(TaskModel taskModel) {

            }
        });
        todoTaskBinding.unfinishedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        todoTaskBinding.unfinishedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        todoTaskBinding.unfinishedRecyclerView.setAdapter(unfinishedTasksAdapter);

        todoTaskBinding.finishedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        todoTaskBinding.finishedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        todoTaskBinding.finishedRecyclerView.setAdapter(unfinishedTasksAdapter);

        //disable scroll for both recyclerview
        todoTaskBinding.unfinishedRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return motionEvent.getAction() == MotionEvent.ACTION_MOVE;
                    }
                }
        );

        //TODO:ADD TWO RECYCLERVIEW FOR TWO MODELLIST
        mViewModel.getModelList()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<List<? extends TaskModel>>() {
                            @Override
                            public void onChanged(List<? extends TaskModel> taskModels) {
                                TodoTaskFragment.this.tasksAdapter.updateModels(taskModels);
                                TodoTaskFragment.this.tasksAdapter.notifyItemInserted(taskModels.size());
                                todoTaskBinding.recyclerView.setVisibility(taskModels.isEmpty()?View.GONE:View.VISIBLE);
                                todoTaskBinding.textWhenEmpty.setVisibility(!taskModels.isEmpty()?View.GONE:View.VISIBLE);
                            }
                        }
                );


        // Create and Register Callback to repository to notify adapter and recycler view
        // TODO: LOGIC WHEN SWITCHING STATUS OF TASK
        // TODO: XML SCROLLVIEW -> LINEAR -> RECYCLERVIEW, LINEAR(TOGGLE VISIBILITY WHEN THERES NO FINISHED TASK) -> TEXTVIEW (TITLE: FINISHED TASKS), RECYCLERVIEW (BOTH RECYCLERVIEW STOP SCROLL)
        GeneralRepositoryCallback repositoryCallback = new GeneralRepositoryCallback() {
            @Override
            public void onAdd() {
                mViewModel.reloadData();
                if (todoTaskBinding.recyclerView.getAdapter()!= null) {
                    todoTaskBinding.recyclerView.getAdapter().notifyItemInserted(todoTaskBinding.recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onUpdate(int id) {
                mViewModel.reloadData();
                if (todoTaskBinding.recyclerView.getAdapter()!= null) {
                    List<? extends TaskModel> taskModels = ((TasksAdapter)todoTaskBinding.recyclerView.getAdapter()).getModels();
                    for (int i = 0; i < taskModels.size(); i++) {
                        if (taskModels.get(i).getId() == id) {
                            todoTaskBinding.recyclerView.getAdapter().notifyItemChanged(i);
                        }
                    }
                }
            }

            @Override
            public void onDelete(int id) {
                mViewModel.reloadData();
                if (todoTaskBinding.recyclerView.getAdapter()!= null) {
                    List<? extends TaskModel> taskModels = ((TasksAdapter)todoTaskBinding.recyclerView.getAdapter()).getModels();
                    for (int i = 0; i < taskModels.size(); i++) {
                        if (taskModels.get(i).getId() == id) {
                            todoTaskBinding.recyclerView.getAdapter().notifyItemRemoved(i);
                        }
                    }
                }
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

        getTouchHelper(todoTaskBinding.recyclerView);
        mViewModel.loadData(repositoryCallback);
    }

    private void getTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = null;
        ItemTouchHelper finalItemTouchHelper = itemTouchHelper;
        itemTouchHelper = new ItemTouchHelper(new TasksSwipeCallback(requireActivity(), new TasksSwipeCallback.TasksSwipeActionCallback() {
            @Override
            public void actionCallback(TaskModel taskModel, RecyclerView.ViewHolder viewHolder) {
                //TODO: delete dialog then delete data
                TodoTask todoTask = (TodoTask) taskModel;

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Todo Task?")
                        .setMessage("This action cannot be undone.")
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                                    }
                                })
                        .setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        int position = viewHolder.getAdapterPosition();
                                        if (recyclerView.getAdapter() != null) {
                                            ((TasksAdapter) recyclerView.getAdapter()).getModels().remove(position);
                                            recyclerView.getAdapter().notifyItemRemoved(position);
                                        }
                                    }
                                }).show();
            }})

        );
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}