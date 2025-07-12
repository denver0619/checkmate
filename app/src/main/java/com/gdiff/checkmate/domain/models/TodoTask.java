package com.gdiff.checkmate.domain.models;

public class TodoTask implements TaskModel {
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
    public String content() {
        return  this._content;
    }
}
