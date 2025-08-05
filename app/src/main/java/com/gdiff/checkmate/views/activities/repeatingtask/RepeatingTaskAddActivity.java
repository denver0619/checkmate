package com.gdiff.checkmate.views.activities.repeatingtask;

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
import com.gdiff.checkmate.databinding.ActivityRepeatingTaskAddBinding;
import com.gdiff.checkmate.domain.models.RepeatingTask;
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

public class RepeatingTaskAddActivity extends BaseTaskActivity {
    private ActivityRepeatingTaskAddBinding repeatingTaskAddBinding;
    private RepeatingTaskAddViewModel mViewModel;

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
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
//                        CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
//                                .setValidator(DateValidatorPointForward.now())
//                                .build();

                        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
//                                .setCalendarConstraints(
//                                        calendarConstraints
//                                )
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
                        if (mViewModel.getSelectedDate().getValue() != null) {
                            RepeatingTask repeatingTask = new RepeatingTask();
                            repeatingTask.setContent((repeatingTaskAddBinding.taskContent.getText()!=null)?repeatingTaskAddBinding.taskContent.getText().toString():"");
                            repeatingTask.setInterval((repeatingTaskAddBinding.interval.getText()!=null)?Integer.parseInt(repeatingTaskAddBinding.interval.getText().toString()):1);
                            repeatingTask.setStartDate(mViewModel.getSelectedDate().getValue());
                            repeatingTask.setLastCompleted(mViewModel.getSelectedDate().getValue());
                            repeatingTask.setCurrentCompleted(mViewModel.getSelectedDate().getValue());

                            mViewModel.addTask(repeatingTask,
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