package com.gdiff.checkmate.presentation.activities.todotask;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.gdiff.checkmate.R;
import com.gdiff.checkmate.application.callbacks.GeneralCallback;
import com.gdiff.checkmate.databinding.ActivityTodoTaskAddBinding;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.presentation.activities.BaseActivity;
import com.gdiff.checkmate.presentation.activities.BaseTaskActivity;

import java.util.Objects;

public class TodoTaskAddActivity extends BaseTaskActivity {
    ActivityTodoTaskAddBinding activityTodoTaskAddBinding;
    TodoTaskAddViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityTodoTaskAddBinding = ActivityTodoTaskAddBinding.inflate(getLayoutInflater());
        setContentView(activityTodoTaskAddBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel = new ViewModelProvider(this).get(TodoTaskAddViewModel.class);
        setSupportActionBar(activityTodoTaskAddBinding.todoTaskToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        activityTodoTaskAddBinding.saveButton
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                activityTodoTaskAddBinding.saveButton.setEnabled(false);
                                TodoTask todoTask = new TodoTask();
                                boolean checksPassed = true;
                                // TODO: Data Checks
                                todoTask.setContent(activityTodoTaskAddBinding.todoTaskContent.getText().toString());

                                if (checksPassed) {
                                    viewModel.addTodoTask(todoTask, new RepositoryOnDataChangedCallback() {
                                        @Override
                                        public void onDataChanged() {
                                            finish();
                                        }
                                    });
                                } else {
                                    new Handler().postDelayed(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    activityTodoTaskAddBinding.saveButton.setEnabled(true);
                                                }
                                            },
                                            300
                                    );
                                }
                            }
                        }
                );
    }
}