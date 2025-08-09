package com.gdiff.checkmate.views.activities.todotask;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class TodoTaskEditActivity extends BaseTaskActivity {
    private TodoTaskEditViewModel viewModel;
    private ActivityTodoTaskEditBinding todoTaskEditBinding;
    private TaskModel taskModel;
    private boolean contentCheckPassed = true;

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
            this.todoTaskEditBinding.taskContent.setText(todoTask.getContent());
        }

        this.todoTaskEditBinding
                .saveButton
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                contentCheckPassed = isTextFieldEmpty(
                                        todoTaskEditBinding.taskContent,
                                        todoTaskEditBinding.taskContentInputLayout,
                                        "Content cannot be empty",
                                        R.drawable.ic_warning
                                );
                                todoTaskEditBinding.saveButton.setEnabled(false);
                                if (todoTask != null) {
                                    todoTask.setContent(
                                            (TodoTaskEditActivity.this
                                                    .todoTaskEditBinding
                                                    .taskContent
                                                    .getText() != null)
                                                    ?TodoTaskEditActivity.this
                                                    .todoTaskEditBinding
                                                    .taskContent
                                                    .getText()
                                                    .toString()
                                                    :""
                                    );

                                    // TODO: Data Checks
                                    if (contentCheckPassed) {
                                        viewModel.editTask(todoTask, new RepositoryOnDataChangedCallback() {
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
                                                        todoTaskEditBinding.saveButton.setEnabled(true);
                                                    }
                                                },
                                                300
                                        );
                                    }
                                }
                            }
                        }
                );

        //ui input checks
        todoTaskEditBinding.taskContent
                .addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable editable) {

                            }

                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                contentCheckPassed = isTextFieldEmpty(
                                        todoTaskEditBinding.taskContent,
                                        todoTaskEditBinding.taskContentInputLayout,
                                        "Content cannot be empty",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );
    }
}