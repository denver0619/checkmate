package com.gdiff.checkmate.domain.models;

import java.io.Serializable;

public abstract class TaskModel implements Serializable {
    public abstract int getId();
    public abstract Boolean isDone();
    public abstract String content();


    //necessary for diffutils
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
