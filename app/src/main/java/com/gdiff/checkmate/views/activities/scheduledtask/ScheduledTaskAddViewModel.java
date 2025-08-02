package com.gdiff.checkmate.views.activities.scheduledtask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gdiff.checkmate.domain.models.ScheduledTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.infrastructure.repositories.ScheduledTasksRepositoryImpl;

import java.util.Date;

public class ScheduledTaskAddViewModel extends AndroidViewModel {

    private final Application _applicaContext;
    private RepositoryOnDataChangedCallback _onDataChangedCallback;
    private final MutableLiveData<Date> _selectedDate = new MutableLiveData<>();

    public ScheduledTaskAddViewModel(@NonNull Application application) {
        super(application);
        this._applicaContext = application;
    }

    public LiveData<Date> getSelectedDate() {
        return this._selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this._selectedDate.setValue(selectedDate);
    }

    public void addTask(ScheduledTask scheduledTask, RepositoryOnDataChangedCallback onDataChangedCallback) {
        this._onDataChangedCallback = onDataChangedCallback;
        ScheduledTasksRepositoryImpl.getInstance(this._applicaContext)
                        .registerCallback(onDataChangedCallback);
        ScheduledTasksRepositoryImpl.getInstance(this._applicaContext)
                .add(scheduledTask);
    }

    @Override
    public void onCleared() {
        if (this._onDataChangedCallback != null) {
            ScheduledTasksRepositoryImpl.getInstance(this._applicaContext)
                    .unregisterCallback(this._onDataChangedCallback);
        }
    }

}
