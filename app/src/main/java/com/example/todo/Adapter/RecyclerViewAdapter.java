package com.example.todo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.AddTaskActivity;
import com.example.todo.Database.DBHandler;
import com.example.todo.MainActivity;
import com.example.todo.Model.TaskModel;
import com.example.todo.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {
    private final List<TaskModel> taskList;
    private List<TaskModel> mData;
    private Context context;
    private DBHandler dbHandler;
    private OnTaskClickListener onTaskClickListener;
    private TaskUpdateListener taskUpdateListener;

    public RecyclerViewAdapter(Context context, List<TaskModel> taskList, OnTaskClickListener onTaskClickListener, TaskUpdateListener taskUpdateListener) {
        this.context = context;
        this.taskList = taskList;
        this.dbHandler = new DBHandler(context);
        this.onTaskClickListener = onTaskClickListener;
        this.taskUpdateListener = taskUpdateListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view, onTaskClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskModel task = taskList.get(position);

        holder.title.setText(task.getTaskTitle());
        holder.des.setText(task.getTaskDescription());
        holder.dateTime.setText(task.getDateTime());
        holder.checkBoxCompleted.setChecked(task.isCompleted());

        // Remove existing listener before setting a new one
        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            dbHandler.updateTask(task);
            if (taskUpdateListener != null) {
                taskUpdateListener.onTaskUpdated();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            context.startActivity(intent);
        });

        holder.edit.setOnClickListener(v -> onTaskClickListener.onEditClick(position));
        holder.delete.setOnClickListener(v -> onTaskClickListener.onDeleteClick(position));
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // Method to update data in the adapter
    public void setData(List<TaskModel> data) {
        this.mData = data;
        notifyDataSetChanged(); // Notify RecyclerView that data has changed
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, des, dateTime;
        CheckBox checkBoxCompleted;
        public ImageView edit, delete;
        OnTaskClickListener onTaskClickListener;

        public ViewHolder(@NonNull View itemView, OnTaskClickListener onTaskClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            des = itemView.findViewById(R.id.taskDes);
            dateTime = itemView.findViewById(R.id.taskDateTime);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            this.onTaskClickListener = onTaskClickListener;

            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
            checkBoxCompleted.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == R.id.edit) {
                onTaskClickListener.onEditClick(position);
            } else if (v.getId() == R.id.delete) {
                onTaskClickListener.onDeleteClick(position);
            } else if (v.getId() == R.id.checkBoxCompleted) {
                onTaskClickListener.onCompletionClick(position);
            }
        }
    }

    public interface OnTaskClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onCompletionClick(int position);
    }

    public interface TaskUpdateListener {
        void onTaskUpdated();
    }

}
