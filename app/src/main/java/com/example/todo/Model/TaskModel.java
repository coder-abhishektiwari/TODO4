package com.example.todo.Model;

import android.widget.CheckBox;

import com.example.todo.MainActivity;
import com.example.todo.R;

public class TaskModel {
    private int id;
    private String taskTitle;
    private String taskDescription;
    private String dateTime;
    private boolean isCompleted;

    public TaskModel() {
    }
    public TaskModel(int id, String taskTitle, String taskDescription, String dateTime, boolean isCompleted) {
        this.id = id;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.dateTime = dateTime;
        this.isCompleted = isCompleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;

    }
}
