package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.todo.Adapter.RecyclerViewAdapter;
import com.example.todo.Database.DBHandler;
import com.example.todo.Model.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnTaskClickListener, RecyclerViewAdapter.TaskUpdateListener {
    ImageView imgDP;
    TextView txtTaskAvailable, txtTaskCompleted, txtTaskPending;
    TextView txtIndicationCompleted;
    CheckBox checkBox;

    ProgressBar progressTaskAvailable, progressTaskCompleted, progressTaskPending;
    RecyclerView recyclerView;
    FloatingActionButton floatingButton;
    DBHandler db;
    List<TaskModel> taskList;
    RecyclerViewAdapter adapter;
    List<TaskModel> completedTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHandler(this);
        taskList = db.getAllTask();
        // Get all completed tasks from DB
        completedTasks = db.getAllCompletedTasks();

        initializeViews();
        setupRecyclerView();
        updateTaskCounters();

        floatingButton = findViewById(R.id.floatingButton);

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
                finish();
            }
        });
    }
    private void initializeViews() {
        imgDP = findViewById(R.id.imgDP);
        txtTaskAvailable = findViewById(R.id.txtTaskAvailable);
        txtTaskCompleted = findViewById(R.id.txtTaskCompleted);
        txtTaskPending = findViewById(R.id.txtTaskPending);
        progressTaskAvailable = findViewById(R.id.progressTaskAvailable);
        progressTaskCompleted = findViewById(R.id.progressTaskCompleted);
        progressTaskPending = findViewById(R.id.progressTaskPending);
        recyclerView = findViewById(R.id.recyclerView);
        txtIndicationCompleted = findViewById(R.id.txtIndicateCompleted);
        checkBox = findViewById(R.id.checkBoxCompleted);

    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, taskList, this, this);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onEditClick(int position) {
        TaskModel task = taskList.get(position);
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
        finish();
    }
    @Override
    public void onDeleteClick(int position) {
        TaskModel task = taskList.get(position);
        db.deleteTask(task);
        taskList.remove(position);
        adapter.notifyItemRemoved(position);
        refreshTaskList();
        updateTaskCounters();
    }
    @Override
    public void onCompletionClick(int position) {

        TaskModel task = new TaskModel();
        int index = taskList.indexOf(task);
        if (index != -1) { // Ensure the task is found in the list
            taskList.get(index).setCompleted(task.isCompleted());
            // Optionally, update your database here
            db.updateTask(taskList.get(index));
            adapter.notifyItemChanged(index);// Notify adapter of data change

        }
    }
    private void refreshTaskList() {
        taskList.clear();
        taskList.addAll(db.getAllTask());
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onTaskUpdated() {
        taskList.clear();
        taskList.addAll(db.getAllTask());
        adapter.notifyDataSetChanged();
        updateTaskCounters();
    }
    public void updateTaskCounters() {
        if (db != null){
            int completedCount = 0;
            int pendingCount = 0;
            for (TaskModel task : taskList) {
                if (task.isCompleted()) {
                    completedCount++;
                } else {
                    pendingCount++;
                }
            }
            int totalTasks = db.getTasksCount();
            completedCount = db.getAllCompletedTasks().size(); // Number of completed tasks
            pendingCount = totalTasks - completedCount; // Number of pending tasks

            txtTaskAvailable.setText(totalTasks + " Tasks Available");
            txtTaskCompleted.setText(completedCount + " Tasks Completed out of " + totalTasks);
            txtTaskPending.setText(pendingCount + " Tasks Pending out of " + totalTasks);
        } else {
            Log.e("MainActivity", "Database handler is null");
        }


    }

}
