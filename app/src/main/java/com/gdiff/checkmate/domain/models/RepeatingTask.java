package com.gdiff.checkmate.domain.models;

import android.database.Cursor;

import com.gdiff.checkmate.infrastructure.database.tables.RepeatingTasksTable;
import com.gdiff.checkmate.infrastructure.database.tables.ScheduledTasksTable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class RepeatingTask extends TaskModel<RepeatingTask> {
    private int _id;
    private String _content;
    private Boolean _status;
    private Date _startDate;
    private int _interval;
    private Date _lastCompleted;
    private Date _currentCompleted;

    public RepeatingTask() {}

    public RepeatingTask(int id, String content, Boolean status, Date startDate, int interval, Date lastCompleted, Date currentCompleted) {
        this._id = id;
        this._content = content;
        this._status = status;
        this._startDate = startDate;
        this._interval = interval;
        this._lastCompleted = lastCompleted;
        this._currentCompleted = currentCompleted;
    }

    public void setId(int id) {
        this._id = id;
    }

    public int getId(){
        return this._id;
    }

    public void setContent(String content) {
        this._content = content;
    }

    public String getContent() {
        return  this._content;
    }

    public void setStatus(Boolean status) {
        if (status) {
            LocalDate today = LocalDate.now();
            LocalDate current = _currentCompleted.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (!today.isEqual(current)) {
                setCurrentCompleted(
                        Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant())
                );
            }
        }
        this._status = status;
    }

    public Boolean getStatus() {
        return this._status;
    }

    public void setStartDate(Date startDate) {
        this._startDate = startDate;
    }

    /**
     * Requires date in ISO8601 format "yyyy-MM-dd HH:mm:ss"
     * e.g. 2025-06-29T20:30:00
     * @param startDate
     */
    public void setStartDateString(String startDate) {
        LocalDateTime localDateTime = LocalDateTime.parse(startDate);
        this._startDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getStartDate() {
        return this._startDate;
    }

    /**
     * @return a {@link String} in ISO8601 format "yyyy-MM-dd HH:mm:ss"
     */
    public String getStartDateString() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(this._startDate.toInstant());
    }

    public void setInterval(int interval){
        this._interval = interval;
    }

    public int getInterval() {
        return  this._interval;
    }

    public void setLastCompleted(Date lastCompleted) {
        this._lastCompleted = lastCompleted;
    }

//    TODO: comment
    public void setLastCompletedString(String lastCompleted) {
        LocalDateTime localDateTime = LocalDateTime.parse(lastCompleted);
        this._lastCompleted = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getLastCompleted() {
        return  this._lastCompleted;
    }

//    TODO: comment
    public String getLastCompletedString() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(this._lastCompleted.toInstant());
    }

    public void setCurrentCompleted(Date currentCompleted) {
        this._currentCompleted = currentCompleted;
    }

    //    TODO: comment
    public void setCurrentCompletedString (String currentCompleted) {
        LocalDateTime localDateTime = LocalDateTime.parse(currentCompleted);
        this._currentCompleted = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getCurrentCompleted() {
        return this._currentCompleted;
    }

    //    TODO: comment
    public String getCurrentCompletedString() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(this._currentCompleted.toInstant());
    }

    @Override
    public Boolean isDone() {
        return this._status;
    }

    @Override
    public Boolean isExpired() {
        //Todo: date checks
        return false;
    }

    @Override
    public String content() {
        return  this._content;
    }

    @Override
    public RepeatingTask fromCursor(Cursor cursor) {
        this._id = cursor.getInt(cursor.getColumnIndexOrThrow(RepeatingTasksTable.id));
        this._content = cursor.getString(cursor.getColumnIndexOrThrow(RepeatingTasksTable.content));
        this._status = cursor.getInt(cursor.getColumnIndexOrThrow(RepeatingTasksTable.status)) != 0; //hack from int to boolean
        setStartDateString(cursor.getString(cursor.getColumnIndexOrThrow(RepeatingTasksTable.startDate)));
        this._interval = cursor.getInt(cursor.getColumnIndexOrThrow(RepeatingTasksTable.interval));
        setLastCompletedString(cursor.getString(cursor.getColumnIndexOrThrow(RepeatingTasksTable.lastCompleted)));
        setCurrentCompletedString(cursor.getString(cursor.getColumnIndexOrThrow(RepeatingTasksTable.currentCompleted)));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepeatingTask)) return false;

        RepeatingTask otherRepeatingTask = (RepeatingTask) o;
        return this._id == otherRepeatingTask._id
                && Objects.equals(this._content, otherRepeatingTask._content)
                && this._status == otherRepeatingTask._status
                && isSameDate(this._startDate, otherRepeatingTask._startDate)
                && this._interval == otherRepeatingTask._interval
                && isSameDate(this._lastCompleted, otherRepeatingTask._lastCompleted)
                && isSameDate(this._currentCompleted, otherRepeatingTask._currentCompleted);
    }

    private boolean isSameDate(Date d1, Date d2) {
        if (d1 == null && d2 == null) return true;
        if (d1 == null || d2 == null) return false;
        LocalDate ld1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ld2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ld1.isEqual(ld2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _content, _status, toLocalDate(_startDate), _interval, toLocalDate(_lastCompleted), toLocalDate(_currentCompleted));
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
