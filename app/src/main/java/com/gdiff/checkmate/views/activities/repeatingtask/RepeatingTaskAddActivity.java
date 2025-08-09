package com.gdiff.checkmate.views.activities.repeatingtask;

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
import com.gdiff.checkmate.databinding.ActivityRepeatingTaskAddBinding;
import com.gdiff.checkmate.domain.models.RepeatingTask;
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

public class RepeatingTaskAddActivity extends BaseTaskActivity {
    private ActivityRepeatingTaskAddBinding repeatingTaskAddBinding;
    private RepeatingTaskAddViewModel mViewModel;
    private boolean contentCheckPassed = true;
    private boolean startDateCheckPassed = true;
    private boolean intervalCheckPassed = true;
    private boolean intervalValidityCheckPassed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        repeatingTaskAddBinding = ActivityRepeatingTaskAddBinding.inflate(getLayoutInflater());
        setContentView(repeatingTaskAddBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel = new ViewModelProvider(this).get(RepeatingTaskAddViewModel.class);
        setSupportActionBar(repeatingTaskAddBinding.todoTaskToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mViewModel.getSelectedDate().observe(
                this,
                new Observer<Date>() {
                    @Override
                    public void onChanged(Date date) {
                        repeatingTaskAddBinding.startDate.setText(
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

        repeatingTaskAddBinding.startDate.setOnClickListener(
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
                                .setTitleText("Start Date")
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

        repeatingTaskAddBinding.saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contentCheckPassed = isTextFieldEmpty(
                                repeatingTaskAddBinding.taskContent,
                                repeatingTaskAddBinding.taskContentInputLayout,
                                "Content cannot be empty",
                                R.drawable.ic_warning
                        );
                        startDateCheckPassed = isTextFieldEmpty(
                                repeatingTaskAddBinding.startDate,
                                repeatingTaskAddBinding.startDateInputLayout,
                                "Please select date",
                                R.drawable.ic_warning
                        );
                        intervalCheckPassed = isTextFieldEmpty(
                                repeatingTaskAddBinding.interval,
                                repeatingTaskAddBinding.intervalInputLayout,
                                "Interval cannot be empty",
                                R.drawable.ic_warning
                        );
                        intervalValidityCheckPassed = isIntervalValid(
                                repeatingTaskAddBinding.interval,
                                repeatingTaskAddBinding.intervalInputLayout,
                                "Interval cannot be less than 1",
                                R.drawable.ic_warning,
                                CustomComparator.LESS_THAN,
                                1
                        );
                        if (mViewModel.getSelectedDate().getValue() != null) {
                            RepeatingTask repeatingTask = new RepeatingTask();
                            repeatingTask.setContent((repeatingTaskAddBinding.taskContent.getText()!=null)?repeatingTaskAddBinding.taskContent.getText().toString():"");
                            repeatingTask.setInterval((repeatingTaskAddBinding.interval.getText()!=null)?Integer.parseInt(repeatingTaskAddBinding.interval.getText().toString()):1);
                            repeatingTask.setStartDate(mViewModel.getSelectedDate().getValue());
                            repeatingTask.setLastCompleted(mViewModel.getSelectedDate().getValue());
                            repeatingTask.setCurrentCompleted(mViewModel.getSelectedDate().getValue());


                            if (contentCheckPassed&&startDateCheckPassed&&intervalCheckPassed&&intervalValidityCheckPassed) {
                                mViewModel.addTask(repeatingTask,
                                        new RepositoryOnDataChangedCallback() {
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
                                                repeatingTaskAddBinding.saveButton.setEnabled(true);
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
        repeatingTaskAddBinding.taskContent
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
                                        repeatingTaskAddBinding.taskContent,
                                        repeatingTaskAddBinding.taskContentInputLayout,
                                        "Content cannot be empty",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );

        repeatingTaskAddBinding.startDate
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
                                startDateCheckPassed = isTextFieldEmpty(
                                        repeatingTaskAddBinding.startDate,
                                        repeatingTaskAddBinding.startDateInputLayout,
                                        "Please select date",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );

        repeatingTaskAddBinding.interval
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
                                intervalCheckPassed = isTextFieldEmpty(
                                        repeatingTaskAddBinding.interval,
                                        repeatingTaskAddBinding.intervalInputLayout,
                                        "Interval cannot be empty",
                                        R.drawable.ic_warning
                                );
                                intervalValidityCheckPassed = isIntervalValid(
                                        repeatingTaskAddBinding.interval,
                                        repeatingTaskAddBinding.intervalInputLayout,
                                        "Interval cannot less than 1",
                                        R.drawable.ic_warning,
                                        CustomComparator.LESS_THAN,
                                        1
                                );
                            }
                        }
                );
    }
}