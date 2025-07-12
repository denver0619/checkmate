package com.gdiff.checkmate.infrastructure.database.tables;

public final class TodoTasksTable {
    public static final String tableName = "TodoTasks";
    public static final String id = "TodoTaskId";
    public static final String content = "Content";
    public static final String status = "Status";

    public static final String createTable = "CREATE TABLE "
            + tableName + " ("
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + content + " TEXT, "
            + status + " BOOLEAN);";

    public static final String dropTable = "DROP TABLE IF EXISTS " + tableName;
}
