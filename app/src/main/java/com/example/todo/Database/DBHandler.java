package com.example.todo.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.todo.Model.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "todoManager";
    private static final String TABLE_TASKS = "tasks";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "taskTitle";
    private static final String KEY_DESCRIPTION = "taskDes";
    private static final String KEY_DATE_TIME = "taskDateTime";
    private static final String KEY_COMPLETED = "isCompleted";
    private static final String KEY_PRIORITY = "priority";  // Add this line

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE_TIME + " TEXT,"
                + KEY_COMPLETED + " INTEGER DEFAULT 0,"
                + KEY_PRIORITY + " INTEGER" + ")";  // Add this line
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + KEY_PRIORITY + " INTEGER DEFAULT 0");
        }
    }

    public void addTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTaskTitle());
        values.put(KEY_DESCRIPTION, task.getTaskDescription());
        values.put(KEY_DATE_TIME, task.getDateTime());
        values.put(KEY_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(KEY_PRIORITY, task.getPriority());  // Add this line

        try {
            db.insert(TABLE_TASKS, null, values);
        } catch (SQLiteException e) {
            Log.e("DBHandler", "Error inserting task into database", e);
        } finally {
            db.close();
        }
    }

    public TaskModel getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        TaskModel task = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_TASKS, new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_DATE_TIME, KEY_COMPLETED, KEY_PRIORITY},
                    KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                task = new TaskModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE_TIME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1,
                        cursor.getString(cursor.getColumnIndex(KEY_PRIORITY))  // Add this line
                );
            }
        } catch (SQLiteException e) {
            Log.e("DBHandler", "Error retrieving task from database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return task;
    }

    public List<TaskModel> getAllTask() {
        List<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            // + " ORDER BY " + KEY_PRIORITY + " DESC, " + KEY_DATE_TIME + " ASC"
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " ORDER BY " + KEY_PRIORITY + " DESC, " + KEY_DATE_TIME + " ASC", null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskModel task = new TaskModel(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE_TIME)),
                            cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1,
                            cursor.getString(cursor.getColumnIndex(KEY_PRIORITY))  // Change to String retrieval
                    );
                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e("DBHandler", "Error retrieving all tasks from database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return taskList;
    }

    public List<TaskModel> getAllCompletedTasks() {
        List<TaskModel> completedTasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_TASKS, null, KEY_COMPLETED + "=?", new String[]{"1"}, null, null, KEY_PRIORITY + " DESC, " + KEY_DATE_TIME + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskModel task = new TaskModel(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE_TIME)),
                            cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1,
                            cursor.getString(cursor.getColumnIndex(KEY_PRIORITY))  // Change to String retrieval
                    );
                    completedTasks.add(task);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e("DBHandler", "Error getting all completed tasks from database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return completedTasks;
    }

    public int updateTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTaskTitle());
        values.put(KEY_DESCRIPTION, task.getTaskDescription());
        values.put(KEY_DATE_TIME, task.getDateTime());
        values.put(KEY_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(KEY_PRIORITY, task.getPriority());  // Add this line

        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_TASKS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(task.getId())});
        } catch (SQLiteException e) {
            Log.e("DBHandler", "Error updating task in database", e);
        } finally {
            db.close();
        }

        return rowsAffected;
    }

    public void deleteTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_TASKS, KEY_ID + " = ?", new String[]{String.valueOf(task.getId())});
        } catch (SQLiteException e) {
            Log.e("DBHandler", "Error deleting task from database", e);
        } finally {
            db.close();
        }
    }

    public int getTasksCount() {
        String countQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();
        } catch (SQLiteException e) {
            Log.e("DBHandler", "Error getting tasks count from database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return count;
    }


}
