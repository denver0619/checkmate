package com.gdiff.checkmate.views.activities.repeatingtask;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.gdiff.checkmate.databinding.ActivityRepeatingTaskEditBinding;
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

public class RepeatingTaskEditActivity extends BaseTaskActivity {
    private ActivityRepeatingTaskEditBinding repeatingTaskEditBinding;
    private RepeatingTaskEditViewModel mViewModel;
    private boolean contentCheckPassed = true;
    private boolean startDateCheckPassed = true;
    private boolean intervalCheckPassed = true;
    private boolean intervalValidityCheckPassed = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        repeatingTaskEditBinding = ActivityRepeatingTaskEditBinding.inflate(getLayoutInflater());
        setContentView(repeatingTaskEditBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel = new ViewModelProvider(this).get(RepeatingTaskEditViewModel.class);
        setSupportActionBar(repeatingTaskEditBinding.todoTaskToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RepeatingTask repeatingTask = (RepeatingTask) getIntent().getSerializableExtra(IntentExtraConstantNames.keyTaskModel);


        if (repeatingTask != null) {
            this.repeatingTaskEditBinding.taskContent.setText(repeatingTask.getContent());
            this.repeatingTaskEditBinding.startDate.setText(
                    new SimpleDateFormat(
                            "MM-dd-yyyy",
                            Locale.getDefault()
                    ).format(
                            repeatingTask.getStartDate()
                    )
            );
            this.repeatingTaskEditBinding.interval.setText(
                    String.valueOf(
                            repeatingTask.getInterval())
            );
            mViewModel.setSelectedDate(repeatingTask.getStartDate());
        }

        mViewModel.getSelectedDate().observe(
                this,
                new Observer<Date>() {
                    @Override
                    public void onChanged(Date date) {
                        repeatingTaskEditBinding.startDate.setText(
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


        repeatingTaskEditBinding.startDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
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

        repeatingTaskEditBinding.saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contentCheckPassed = isTextFieldEmpty(
                                repeatingTaskEditBinding.taskContent,
                                repeatingTaskEditBinding.taskContentInputLayout,
                                "Content cannot be empty",
                                R.drawable.ic_warning
                        );
                        startDateCheckPassed = isTextFieldEmpty(
                                repeatingTaskEditBinding.startDate,
                                repeatingTaskEditBinding.startDateInputLayout,
                                "Please select date",
                                R.drawable.ic_warning
                        );
                        intervalCheckPassed = isTextFieldEmpty(
                                repeatingTaskEditBinding.interval,
                                repeatingTaskEditBinding.intervalInputLayout,
                                "Interval cannot be empty",
                                R.drawable.ic_warning
                        );
                        intervalValidityCheckPassed = isIntervalValid(
                                repeatingTaskEditBinding.interval,
                                repeatingTaskEditBinding.intervalInputLayout,
                                "Interval cannot be less than 1",
                                R.drawable.ic_warning,
                                CustomComparator.LESS_THAN,
                                1
                        );
                        if (mViewModel.getSelectedDate().getValue()!= null) {
                            if (repeatingTask != null) {
                                repeatingTask.setContent((repeatingTaskEditBinding.taskContent.getText()!=null)?repeatingTaskEditBinding.taskContent.getText().toString():"");
                                repeatingTask.setInterval((repeatingTaskEditBinding.interval.getText()!=null)?Integer.parseInt(repeatingTaskEditBinding.interval.getText().toString()):1);
                                repeatingTask.setStartDate(mViewModel.getSelectedDate().getValue());
                                repeatingTask.setLastCompleted(mViewModel.getSelectedDate().getValue());
                                repeatingTask.setCurrentCompleted(mViewModel.getSelectedDate().getValue());


                                if (contentCheckPassed&&startDateCheckPassed&&intervalCheckPassed&&intervalValidityCheckPassed) {
                                    mViewModel.updateTask(
                                            repeatingTask,
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
                                                    repeatingTaskEditBinding.saveButton.setEnabled(true);
                                                }
                                            },
                                            300
                                    );
                                }
                            }
                        }
                    }
                });

        //ui input checks
        repeatingTaskEditBinding.taskContent
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
                                        repeatingTaskEditBinding.taskContent,
                                        repeatingTaskEditBinding.taskContentInputLayout,
                                        "Content cannot be empty",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );

        repeatingTaskEditBinding.startDate
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
                                        repeatingTaskEditBinding.startDate,
                                        repeatingTaskEditBinding.startDateInputLayout,
                                        "Please select date",
                                        R.drawable.ic_warning
                                );
                            }
                        }
                );

        repeatingTaskEditBinding.interval
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
                                        repeatingTaskEditBinding.interval,
                                        repeatingTaskEditBinding.intervalInputLayout,
                                        "Interval cannot be empty",
                                        R.drawable.ic_warning
                                );
                                intervalValidityCheckPassed = isIntervalValid(
                                        repeatingTaskEditBinding.interval,
                                        repeatingTaskEditBinding.intervalInputLayout,
                                        "Interval cannot be less than 1",
                                        R.drawable.ic_warning,
                                        CustomComparator.LESS_THAN,
                                        1
                                );
                            }
                        }
                );
    }
}