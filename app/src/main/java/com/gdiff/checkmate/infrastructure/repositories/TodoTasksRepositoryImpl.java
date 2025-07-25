package com.gdiff.checkmate.infrastructure.repositories;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.gdiff.checkmate.domain.models.TodoTask;
import com.gdiff.checkmate.domain.repositories.GeneralRepositoryCallback;
import com.gdiff.checkmate.domain.repositories.RepositoryListFetchCallback;
import com.gdiff.checkmate.domain.repositories.RepositorySingleFetchCallback;
import com.gdiff.checkmate.domain.repositories.TodoTasksRepository;
import com.gdiff.checkmate.infrastructure.database.TaskDbHelper;
import com.gdiff.checkmate.infrastructure.database.tables.TodoTasksTable;

import java.util.ArrayList;
import java.util.List;

public final class TodoTasksRepositoryImpl implements TodoTasksRepository {
    private final SQLiteDatabase _database;
    private final Context _context;
    private GeneralRepositoryCallback callback;
    private static volatile TodoTasksRepositoryImpl _instance;

    private TodoTasksRepositoryImpl(Application applicationContext) {
        this._context = applicationContext;
        this._database = TaskDbHelper.getInstance(applicationContext).getWritableDatabase();
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
    public void registerCallback(GeneralRepositoryCallback callback) {
        if (this.callback==null) {
            this.callback = callback;
        }
    }

    @Override
    public void unregisterCallback() {
        this.callback = null;
    }

    @Override
    public List<TodoTask> getAll() {
        String query = "SELECT * FROM " + TodoTasksTable.tableName + ";";
        Cursor cursor = null;
        List<TodoTask> result = new ArrayList<>();
        if (this._database != null) {
            cursor = this._database.rawQuery(query, null);
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
        return result;
    }

    @Override
    public List<TodoTask> getAll(RepositoryListFetchCallback fetchCallback) {
        String query = "SELECT * FROM " + TodoTasksTable.tableName + ";";
        Cursor cursor = null;
        List<TodoTask> result = new ArrayList<>();
        if (this._database != null) {
            cursor = this._database.rawQuery(query, null);
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
        return result;
    }

    @Override
    public TodoTask getById(int id) {
        String query = "SELECT * FROM " + TodoTasksTable.tableName + " WHERE id = " + String.valueOf(id) + " ;";
        Cursor cursor = null;
        List<TodoTask> result = new ArrayList<>();

        if (this._database != null) {
            cursor = this._database.rawQuery(query, null);
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
        return (result.isEmpty())?null:result.get(0);
    }

    @Override
    public TodoTask getById(int id, RepositorySingleFetchCallback fetchCallback) {
        String query = "SELECT * FROM " + TodoTasksTable.tableName + " WHERE id = " + String.valueOf(id) + " ;";
        Cursor cursor = null;
        List<TodoTask> result = new ArrayList<>();

        if (this._database != null) {
            cursor = this._database.rawQuery(query, null);
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
        return (result.isEmpty())?null:result.get(0);
    }

    @Override
    public void add(TodoTask todoTask) {
        ContentValues values = new ContentValues();
        values.put(TodoTasksTable.content, todoTask.getContent());
        values.put(TodoTasksTable.status, todoTask.getStatus());
        long result = this._database.insert(TodoTasksTable.tableName, null, values);
        if (result == -1) {
            Toast.makeText(this._context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this._context, "Success", Toast.LENGTH_SHORT).show();
        }
        if(this.callback != null) {
            this.callback.onAdd();
        }
    }

    @Override
    public void edit(TodoTask todoTask) {
        ContentValues values = new ContentValues();
        values.put(TodoTasksTable.id, todoTask.getId());
        values.put(TodoTasksTable.content, todoTask.getContent());
        values.put(TodoTasksTable.status, todoTask.getStatus());
        long result = this._database.update(TodoTasksTable.tableName,
                values,
                TodoTasksTable.id+" =?",
                new String[]{String.valueOf(todoTask.getId())});
        if (result == -1) {
            Toast.makeText(this._context, "Failed to update values.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this._context, "Successfully updated values.", Toast.LENGTH_SHORT).show();
        }
        if(this.callback != null) {
            this.callback.onUpdate(todoTask.getId());
        }
    }

    @Override
    public void delete(TodoTask todoTask) {
        this._database.delete(TodoTasksTable.tableName,
                TodoTasksTable.id + " =?",
                new String[]{String.valueOf(todoTask.getId())});
        if(this.callback != null) {
            this.callback.onDelete(todoTask.getId());
        }
    }
}
