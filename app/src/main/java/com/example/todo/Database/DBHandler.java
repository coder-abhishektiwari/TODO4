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
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "todoManager";
    private static final String TABLE_TASKS = "tasks";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "taskTitle";
    private static final String KEY_DESCRIPTION = "taskDes";
    private static final String KEY_DATE_TIME = "taskDateTime";
    private static final String KEY_COMPLETED = "isCompleted";

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
                + KEY_COMPLETED + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + KEY_COMPLETED + " INTEGER DEFAULT 0");
        }
    }
    public void addTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTaskTitle());
        values.put(KEY_DESCRIPTION, task.getTaskDescription());
        values.put(KEY_DATE_TIME, task.getDateTime());
        values.put(KEY_COMPLETED, task.isCompleted() ? 1 : 0);

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
            cursor = db.query(TABLE_TASKS, new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_DATE_TIME, KEY_COMPLETED},
                    KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Check column indices before accessing values
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int titleIndex = cursor.getColumnIndex(KEY_TITLE);
                int descIndex = cursor.getColumnIndex(KEY_DESCRIPTION);
                int dateTimeIndex = cursor.getColumnIndex(KEY_DATE_TIME);
                int completedIndex = cursor.getColumnIndex(KEY_COMPLETED);

                if (idIndex != -1 && titleIndex != -1 && descIndex != -1 && dateTimeIndex != -1 && completedIndex != -1) {
                    task = new TaskModel(
                            cursor.getInt(idIndex),
                            cursor.getString(titleIndex),
                            cursor.getString(descIndex),
                            cursor.getString(dateTimeIndex),
                            cursor.getInt(completedIndex) == 1
                    );
                } else {
                    Log.e("DBHandler", "One or more column indices not found");
                }
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
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Check column indices before accessing values
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int titleIndex = cursor.getColumnIndex(KEY_TITLE);
                    int descIndex = cursor.getColumnIndex(KEY_DESCRIPTION);
                    int dateTimeIndex = cursor.getColumnIndex(KEY_DATE_TIME);
                    int completedIndex = cursor.getColumnIndex(KEY_COMPLETED);

                    if (idIndex != -1 && titleIndex != -1 && descIndex != -1 && dateTimeIndex != -1 && completedIndex != -1) {
                        TaskModel task = new TaskModel(
                                cursor.getInt(idIndex),
                                cursor.getString(titleIndex),
                                cursor.getString(descIndex),
                                cursor.getString(dateTimeIndex),
                                cursor.getInt(completedIndex) == 1
                        );
                        taskList.add(task);
                    } else {
                        Log.e("DBHandler", "One or more column indices not found");
                    }
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
    public int updateTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTaskTitle());
        values.put(KEY_DESCRIPTION, task.getTaskDescription());
        values.put(KEY_DATE_TIME, task.getDateTime());
        values.put(KEY_COMPLETED, task.isCompleted() ? 1 : 0);

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
    //get all completed tasksCount
    public List<TaskModel> getAllCompletedTasks() {
        List<TaskModel> completedTasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Query to fetch all completed tasks
            cursor = db.query(TABLE_TASKS, null, KEY_COMPLETED + "=?", new String[]{"1"}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Log column names for debugging
                    String[] columnNames = cursor.getColumnNames();
                    for (String columnName : columnNames) {
                        Log.d("DBHandler", "Column name: " + columnName);
                    }

                    @SuppressLint("Range") TaskModel task = new TaskModel(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE_TIME)),
                            cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1
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
    // Method to update task completion status
}
