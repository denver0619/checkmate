package com.gdiff.checkmate.infrastructure.database.tables;

public final class RepeatingTasksTable {
    public static final String tableName = "RepeatingTasks";
    public static final String id = "RepeatingTaskId";
    public static final String content = "Content";
    public static final String status = "Status";
    public static final String startDate = "StartDate";
    public static final String interval = "Interval";
    public static final String lastCompleted = "LastCompleted";
    public static final String currentCompleted = "CurrentCompleted";

    public static final String createTable = "CREATE TABLE "
            + tableName + " ("
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + content + " TEXT, "
            + status + " BOOLEAN, "
            + startDate + " TEXT, "
            + interval + " INTEGER, "
            + lastCompleted + " TEXT, "
            + currentCompleted + " TEXT);";

    public static final String dropTable = "DROP TABLE IF EXISTS " + tableName;
}
