package com.gdiff.checkmate.infrastructure.repositories;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.gdiff.checkmate.domain.models.RepeatingTask;
import com.gdiff.checkmate.domain.models.ScheduledTask;
import com.gdiff.checkmate.domain.repositories.RepeatingTasksRepository;
import com.gdiff.checkmate.domain.repositories.RepositoryListFetchResultCallback;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.domain.repositories.RepositorySingleFetchResultCallback;
import com.gdiff.checkmate.infrastructure.database.TaskDbHelper;
import com.gdiff.checkmate.infrastructure.database.tables.RepeatingTasksTable;
import com.gdiff.checkmate.infrastructure.database.tables.ScheduledTasksTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class RepeatingTasksRepositoryImpl implements RepeatingTasksRepository {
    private final SQLiteDatabase _database;
    private final Application _context;
    private static ExecutorService _executorService;
    private static List<RepositoryOnDataChangedCallback> _callbacks;
    private static volatile RepeatingTasksRepositoryImpl _instance;

    private RepeatingTasksRepositoryImpl(Application context) {
        this._context = context;
        this._database = TaskDbHelper.getInstance(context).getWritableDatabase();
        _callbacks = new ArrayList<>();
        _executorService = Executors.newSingleThreadExecutor();
    }

    public static RepeatingTasksRepositoryImpl getInstance(Application _context) {
        if (_instance != null) {
            synchronized (RepeatingTasksRepositoryImpl.class) {
                _instance = new RepeatingTasksRepositoryImpl(_context);
            }
        }
        return _instance;
    }

    @Override
    public void add(RepeatingTask repeatingTask) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(RepeatingTasksTable.content, repeatingTask.getContent());
                        contentValues.put(RepeatingTasksTable.status, repeatingTask.getStatus());
                        contentValues.put(RepeatingTasksTable.startDate, repeatingTask.getStartDateString());
                        contentValues.put(RepeatingTasksTable.interval, repeatingTask.getInterval());
                        contentValues.put(RepeatingTasksTable.lastCompleted, repeatingTask.getLastCompletedString());
                        contentValues.put(RepeatingTasksTable.currentCompleted, repeatingTask.getCurrentCompletedString());


                        long result = RepeatingTasksRepositoryImpl.this._database.insert(RepeatingTasksTable.tableName, null, contentValues);

                        if (result == -1) {
                            Toast.makeText(RepeatingTasksRepositoryImpl.this._context, "Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            if(!_callbacks.isEmpty()) {
                                for (RepositoryOnDataChangedCallback callback : RepeatingTasksRepositoryImpl._callbacks) {
                                    callback.onDataChanged();
                                }
                            }
                            Toast.makeText(RepeatingTasksRepositoryImpl.this._context, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void update(RepeatingTask repeatingTask) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(RepeatingTasksTable.content, repeatingTask.getContent());
                        contentValues.put(RepeatingTasksTable.status, repeatingTask.getStatus());
                        contentValues.put(RepeatingTasksTable.startDate, repeatingTask.getStartDateString());
                        contentValues.put(RepeatingTasksTable.interval, repeatingTask.getInterval());
                        contentValues.put(RepeatingTasksTable.lastCompleted, repeatingTask.getLastCompletedString());
                        contentValues.put(RepeatingTasksTable.currentCompleted, repeatingTask.getCurrentCompletedString());

                        long result = RepeatingTasksRepositoryImpl.this._database.
                                update(RepeatingTasksTable.tableName,
                                        contentValues,
                                        RepeatingTasksTable.id + " =?",
                                        new String[]{String.valueOf(repeatingTask.getId())});

                        if (result == -1) {
                            Toast.makeText(RepeatingTasksRepositoryImpl.this._context, "Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            if(!_callbacks.isEmpty()) {
                                for (RepositoryOnDataChangedCallback callback : RepeatingTasksRepositoryImpl._callbacks) {
                                    callback.onDataChanged();
                                }
                            }
                            Toast.makeText(RepeatingTasksRepositoryImpl.this._context, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void delete(RepeatingTask repeatingTask) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        RepeatingTasksRepositoryImpl.this._database
                                .delete(
                                        RepeatingTasksTable.tableName,
                                        RepeatingTasksTable.id + " =?",
                                        new String[]{String.valueOf(repeatingTask.getId())}
                                );
                        if(!_callbacks.isEmpty()) {
                            for (RepositoryOnDataChangedCallback callback : RepeatingTasksRepositoryImpl._callbacks) {
                                callback.onDataChanged();
                            }
                        }
                    }
                }
        );

    }

    @Override
    public void deleteAll(List<RepeatingTask> repeatingTasks) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        if (repeatingTasks == null || repeatingTasks.isEmpty()) return;

                        String placeholders = String.join(",", Collections.nCopies(repeatingTasks.size(), "?"));

                        RepeatingTasksRepositoryImpl.this._database.delete(RepeatingTasksTable.tableName,
                                RepeatingTasksTable.id + " IN(" + placeholders + ")",
                                repeatingTasks.stream()
                                        .map(
                                                new Function<RepeatingTask, String>() {
                                                    @Override
                                                    public String apply(RepeatingTask repeatingTask) {
                                                        return String.valueOf(repeatingTask.getId());
                                                    }
                                                }
                                        )
                                        .toArray(String[]::new));
                        if(!_callbacks.isEmpty()) {
                            for (RepositoryOnDataChangedCallback callback : RepeatingTasksRepositoryImpl._callbacks) {
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
                        String query = "SELECT * FROM " + RepeatingTasksTable.tableName;
                        Cursor cursor = null;
                        List<RepeatingTask> results = new ArrayList<>();

                        if (RepeatingTasksRepositoryImpl.this._database != null) {
                            cursor = RepeatingTasksRepositoryImpl.this._database.rawQuery(query, null);
                        }

                        if (cursor != null) {
                            try {
                                for (cursor.moveToFirst(); cursor.isAfterLast(); cursor.moveToNext()) {
                                    results.add(
                                            new RepeatingTask().fromCursor(cursor)
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
                        String query = "SELECT * FROM " + RepeatingTasksTable.tableName + " WHERE id = " + String.valueOf(id) + ";";
                        Cursor cursor = null;
                        List<RepeatingTask> results = new ArrayList<>();

                        if (RepeatingTasksRepositoryImpl.this._database != null) {
                           cursor = RepeatingTasksRepositoryImpl.this._database.rawQuery(query, null);
                        }

                        if (cursor != null) {
                            try {
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                                    results.add(
                                            new RepeatingTask().fromCursor(cursor)
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
