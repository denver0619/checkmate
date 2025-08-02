package com.gdiff.checkmate.infrastructure.repositories;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.gdiff.checkmate.domain.models.ScheduledTask;
import com.gdiff.checkmate.domain.models.TaskModel;
import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.RepositoryListFetchResultCallback;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.domain.repositories.RepositorySingleFetchResultCallback;
import com.gdiff.checkmate.domain.repositories.ScheduledTasksRepository;
import com.gdiff.checkmate.infrastructure.database.TaskDbHelper;
import com.gdiff.checkmate.infrastructure.database.tables.ScheduledTasksTable;
import com.gdiff.checkmate.infrastructure.database.tables.TodoTasksTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class ScheduledTasksRepositoryImpl implements ScheduledTasksRepository {
    private final SQLiteDatabase _database;
    private final Application _context;
    private static ExecutorService _executorService;
    private static List<RepositoryOnDataChangedCallback> _callbacks;
    private static volatile ScheduledTasksRepositoryImpl _instance;

    private ScheduledTasksRepositoryImpl(Application context) {
        this._context = context;
        this._database = TaskDbHelper.getInstance(context).getWritableDatabase();
        _callbacks = new ArrayList<>();
        _executorService = Executors.newSingleThreadExecutor();
    }

    public static ScheduledTasksRepositoryImpl getInstance(Application context) {
        if (_instance == null) {
            synchronized (ScheduledTasksRepositoryImpl.class) {
                if (_instance==null) {
                    _instance = new ScheduledTasksRepositoryImpl(context);
                }
            }
        }
        return _instance;
    }

    @Override
    public void add(ScheduledTask scheduledTask) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ScheduledTasksTable.content, scheduledTask.getContent());
                        contentValues.put(ScheduledTasksTable.status, scheduledTask.getStatus());
                        contentValues.put(ScheduledTasksTable.dueDate, scheduledTask.getDueDateString());

                        long result = ScheduledTasksRepositoryImpl.this._database.insert(ScheduledTasksTable.tableName, null, contentValues);

                        if (result == -1) {
                            Toast.makeText(ScheduledTasksRepositoryImpl.this._context, "Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            if(!_callbacks.isEmpty()) {
                                for (RepositoryOnDataChangedCallback callback : ScheduledTasksRepositoryImpl._callbacks) {
                                    callback.onDataChanged();
                                }
                            }
                            Toast.makeText(ScheduledTasksRepositoryImpl.this._context, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void update(ScheduledTask scheduledTask) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ScheduledTasksTable.content, scheduledTask.getContent());
                        contentValues.put(ScheduledTasksTable.status, scheduledTask.getStatus());
                        contentValues.put(ScheduledTasksTable.dueDate, scheduledTask.getDueDateString());

                        long result = ScheduledTasksRepositoryImpl.this._database.
                                update(ScheduledTasksTable.tableName,
                                        contentValues,
                                        ScheduledTasksTable.id + " =?",
                                        new String[]{String.valueOf(scheduledTask.getId())});
                        if (result == -1) {
                            Toast.makeText(ScheduledTasksRepositoryImpl.this._context, "Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            if(!_callbacks.isEmpty()) {
                                for (RepositoryOnDataChangedCallback callback : ScheduledTasksRepositoryImpl._callbacks) {
                                    callback.onDataChanged();
                                }
                            }
                            Toast.makeText(ScheduledTasksRepositoryImpl.this._context, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

    @Override
    public void delete(ScheduledTask scheduledTask) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        ScheduledTasksRepositoryImpl.this._database
                                .delete(
                                  ScheduledTasksTable.tableName,
                                  ScheduledTasksTable.id + " =?",
                                  new String[]{String.valueOf(scheduledTask.getId())}
                                );
                        if(!_callbacks.isEmpty()) {
                            for (RepositoryOnDataChangedCallback callback : ScheduledTasksRepositoryImpl._callbacks) {
                                callback.onDataChanged();
                            }
                        }
                    }
                }
        );

    }

    @Override
    public void deleteAll(List<ScheduledTask> scheduledTasks) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {

                        if (scheduledTasks == null || scheduledTasks.isEmpty()) return;

                        String placeholders = String.join(",", Collections.nCopies(scheduledTasks.size(), "?"));

                        ScheduledTasksRepositoryImpl.this._database.delete(ScheduledTasksTable.tableName,
                                ScheduledTasksTable.id + " IN(" + placeholders + ")",
                                scheduledTasks.stream()
                                        .map(
                                                new Function<ScheduledTask, String>() {
                                                    @Override
                                                    public String apply(ScheduledTask scheduledTask) {
                                                        return String.valueOf(scheduledTask.getId());
                                                    }
                                                }
                                        )
                                        .toArray(String[]::new));
                        if(!_callbacks.isEmpty()) {
                            for (RepositoryOnDataChangedCallback callback : ScheduledTasksRepositoryImpl._callbacks) {
                                callback.onDataChanged();
                            }
                        }
                    }
                }
        );

    }

    @Override
    public void registerCallback(RepositoryOnDataChangedCallback callback) {
        _callbacks.add(callback);
    }

    @Override
    public void unregisterCallback(RepositoryOnDataChangedCallback callback) {
        _callbacks.remove(callback);
    }

    @Override
    public void getAll(RepositoryListFetchResultCallback fetchCallback) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        String query = "SELECT * FROM " + ScheduledTasksTable.tableName;
                        Cursor cursor = null;
                        List<ScheduledTask> results = new ArrayList<>();

                        if (ScheduledTasksRepositoryImpl.this._database != null) {
                            cursor = ScheduledTasksRepositoryImpl.this._database.rawQuery(query, null);
                        }

                        if (cursor != null) {
                            try {
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    results.add(
                                            new ScheduledTask().fromCursor(cursor)
                                    );
                                }
                            } catch (SQLiteException SQLe) {

                            } finally {
                                cursor.close();
                            }
                        }
                        fetchCallback.onFetch(results);
                    }
                }
        );
    }

    @Override
    public void getById(int id, RepositorySingleFetchResultCallback fetchCallback) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        String query = "SELECT * FROM " + ScheduledTasksTable.tableName + " WHERE id = " + String.valueOf(id) + " ;";
                        Cursor cursor = null;
                        List<ScheduledTask> results = new ArrayList<>();

                        if(ScheduledTasksRepositoryImpl.this._database!=null) {
                            cursor = ScheduledTasksRepositoryImpl.this._database.rawQuery(query, null);
                        }

                        if(cursor != null) {
                            try {
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                                    results.add(
                                            new ScheduledTask().fromCursor(cursor)
                                    );
                                }
                            } catch (SQLiteException SQLe) {

                            } finally {
                            cursor.close();
                            }
                        }
                        fetchCallback.onFetch((results.isEmpty())?null:results.get(0));
                    }
                }
        );

    }

    @Override
    public void onDestroy() {
        if(_executorService!=null) {
            _executorService.shutdown();
        }
    }
}
