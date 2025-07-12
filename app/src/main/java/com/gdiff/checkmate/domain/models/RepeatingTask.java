package com.gdiff.checkmate.domain.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class RepeatingTask implements TaskModel {
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
    public String content() {
        return  this._content;
    }
}
