package com.gdiff.checkmate.views.activities.scheduledtask;

import android.os.Bundle;
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
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
//                        CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
//                                .setValidator(DateValidatorPointForward.now())
//                                .build();

                        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
//                                .setCalendarConstraints(
//                                        calendarConstraints
//                                )
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
                    if (mViewModel.getSelectedDate().getValue()!= null) {
                        if (scheduledTask != null) {
                            scheduledTask.setContent((scheduledTaskEditBinding.taskContent.getText()!= null)?scheduledTaskEditBinding.taskContent.getText().toString():"");
                            scheduledTask.setDueDate(mViewModel.getSelectedDate().getValue());
                            mViewModel.updateTask(
                                scheduledTask,
                                new RepositoryOnDataChangedCallback() {
                                    @Override
                                    public void onDataChanged() {
                                        finish();
                                    }
                                }
                            );
                        }
                    }
                }
            }
        );
    }
}