package com.example.signuploginfirebase;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoutinesAdapter extends RecyclerView.Adapter<RoutinesAdapter.RoutineViewHolder> {

    private List<TaskModel> routines;
    private TaskCompletionCallback callback;

    public RoutinesAdapter(List<TaskModel> routines, TaskCompletionCallback callback) {
        if (routines == null) {
            throw new IllegalArgumentException("Routines list cannot be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        this.routines = routines;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        TaskModel task = routines.get(position);

        holder.titleTextView.setText(task.getTitle());
        holder.desTextView.setText(task.getDescription());
        holder.TimeTextView.setText(task.getTime());
        holder.taskCheckbox.setChecked(task.isCompleted());

        // Apply strikethrough and change color based on task completion
        applyStrikethrough(holder.titleTextView, holder.desTextView, holder.TimeTextView, task.isCompleted());

        // Remove existing listener first to prevent multiple triggers when recycling views
        holder.taskCheckbox.setOnCheckedChangeListener(null);
        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            applyStrikethrough(holder.titleTextView, holder.desTextView, holder.TimeTextView, isChecked); // Update all three views
            callback.onTaskCompletionChanged(task); // Notify callback
        });

        // Set click listener on the delete button
        holder.deleteButton.setOnClickListener(v -> deleteRoutine(position)); // Use position instead of undefined 'routine'
    }

    private void deleteRoutine(int position) {
        if (position >= 0 && position < routines.size()) {
            // Proceed with the deletion
            TaskModel task = routines.get(position);
            callback.onTaskDeleted(task);  // Inform the callback to delete the task from Firebase

            // Remove from the list and update the RecyclerView
            routines.remove(position);
            notifyItemRemoved(position);
        } else {
            Log.e("RoutinesAdapter", "Invalid position: " + position);
        }
    }


    private void applyStrikethrough(TextView textView, TextView desTextView, TextView timeTextView, boolean isCompleted) {
        if (isCompleted) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(Color.GRAY); // Set text color to grey
            desTextView.setTextColor(Color.GRAY); // Set text color to grey
            timeTextView.setTextColor(Color.GRAY); // Set text color to grey
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setTextColor(Color.BLACK); // Set text color to black
            desTextView.setTextColor(Color.BLACK); // Set text color to black
            timeTextView.setTextColor(Color.BLACK); // Set text color to black
        }
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    public static class RoutineViewHolder extends RecyclerView.ViewHolder {
        public View deleteButton;
        TextView titleTextView, desTextView, TimeTextView;
        CheckBox taskCheckbox;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_title);
            desTextView = itemView.findViewById(R.id.task_des);
            TimeTextView = itemView.findViewById(R.id.task_time);
            taskCheckbox = itemView.findViewById(R.id.task_checkbox);
            deleteButton = itemView.findViewById(R.id.delete_button); // Make sure you have the delete button view ID
        }
    }

    // Callback interface for task completion updates
    public interface TaskCompletionCallback {
        void onTaskCompletionChanged(TaskModel task);
        void onTaskDeleted(TaskModel task); // New method for deleting tasks
    }
}
