package com.gdiff.checkmate.infrastructure.repositories;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.RepositoryOnDataChangedCallback;
import com.gdiff.checkmate.domain.repositories.RepositoryListFetchResultCallback;
import com.gdiff.checkmate.domain.repositories.RepositorySingleFetchResultCallback;
import com.gdiff.checkmate.domain.repositories.TodoTasksRepository;
import com.gdiff.checkmate.infrastructure.database.TaskDbHelper;
import com.gdiff.checkmate.infrastructure.database.tables.TodoTasksTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TodoTasksRepositoryImpl implements TodoTasksRepository {
    private final SQLiteDatabase _database;
    private final Context _context;
    private static List<RepositoryOnDataChangedCallback> _callbacks;
    private static volatile TodoTasksRepositoryImpl _instance;
    private final ExecutorService _executorService;

    private TodoTasksRepositoryImpl(Application applicationContext) {
        this._context = applicationContext;
        this._database = TaskDbHelper.getInstance(applicationContext).getWritableDatabase();
        _callbacks = new ArrayList<>();
        this._executorService = Executors.newSingleThreadExecutor();
    }

    public static TodoTasksRepositoryImpl getInstance(Application applicationContext) {
        if (_instance == null) {
            synchronized (TodoTasksRepositoryImpl.class) {
                if(_instance==null) {
                    _instance = new TodoTasksRepositoryImpl(applicationContext);
                }
            }
        }
        return _instance;
    }

    @Override
    public void registerCallback(RepositoryOnDataChangedCallback callback) {
        _callbacks.add(callback);
    }

    @Override
    public void unregisterCallback(RepositoryOnDataChangedCallback callback) {
        _callbacks.remove(callback);
    }


//    @Override
//    public List<TodoTask> getAll() {
//        String query = "SELECT * FROM " + TodoTasksTable.tableName + ";";
//        Cursor cursor = null;
//        List<TodoTask> result = new ArrayList<>();
//        if (this._database != null) {
//            cursor = this._database.rawQuery(query, null);
//        }
//
//        if (cursor != null) {
//            try {
//                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                    result.add(
//                            new TodoTask(
//                                    cursor.getInt(cursor.getColumnIndexOrThrow(TodoTasksTable.id)),
//                                    cursor.getString(cursor.getColumnIndexOrThrow(TodoTasksTable.content)),
//                                    cursor.getInt(cursor.getColumnIndexOrThrow(TodoTasksTable.status)) != 0 //hack from int to boolean
//                            )
//                    );
//                }
//            } catch (SQLiteException err) {
//
//            } finally {
//                cursor.close();
//            }
//        }
//        return result;
//    }

    @Override
    public void getAll(RepositoryListFetchResultCallback fetchCallback) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        String query = "SELECT * FROM " + TodoTasksTable.tableName + ";";
                        Cursor cursor = null;
                        List<TodoTask> result = new ArrayList<>();
                        if (TodoTasksRepositoryImpl.this._database != null) {
                            cursor = TodoTasksRepositoryImpl.this._database.rawQuery(query, null);
                        }

                        if (cursor != null) {
                            try {
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    result.add(
                                            new TodoTask(
                                                    cursor.getInt(cursor.getColumnIndexOrThrow(TodoTasksTable.id)),
                                                    cursor.getString(cursor.getColumnIndexOrThrow(TodoTasksTable.content)),
                                                    cursor.getInt(cursor.getColumnIndexOrThrow(TodoTasksTable.status)) != 0 //hack from int to boolean
                                            )
                                    );
                                }
                            } catch (SQLiteException err) {

                            } finally {
                                cursor.close();
                            }
                        }
                        fetchCallback.onFetch(result);
                    }
                }
        );
    }

//    @Override
//    public TodoTask getById(int id) {
//        String query = "SELECT * FROM " + TodoTasksTable.tableName + " WHERE id = " + String.valueOf(id) + " ;";
//        Cursor cursor = null;
//        List<TodoTask> result = new ArrayList<>();
//
//        if (this._database != null) {
//            cursor = this._database.rawQuery(query, null);
//        }
//
//        if (cursor != null ) {
//            try {
//                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                    result.add(
//                            new TodoTask(
//                                    cursor.getInt(cursor.getColumnIndexOrThrow(TodoTasksTable.id)),
//                                    cursor.getString(cursor.getColumnIndexOrThrow(TodoTasksTable.content)),
//                                    cursor.getInt(cursor.getColumnIndexOrThrow(TodoTasksTable.status)) != 0 //hack from int to boolean
//                            )
//                    );
//                }
//            } catch (SQLiteException err) {
//
//            } finally {
//                cursor.close();
//            }
//        }
//        return (result.isEmpty())?null:result.get(0);
//    }

    @Override
    public void getById(int id, RepositorySingleFetchResultCallback fetchCallback) {
        this._executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        String query = "SELECT * FROM " + TodoTasksTable.tableName + " WHERE id = " + String.valueOf(id) + " ;";
                        Cursor cursor = null;
                        List<TodoTask> result = new ArrayList<>();

                        if (TodoTasksRepositoryImpl.this._database != null) {
                            cursor = TodoTasksRepositoryImpl.this._database.rawQuery(query, null);
                        }

                        if (cursor != null ) {
                            try {
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    result.add(
                                            new TodoTask(
                                                    cursor.getInt(cursor.getColumnIndexOrThrow(TodoTasksTable.id)),
                                                    cursor.getString(cursor.getColumnIndexOrThrow(TodoTasksTable.content)),
                                                    cursor.getInt(cursor.getColumnIndexOrThrow(TodoTasksTable.status)) != 0 //hack from int to boolean
                                            )
                                    );
                                }
                            } catch (SQLiteException err) {

                            } finally {
                                cursor.close();
                            }
                        }
                        fetchCallback.onFetch((result.isEmpty())?null:result.get(0));
                    }
                }
        );
    }

    @Override
    public void add(TodoTask todoTask) {
        this._executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {ContentValues values = new ContentValues();
                        values.put(TodoTasksTable.content, todoTask.getContent());
                        values.put(TodoTasksTable.status, todoTask.getStatus());
                        long result = TodoTasksRepositoryImpl.this._database.insert(TodoTasksTable.tableName, null, values);
                        if (result == -1) {
                            Toast.makeText(TodoTasksRepositoryImpl.this._context, "Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            if(!_callbacks.isEmpty()) {
                                for (RepositoryOnDataChangedCallback callback : TodoTasksRepositoryImpl._callbacks) {
                                    callback.onDataChanged();
                                }
                            }
                            Toast.makeText(TodoTasksRepositoryImpl.this._context, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void update(TodoTask todoTask) {
        this._executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        ContentValues values = new ContentValues();
                        values.put(TodoTasksTable.id, todoTask.getId());
                        values.put(TodoTasksTable.content, todoTask.getContent());
                        values.put(TodoTasksTable.status, todoTask.getStatus());
                        long result = TodoTasksRepositoryImpl.this._database.update(TodoTasksTable.tableName,
                                values,
                                TodoTasksTable.id+" =?",
                                new String[]{String.valueOf(todoTask.getId())});
                        if (result == -1) {
                            Toast.makeText(TodoTasksRepositoryImpl.this._context, "Failed to update values.", Toast.LENGTH_SHORT).show();
                        } else {
                            if(!_callbacks.isEmpty()) {
                                for (RepositoryOnDataChangedCallback callback : TodoTasksRepositoryImpl._callbacks) {
                                    callback.onDataChanged();
                                }
                            }
                            Toast.makeText(TodoTasksRepositoryImpl.this._context, "Successfully updated values.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void delete(TodoTask todoTask) {
        this._executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        TodoTasksRepositoryImpl.this._database.delete(TodoTasksTable.tableName,
                                TodoTasksTable.id + " =?",
                                new String[]{String.valueOf(todoTask.getId())});
                        if(!_callbacks.isEmpty()) {
                            for (RepositoryOnDataChangedCallback callback : TodoTasksRepositoryImpl._callbacks) {
                                callback.onDataChanged();
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void deleteAll(List<TodoTask> todoTasks) {
        this._executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        if (todoTasks == null || todoTasks.isEmpty()) return;

                        // Build WHERE clause
                        StringBuilder whereClause = new StringBuilder("id IN (");
                        String[] args = new String[todoTasks.size()];
                        for (int i = 0; i < todoTasks.size(); i++) {
                            whereClause.append("?");
                            if (i != todoTasks.size() - 1) {
                                whereClause.append(", ");
                            }
                            args[i] = String.valueOf(todoTasks.get(i).getId());
                        }
                        whereClause.append(")");

                        // Perform delete
                        _database.delete(TodoTasksTable.tableName, whereClause.toString(), args);

                        if(!_callbacks.isEmpty()) {
                            for (RepositoryOnDataChangedCallback callback : TodoTasksRepositoryImpl._callbacks) {
                                callback.onDataChanged();
                            }
                        }
                    }
                }
        );
    }


    @Override
    public void onDestroy() {
        if(this._executorService != null) {
            this._executorService.shutdown();
        }
    }
}
