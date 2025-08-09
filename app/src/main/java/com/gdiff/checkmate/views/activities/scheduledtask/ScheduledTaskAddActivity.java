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
import com.gdiff.checkmate.databinding.ActivityScheduledTaskAddBinding;
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

public class ScheduledTaskAddActivity extends BaseTaskActivity {
    private ActivityScheduledTaskAddBinding scheduledTaskAddBinding;
    private ScheduledTaskAddViewModel mViewModel;
    private boolean contentCheckPassed = true;
    private boolean dueDateCheckPassed = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        scheduledTaskAddBinding = ActivityScheduledTaskAddBinding.inflate(getLayoutInflater());
        setContentView(scheduledTaskAddBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        mViewModel = new ViewModelProvider(this).get(ScheduledTaskAddViewModel.class);
        setSupportActionBar(scheduledTaskAddBinding.todoTaskToolbar);

        mViewModel.getSelectedDate().observe(
                this,
                new Observer<Date>() {
                    @Override
                    public void onChanged(Date date) {
                        scheduledTaskAddBinding.dueDate.setText(
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

        scheduledTaskAddBinding.dueDate.setOnClickListener(
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

        //ui input checks
        scheduledTaskAddBinding.saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contentCheckPassed = isTextFieldEmpty(
                            scheduledTaskAddBinding.taskContent,
                            scheduledTaskAddBinding.taskContentInputLayout,
                            "Content cannot be empty",
                            R.drawable.ic_warning
                        );

                        dueDateCheckPassed = isTextFieldEmpty(
                                scheduledTaskAddBinding.dueDate,
                                scheduledTaskAddBinding.dueDateInputLayout,
                                "Please select date",
                                R.drawable.ic_warning
                        );
                        if (mViewModel.getSelectedDate().getValue()!= null) {
                            scheduledTaskAddBinding.saveButton.setEnabled(false);
                            ScheduledTask scheduledTask = new ScheduledTask();
                            scheduledTask.setContent((scheduledTaskAddBinding.taskContent.getText()!= null)?scheduledTaskAddBinding.taskContent.getText().toString():"");
                            scheduledTask.setDueDate(mViewModel.getSelectedDate().getValue());




                            if (contentCheckPassed&&dueDateCheckPassed) {
                                mViewModel.addTask(
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
                                                scheduledTaskAddBinding.saveButton.setEnabled(true);
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
        scheduledTaskAddBinding.taskContent
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
                                        scheduledTaskAddBinding.taskContent,
                                        scheduledTaskAddBinding.taskContentInputLayout,
                                        "Content cannot be empty",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );

        scheduledTaskAddBinding.dueDate
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
                                        scheduledTaskAddBinding.dueDate,
                                        scheduledTaskAddBinding.dueDateInputLayout,
                                        "Please select date",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );
    }
}