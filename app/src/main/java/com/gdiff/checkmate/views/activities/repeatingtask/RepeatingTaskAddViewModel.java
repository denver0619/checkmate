package com.gdiff.checkmate.views.activities.repeatingtask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gdiff.checkmate.domain.models.RepeatingTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.infrastructure.repositories.RepeatingTasksRepositoryImpl;

import java.util.Date;

public class RepeatingTaskAddViewModel extends AndroidViewModel {
    private final Application _context;
    private RepositoryOnDataChangedCallback _onDataChangedCallback;
    private final MutableLiveData<Date> _selectedDate = new MutableLiveData<>();

    public RepeatingTaskAddViewModel(@NonNull Application application) {
        super(application);
        this._context = application;
    }

    public LiveData<Date> getSelectedDate() {
        return this._selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this._selectedDate.setValue(selectedDate);
    }

    public void addTask(RepeatingTask repeatingTask, RepositoryOnDataChangedCallback onDataChangedCallback) {
        this._onDataChangedCallback = onDataChangedCallback;
        RepeatingTasksRepositoryImpl.getInstance(this._context)
                .registerCallback(onDataChangedCallback);
        RepeatingTasksRepositoryImpl.getInstance(this._context)
                .add(repeatingTask);
    }

    @Override
    public void onCleared() {
        if (this._onDataChangedCallback != null) {
            RepeatingTasksRepositoryImpl.getInstance(this._context)
                    .unregisterCallback(this._onDataChangedCallback);
        }
    }
}
