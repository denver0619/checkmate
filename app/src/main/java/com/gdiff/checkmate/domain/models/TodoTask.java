package com.gdiff.checkmate.domain.models;

import java.util.Objects;

public class TodoTask extends TaskModel {
    private int _id;
    private String _content;
    private Boolean _status;

    public TodoTask() {}

    public TodoTask(int id, String content, Boolean status) {
        this._id = id;
        this._content = content;
        this._status = status;
    }

    public void setId(int id) {
        this._id = id;
    }

    public int getId () {
        return this._id;
    }

    public void setContent(String content) {
        this._content = content;
    }

    public String getContent() {
        return this._content;
    }

    public void setStatus(Boolean status) {
        this._status = status;
    }

    public Boolean getStatus() {
        return this._status;
    }


    @Override
    public Boolean isDone() {
        return this._status;
    }

    @Override
    public Boolean isExpired() {
        return false;
    }

    @Override
    public String content() {
        return  this._content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoTask)) return false;

        TodoTask otherTodoTask = (TodoTask) o;
        return this._id == otherTodoTask._id
                && Objects.equals(this._content, otherTodoTask._content)
                && this._status == otherTodoTask._status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _content, _status);
    }
}
