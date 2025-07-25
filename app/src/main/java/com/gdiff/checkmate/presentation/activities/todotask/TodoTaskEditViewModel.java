package com.gdiff.checkmate.presentation.activities.todotask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class TodoTaskEditViewModel extends AndroidViewModel {
    private final Application _application;

    public TodoTaskEditViewModel(@NonNull Application application) {
        super(application);
        this._application = application;
    }
}
