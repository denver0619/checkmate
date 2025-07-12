package com.gdiff.checkmate.infrastructure.database;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.gdiff.checkmate.infrastructure.database.tables.RepeatingTasksTable;
import com.gdiff.checkmate.infrastructure.database.tables.ScheduledTasksTable;
import com.gdiff.checkmate.infrastructure.database.tables.TodoTasksTable;

public final class TaskDbHelper extends SQLiteOpenHelper {
    private static volatile TaskDbHelper _instance;
    private static final String _databaseName = "task.db";
    private static final int _databaseVersion = 1;
    private static final SQLiteDatabase.CursorFactory _databaseFactory = null;

    private TaskDbHelper(@Nullable Application context) {
        super(context, _databaseName, _databaseFactory, _databaseVersion);
    }

    public static TaskDbHelper getInstance(Application applicationContext) {
        if (_instance == null) {
            synchronized (TaskDbHelper.class) {
                if(_instance == null) {
                    _instance = new TaskDbHelper(applicationContext);
                }
            }
        }
        return _instance;
     }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TodoTasksTable.createTable);
        sqLiteDatabase.execSQL(ScheduledTasksTable.createTable);
        sqLiteDatabase.execSQL(RepeatingTasksTable.createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(TodoTasksTable.dropTable);
        sqLiteDatabase.execSQL(ScheduledTasksTable.dropTable);
        sqLiteDatabase.execSQL(RepeatingTasksTable.dropTable);
        onCreate(sqLiteDatabase);
    }
}
