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
    ActivityScheduledTaskAddBinding scheduledTaskAddBinding;
    ScheduledTaskAddViewModel mViewModel;


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

        scheduledTaskAddBinding.saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mViewModel.getSelectedDate().getValue()!= null) {
                            ScheduledTask scheduledTask = new ScheduledTask();
                            scheduledTask.setContent((scheduledTaskAddBinding.taskContent.getText()!= null)?scheduledTaskAddBinding.taskContent.getText().toString():"");
                            scheduledTask.setDueDate(mViewModel.getSelectedDate().getValue());
                            mViewModel.addTask(
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
        );
    }
}