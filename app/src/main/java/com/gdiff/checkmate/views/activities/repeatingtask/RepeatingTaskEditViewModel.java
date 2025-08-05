package com.gdiff.checkmate.views.activities.repeatingtask;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gdiff.checkmate.domain.models.RepeatingTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.infrastructure.repositories.RepeatingTasksRepositoryImpl;

import java.util.Date;

public class RepeatingTaskEditViewModel extends AndroidViewModel {
    private final Application _context;
    private RepositoryOnDataChangedCallback _onDataChangedCallback;
    private final MutableLiveData<Date> _selectedDate = new MutableLiveData<>();

    public RepeatingTaskEditViewModel(@NonNull Application application) {
        super(application);
        this._context = application;
    }

    public LiveData<Date> getSelectedDate() {
        return this._selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this._selectedDate.setValue(selectedDate);
    }

    public void updateTask(RepeatingTask repeatingTask, RepositoryOnDataChangedCallback onDataChangedCallback) {
        Log.d("VIEWMODEL", "Updating task ID: " + repeatingTask.getId());
        Log.d("VIEWMODEL", "Updating task ID: " + repeatingTask.getId());
        Log.d("VIEWMODEL", "Updating task ID: " + repeatingTask.getId());
        Log.d("VIEWMODEL", "Updating task ID: " + repeatingTask.getId());
        Log.d("VIEWMODEL", "Updating task ID: " + repeatingTask.getId());
        this._onDataChangedCallback = onDataChangedCallback;
        RepeatingTasksRepositoryImpl.getInstance(this._context)
                .registerCallback(onDataChangedCallback);
        RepeatingTasksRepositoryImpl.getInstance(this._context)
                .update(repeatingTask);
    }

    @Override
    public void onCleared() {
        if(this._onDataChangedCallback != null) {
            RepeatingTasksRepositoryImpl.getInstance(this._context)
                    .unregisterCallback(this._onDataChangedCallback);
        }
    }
}
