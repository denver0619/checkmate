package com.gdiff.checkmate.views.activities.todotask;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.gdiff.checkmate.R;
import com.gdiff.checkmate.application.constants.IntentExtraConstantNames;
import com.gdiff.checkmate.databinding.ActivityTodoTaskEditBinding;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.views.activities.BaseTaskActivity;

public class TodoTaskEditActivity extends BaseTaskActivity {
    private TodoTaskEditViewModel viewModel;
    private ActivityTodoTaskEditBinding todoTaskEditBinding;
    private TaskModel taskModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        todoTaskEditBinding = ActivityTodoTaskEditBinding.inflate(getLayoutInflater());
        setContentView(todoTaskEditBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel = new ViewModelProvider(this).get(TodoTaskEditViewModel.class);
        setSupportActionBar(todoTaskEditBinding.todoTaskToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TodoTask todoTask = (TodoTask) getIntent().getSerializableExtra(IntentExtraConstantNames.keyTaskModel);
        if (todoTask != null) {
            this.todoTaskEditBinding.todoTaskContent.setText(todoTask.getContent());
        }

        this.todoTaskEditBinding
                .saveButton
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (todoTask != null) {
                                    todoTask.setContent(
                                            (TodoTaskEditActivity.this
                                                    .todoTaskEditBinding
                                                    .todoTaskContent
                                                    .getText() != null)
                                                    ?TodoTaskEditActivity.this
                                                    .todoTaskEditBinding
                                                    .todoTaskContent
                                                    .getText()
                                                    .toString()
                                                    :""
                                    );
                                    viewModel.editTask(todoTask,
                                            new RepositoryOnDataChangedCallback() {
                                                @Override
                                                public void onDataChanged() {
                                                    finish();
                                                }
                                            });
                                }
                            }
                        }
                );

    }
}