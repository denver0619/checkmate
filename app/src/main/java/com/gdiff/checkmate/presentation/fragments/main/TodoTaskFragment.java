package com.gdiff.checkmate.presentation.fragments.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

import com.gdiff.checkmate.MainApplication;
import com.gdiff.checkmate.R;
import com.gdiff.checkmate.databinding.FragmentTodoTaskBinding;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.presentation.activities.todotask.TodoTaskAddActivity;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        tasksAdapter = new TasksAdapter(Collections.emptyList(), new TasksAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskClick(TaskModel taskModel) {
                TodoTask todoTask = (TodoTask) taskModel ;
                // launch edit intent
            }
        });
        todoTaskBinding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        todoTaskBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        todoTaskBinding.recyclerView.addItemDecoration(new MaterialDividerItemDecoration(
                requireContext(), LinearLayoutManager.VERTICAL
        ));
        todoTaskBinding.recyclerView.setAdapter(tasksAdapter);
        mViewModel.getModelList()
                .observe(
                        getViewLifecycleOwner(),
                        new Observer<List<? extends TaskModel>>() {
                            @Override
                            public void onChanged(List<? extends TaskModel> taskModels) {
                                TodoTaskFragment.this.tasksAdapter.updateModels(taskModels);
                                todoTaskBinding.recyclerView.setVisibility(taskModels.isEmpty()?View.GONE:View.VISIBLE);
                                todoTaskBinding.textWhenEmpty.setVisibility(!taskModels.isEmpty()?View.GONE:View.VISIBLE);
                            }
                        }
                );

        todoTaskBinding.fabNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch add intent
                Intent intent = new Intent(requireActivity(), TodoTaskAddActivity.class);
                requireActivity().startActivity(intent);
            }
        });

        getTouchHelper(todoTaskBinding.recyclerView);
        mViewModel.loadData();
    }

    private void getTouchHelper(RecyclerView recyclerView) {
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        //TODO: handle delete
                    }
                }
        ).attachToRecyclerView(recyclerView);
    }

}