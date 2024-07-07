package com.example.todo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todo.Database.DBHandler;
import com.example.todo.Model.TaskModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends Activity {
    TextView windowTitle, cancel;
    EditText edtTaskTitle, edtTaskDescription;
    TextView btnPickDateTime,btnSaveTask;
    Calendar calendar;
    DateFormat dateFormat;
    SimpleDateFormat timeFormat;
    Context context;
    DBHandler db;
    private Spinner prioritySpinner;

    TaskModel taskToUpdate; // Hold the task to be updated, if editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_entry_layout);

        context = this;
        db = new DBHandler(context);

        initializeViews();
        setupDateTimePicker();
        setupSaveTaskButton();
        currentDateTime();

        // Go back to the main activity
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Check if we are editing an existing task
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("task_id")) {
            int taskId = extras.getInt("task_id");
            taskToUpdate = db.getTask(taskId); // Assuming you have a method to get a task by ID
            if (taskToUpdate != null) {
                edtTaskTitle.setText(taskToUpdate.getTaskTitle());
                edtTaskDescription.setText(taskToUpdate.getTaskDescription());
                btnPickDateTime.setText(taskToUpdate.getDateTime());
                prioritySpinner.setSelection(Integer.parseInt(taskToUpdate.getPriority()));
            }
        }
    }
    private void initializeViews() {
        windowTitle = findViewById(R.id.newTaskWindow);
        edtTaskTitle = findViewById(R.id.editTextTaskTitle);
        edtTaskDescription = findViewById(R.id.editTextTaskDescription);
        btnPickDateTime = findViewById(R.id.buttonPickDateTime);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        btnSaveTask = findViewById(R.id.buttonSaveTask);
        cancel = findViewById(R.id.cancel);
    }
    private void setupDateTimePicker() {
        calendar = Calendar.getInstance();
        dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        btnPickDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
    }
    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);

                                        String dateTime = dateFormat.format(calendar.getTime()) +
                                                " " + timeFormat.format(calendar.getTime());
                                        btnPickDateTime.setText(dateTime);
                                    }
                                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void setupSaveTaskButton() {
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
                finish();
            }
        });
    }
    private void saveTask() {
        String title = edtTaskTitle.getText().toString();
        String description = edtTaskDescription.getText().toString();
        String dateTime = btnPickDateTime.getText().toString();
        Integer priority = prioritySpinner.getSelectedItemPosition();

        if (title.isEmpty() || description.isEmpty() || dateTime.isEmpty()) {
            Toast.makeText(context, "Please enter all details", Toast.LENGTH_SHORT).show();
        } else {
            if (taskToUpdate == null) {
                // Adding new task
                TaskModel task = new TaskModel();
                task.setTaskTitle(title);
                task.setTaskDescription(description);
                task.setDateTime(dateTime);
                task.setPriority(String.valueOf(priority));
                task.setCompleted(false); // Default to not completed

                db.addTask(task);
                Toast.makeText(context, "Task saved", Toast.LENGTH_SHORT).show();
            } else {
                // Updating existing task
                taskToUpdate.setTaskTitle(title);
                taskToUpdate.setTaskDescription(description);
                taskToUpdate.setDateTime(dateTime);
                taskToUpdate.setPriority(String.valueOf(priority));

                db.updateTask(taskToUpdate);
                Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK);
            finish();
        }
    }
    private void currentDateTime() {
        calendar = Calendar.getInstance();
        String dateTime = dateFormat.format(calendar.getTime()) + " " + timeFormat.format(calendar.getTime());
        btnPickDateTime.setText(dateTime);
    }
}
