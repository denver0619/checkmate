package com.gdiff.checkmate.domain.models;

import android.database.Cursor;

import java.io.Serializable;

public abstract class TaskModel<T extends TaskModel<T>> implements Serializable {
    public abstract int getId();
    public abstract Boolean isDone();
    public abstract Boolean isExpired();
    public abstract String content();
    public abstract T fromCursor(Cursor cursor);


    //necessary for diffutils
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
