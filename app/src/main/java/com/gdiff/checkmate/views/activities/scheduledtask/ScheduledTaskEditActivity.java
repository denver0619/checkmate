package com.gdiff.checkmate.views.activities.scheduledtask;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gdiff.checkmate.R;
import com.gdiff.checkmate.application.constants.IntentExtraConstantNames;
import com.gdiff.checkmate.databinding.ActivityScheduledTaskEditBinding;
import com.gdiff.checkmate.domain.models.ScheduledTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.views.activities.BaseTaskActivity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ScheduledTaskEditActivity extends BaseTaskActivity {
    ActivityScheduledTaskEditBinding scheduledTaskEditBinding;
    ScheduledTaskEditViewModel mViewModel;
    private boolean contentCheckPassed = true;
    private boolean dueDateCheckPassed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        scheduledTaskEditBinding = ActivityScheduledTaskEditBinding.inflate(getLayoutInflater());
        setContentView(scheduledTaskEditBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel = new ViewModelProvider(this).get(ScheduledTaskEditViewModel.class);
        setSupportActionBar(scheduledTaskEditBinding.todoTaskToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ScheduledTask scheduledTask = (ScheduledTask) getIntent().getSerializableExtra(IntentExtraConstantNames.keyTaskModel);

        if (scheduledTask != null) {
            this.scheduledTaskEditBinding.taskContent.setText(scheduledTask.getContent());
            this.scheduledTaskEditBinding.dueDate.setText(
                    new SimpleDateFormat(
                            "MM-dd-yyyy",
                            Locale.getDefault()
                    ).format(
                            scheduledTask.getDueDate()
                    )
            );
            mViewModel.setSelectedDate(scheduledTask.getDueDate());
        }

        mViewModel.getSelectedDate().observe(
                this,
                new Observer<Date>() {
                    @Override
                    public void onChanged(Date date) {
                        scheduledTaskEditBinding.dueDate.setText(
                                new SimpleDateFormat(
                                        "MM-dd-yyyy",
                                        Locale.getDefault()
                                ).format(
                                        date
                                )
                        );
                    }
                }
        );

        scheduledTaskEditBinding.dueDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                                .setValidator(DateValidatorPointForward.now())
                                .build();

                        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                                .setCalendarConstraints(
                                        calendarConstraints
                                )
                                .setTitleText("Due Date")
                                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                                .build();
                        materialDatePicker.addOnPositiveButtonClickListener(
                                new MaterialPickerOnPositiveButtonClickListener<Long>() {
                                    @Override
                                    public void onPositiveButtonClick(Long aLong) {
                                        mViewModel.setSelectedDate(new Date(aLong));
                                    }
                                }
                        );
                        materialDatePicker.show(getSupportFragmentManager(), "Date Picker");
                    }
                }
        );

        scheduledTaskEditBinding.saveButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentCheckPassed = isTextFieldEmpty(
                            scheduledTaskEditBinding.taskContent,
                            scheduledTaskEditBinding.taskContentInputLayout,
                            "Content cannot be empty",
                            R.drawable.ic_warning
                    );

                    dueDateCheckPassed = isTextFieldEmpty(
                            scheduledTaskEditBinding.dueDate,
                            scheduledTaskEditBinding.dueDateInputLayout,
                            "Please select date",
                            R.drawable.ic_warning
                    );

                    if (mViewModel.getSelectedDate().getValue()!= null) {
                        scheduledTaskEditBinding.saveButton.setEnabled(false);
                        if (scheduledTask != null) {
                            scheduledTask.setContent((scheduledTaskEditBinding.taskContent.getText()!= null)?scheduledTaskEditBinding.taskContent.getText().toString():"");
                            scheduledTask.setDueDate(mViewModel.getSelectedDate().getValue());

                            if (contentCheckPassed&&dueDateCheckPassed) {
                                mViewModel.updateTask(
                                        scheduledTask,
                                        new RepositoryOnDataChangedCallback() {
                                            @Override
                                            public void onDataChanged() {
                                                finish();
                                            }
                                        }
                                );
                            } else {
                                new Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                scheduledTaskEditBinding.saveButton.setEnabled(true);
                                            }
                                        },
                                        300
                                );
                            }
                        }
                    }
                }
            }
        );

        //ui input checks
        scheduledTaskEditBinding.taskContent
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
                                        scheduledTaskEditBinding.taskContent,
                                        scheduledTaskEditBinding.taskContentInputLayout,
                                        "Content cannot be empty",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );

        scheduledTaskEditBinding.dueDate
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
                                dueDateCheckPassed = isTextFieldEmpty(
                                        scheduledTaskEditBinding.dueDate,
                                        scheduledTaskEditBinding.dueDateInputLayout,
                                        "Please select date",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );
    }
}