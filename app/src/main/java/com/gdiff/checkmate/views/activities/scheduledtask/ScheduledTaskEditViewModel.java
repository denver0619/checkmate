package com.gdiff.checkmate.views.activities.scheduledtask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gdiff.checkmate.databinding.ActivityScheduledTaskEditBinding;
import com.gdiff.checkmate.domain.models.ScheduledTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.infrastructure.repositories.ScheduledTasksRepositoryImpl;

import java.util.Date;

public class ScheduledTaskEditViewModel extends AndroidViewModel {
    private final Application _applicationContext;
    private RepositoryOnDataChangedCallback _onDataChangedCallback;
    private MutableLiveData<Date> _selectedDate = new MutableLiveData<>();

    public ScheduledTaskEditViewModel(@NonNull Application application) {
        super(application);
        this._applicationContext = application;
    }

    public LiveData<Date> getSelectedDate() {
        return this._selectedDate;
    }
    public void setSelectedDate(Date selectedDate) {
        this._selectedDate.setValue(selectedDate);
    }

    public void updateTask(ScheduledTask scheduledTask, RepositoryOnDataChangedCallback onDataChangedCallback) {
        this._onDataChangedCallback = onDataChangedCallback;
        ScheduledTasksRepositoryImpl.getInstance(this._applicationContext)
                .registerCallback(onDataChangedCallback);
        ScheduledTasksRepositoryImpl.getInstance(this._applicationContext)
                .update(scheduledTask);
    }

    @Override
    public void onCleared() {
        if (_onDataChangedCallback != null) {
            ScheduledTasksRepositoryImpl.getInstance(this._applicationContext)
                    .unregisterCallback(_onDataChangedCallback);
        }
    }

}
