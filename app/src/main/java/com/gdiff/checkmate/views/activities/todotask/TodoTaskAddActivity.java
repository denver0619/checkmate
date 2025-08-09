package com.gdiff.checkmate.views.activities.todotask;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.gdiff.checkmate.R;
import com.gdiff.checkmate.databinding.ActivityTodoTaskAddBinding;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.views.activities.BaseTaskActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;

public class TodoTaskAddActivity extends BaseTaskActivity {
    private ActivityTodoTaskAddBinding activityTodoTaskAddBinding;
    private TodoTaskAddViewModel viewModel;
    private boolean contentCheckPassed = true;
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
                                contentCheckPassed = isTextFieldEmpty(
                                        activityTodoTaskAddBinding.taskContent,
                                        activityTodoTaskAddBinding.taskContentInputLayout,
                                        "Content cannot be empty",
                                        R.drawable.ic_warning
                                );

                                activityTodoTaskAddBinding.saveButton.setEnabled(false);
                                TodoTask todoTask = new TodoTask();
                                todoTask.setContent((activityTodoTaskAddBinding
                                        .taskContent
                                        .getText()!=null)
                                        ?activityTodoTaskAddBinding
                                        .taskContent
                                        .getText()
                                        .toString()
                                        :"");



                                // TODO: Data Checks
                                if (contentCheckPassed) {
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


        //ui input checks
        activityTodoTaskAddBinding.taskContent
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
                                        activityTodoTaskAddBinding.taskContent,
                                        activityTodoTaskAddBinding.taskContentInputLayout,
                                        "Content cannot be empty",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );

    }
}