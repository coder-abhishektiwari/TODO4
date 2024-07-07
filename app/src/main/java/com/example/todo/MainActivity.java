package com.example.todo;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.todo.Adapter.RecyclerViewAdapter;
import com.example.todo.Database.DBHandler;
import com.example.todo.Model.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnTaskClickListener, RecyclerViewAdapter.TaskUpdateListener {
    ImageView imgDP, expand;
    TextView txtTaskAvailable, txtTaskCompleted, txtTaskPending;
    TextView txtIndicationCompleted;
    private ProgressBar progressBarCircular;
    private TextView progressText;
    CheckBox checkBox;
    LinearLayout header;
    RelativeLayout head, intro;
    int flag = 0;

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


        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownHeader();
            }
        });
        setupRecyclerView();
        updateTaskCounters();
        updateTaskProgress();

        floatingButton = findViewById(R.id.floatingButton);

        floatingButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshTaskList();
        noTaskSwitchRecyclerView();
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
        progressBarCircular = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        expand = findViewById(R.id.expend);
        header = findViewById(R.id.header);
        head = findViewById(R.id.head);
        head.bringToFront();
        intro = findViewById(R.id.intro);
    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, taskList, this, this);
        recyclerView.setAdapter(adapter);
        noTaskSwitchRecyclerView();
        refreshTaskList();
    }
    @Override
    public void onEditClick(int position) {
        TaskModel task = taskList.get(position);
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }
    @Override
    public void onDeleteClick(int position) {
        TaskModel task = taskList.get(position);
        db.deleteTask(task);
        taskList.remove(position);
        adapter.notifyItemRemoved(position);
        refreshTaskList();
        updateTaskCounters();
        updateTaskProgress();
        noTaskSwitchRecyclerView();
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
        recyclerView.post(() -> {
            updateTaskCounters();
            updateTaskProgress();
        });
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
            completedCount = db.getAllCompletedTasks().size();
            pendingCount = totalTasks - completedCount;

            txtTaskAvailable.setText(totalTasks + " Tasks Available");
            txtTaskCompleted.setText(completedCount + " Tasks Completed out of " + totalTasks);
            txtTaskPending.setText(pendingCount + " Tasks Active out of " + totalTasks);
        } else {
            Log.e("MainActivity", "Database handler is null");
        }


    }
    private void updateTaskProgress() {
        int totalTasks = db.getTasksCount();
        int completedTasks = db.getAllCompletedTasks().size();
        int progress = (int) ((completedTasks / (float) totalTasks) * 100);

        progressBarCircular.setMax(100);
        progressBarCircular.setProgress(progress);
        progressText.setText(progress + "% Completed");

    }

    public void dropDownHeader(){

        if (flag == 0){
            //header.setTranslationY(0);

            header.animate().translationYBy(280);
            header.bringToFront();
            expand.setImageResource(R.drawable.baseline_arrow_drop_up_24);
            flag = 1;
        }else{
            //header.setTranslationY(-280);
            head.bringToFront();
            header.animate().translationYBy(-280);
            expand.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            flag = 0;
        }
    }

    public void noTaskSwitchRecyclerView(){
        if(taskList.isEmpty()){
            recyclerView.setVisibility(View.INVISIBLE);
            intro.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            intro.setVisibility(View.INVISIBLE);
        }
    }

}
