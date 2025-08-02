package com.gdiff.checkmate.domain.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.gdiff.checkmate.infrastructure.database.tables.ScheduledTasksTable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

public class ScheduledTask extends TaskModel<ScheduledTask>{
    private int _id;
    private String _content;
    private Boolean _status;
    private Date _dueDate;

    public ScheduledTask() {}

    public ScheduledTask(int id, String content, Boolean status, Date dueDate) {
        this._id = id;
        this._content = content;
        this._status = status;
        this._dueDate = dueDate;
    }

    public void setId (int id) {
        this._id = id;
    }

    public int getId() {
        return  this._id;
    }

    public void setContent(String content) {
        this._content = content;
    }

    public String getContent() {
        return  this._content;
    }

    public void setStatus(Boolean status) {
        this._status = status;
    }

    public Boolean getStatus() {
        return  this._status;
    }

    public void setDueDate(Date dueDate) {
        this._dueDate = dueDate;
    }

    public Date getDueDate(){
        return this._dueDate;
    }

    /**
     * Requires date in ISO8601 format "yyyy-MM-dd HH:mm:ss"
     * e.g. 2025-06-29T20:30:00
     * @param dueDate
     */
    public void setDueDateString(String dueDate) {
        LocalDateTime localDateTime = LocalDateTime.parse(dueDate);
        this._dueDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @return a {@link String} in ISO8601 format "yyyy-MM-dd HH:mm:ss"
     */
    public String getDueDateString() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(this._dueDate.toInstant());
    }

    @Override
    public Boolean isDone() {
        return this._status;
    }

    @Override
    public Boolean isExpired() {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = getDueDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return today.isAfter(dueDate);
    }

    @Override
    public String content() {
        return  this._content;
    }

    @Override
    public ScheduledTask fromCursor(Cursor cursor) throws SQLiteException {
        this._id = cursor.getInt(cursor.getColumnIndexOrThrow(ScheduledTasksTable.id));
        this._content = cursor.getString(cursor.getColumnIndexOrThrow(ScheduledTasksTable.content));
        this._status = cursor.getInt(cursor.getColumnIndexOrThrow(ScheduledTasksTable.status)) != 0; //hack from int to boolean
        setDueDateString(cursor.getString(cursor.getColumnIndexOrThrow(ScheduledTasksTable.dueDate)));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledTask)) return false;

        ScheduledTask otherScheduledTask = (ScheduledTask) o;
        return this._id == otherScheduledTask._id
                && Objects.equals(this._content, otherScheduledTask._content)
                && this._status == otherScheduledTask._status
                && this._dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(
                        otherScheduledTask.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _content, _status, _dueDate);
    }
}
