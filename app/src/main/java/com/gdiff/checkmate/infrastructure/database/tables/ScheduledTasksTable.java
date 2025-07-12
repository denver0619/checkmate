package com.gdiff.checkmate.infrastructure.database.tables;

public final class ScheduledTasksTable {
    public static final String tableName = "ScheduledTasks";
    public static final String id = "ScheduledTaskId";
    public static final String content = "Content";
    public static final String status = "Status";
    public static final String dueDate = "DueDate";

    public static final String createTable = "CREATE TABLE "
            + tableName + " ("
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + content + " TEXT, "
            + status + " BOOLEAN, "
            + dueDate + " TEXT);";

    public static final String dropTable = "DROP TABLE IF EXISTS " + tableName;

}
