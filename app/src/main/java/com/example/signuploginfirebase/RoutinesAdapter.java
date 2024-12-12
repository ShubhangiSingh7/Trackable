package com.example.signuploginfirebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutinesAdapter extends RecyclerView.Adapter<RoutinesAdapter.RoutineViewHolder> {

    private List<TaskModel> routines;
    private TaskCompletionCallback callback;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Context context; // Declare the context variable
    private Map<String, CountDownTimer> ongoingTimers = new HashMap<>();
    private SharedPreferences timerPrefs;


    public RoutinesAdapter(List<TaskModel> routines, TaskCompletionCallback callback, Context context) {
        if (routines == null) {
            throw new IllegalArgumentException("Routines list cannot be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        this.routines = routines;
        this.callback = callback;
        this.context = context;  // Now context is passed and assigned properly
    }


    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize SharedPreferences for saving timer state
        timerPrefs = context.getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);

        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        TaskModel task = routines.get(position);

        holder.titleTextView.setText(task.getTitle());
        holder.desTextView.setText(task.getDescription());
        holder.TimeTextView.setText(task.getTime());
        holder.taskCheckbox.setChecked(task.isCompleted());
        holder.durationTextView.setText(task.getDuration() > 0 ? task.getDuration() + " min" : "N/A");

        applyStrikethrough(holder.titleTextView, holder.desTextView, holder.TimeTextView, task.isCompleted(), holder.durationTextView);

        holder.taskCheckbox.setOnCheckedChangeListener(null);
        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            applyStrikethrough(holder.titleTextView, holder.desTextView, holder.TimeTextView, isChecked, holder.durationTextView);
            callback.onTaskCompletionChanged(task);
        });

        holder.itemView.setOnClickListener(v -> showTimerDialog(task, holder, position));

        holder.deleteButton.setOnClickListener(v -> deleteRoutine(position));

        // Restore timer if it was ongoing
        restoreTimerState(task, holder, position);
    }



    private void showTimerDialog(final TaskModel task, final RoutineViewHolder holder, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.timer_dialog, null);
        builder.setView(dialogView);

        final EditText durationInput = dialogView.findViewById(R.id.timer_duration_input);

        builder.setTitle("Set Timer for " + task.getTitle())
                .setPositiveButton("Start Timer", (dialog, which) -> {
                    String durationText = durationInput.getText().toString();
                    if (!durationText.isEmpty()) {
                        try {
                            int duration = Integer.parseInt(durationText);
                            task.setDuration(duration);

                            holder.durationTextView.setText(duration + " min");
                            updateRoutineInDatabase(task);

                            cancelTimerForTask(task);
                            startTimer(task, holder, position, duration);
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Please enter a valid duration!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Please enter a valid duration!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }


    private void updateRoutineInDatabase(TaskModel routine) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference routineRef = mDatabase.child("users").child(userId)
                    .child("routines").child(routine.getDate()).child(routine.getTaskId());

            // Store the updated duration in Firebase along with other details
            routineRef.child("duration").setValue(routine.getDuration())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Task duration updated!");
                        } else {
                            Log.e("Firebase", "Failed to update duration");
                        }
                    });
        }
    }


    private void startTimer(final TaskModel task, final RoutineViewHolder holder, final int position, int duration) {
        cancelTimerForTask(task);

        long totalDurationMillis = duration * 60000;
        holder.timerCountdownTextView.setVisibility(View.VISIBLE);

        CountDownTimer timer = new CountDownTimer(totalDurationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;

                holder.timerCountdownTextView.setText(String.format("%02d:%02d", minutes, seconds));
                saveTimerState(task.getTaskId(), millisUntilFinished);
            }

            @Override
            public void onFinish() {
                task.setCompleted(true);
                applyStrikethrough(holder.titleTextView, holder.desTextView, holder.timerCountdownTextView, true, holder.durationTextView);

                updateRoutineCheckInDatabase(task);
                holder.timerCountdownTextView.setVisibility(View.GONE);

                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.timer_end_sound);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> mp.release());

                Toast.makeText(context, "Timer finished for " + task.getTitle(), Toast.LENGTH_SHORT).show();
                clearTimerState(task.getTaskId());
            }
        };

        timer.start();
        ongoingTimers.put(task.getTaskId(), timer);
    }

    private void saveTimerState(String taskId, long remainingMillis) {
        SharedPreferences.Editor editor = timerPrefs.edit();
        editor.putLong(taskId, remainingMillis);
        editor.putLong(taskId + "_timestamp", System.currentTimeMillis());
        editor.apply();
    }

    private void restoreTimerState(TaskModel task, RoutineViewHolder holder, int position) {
        long savedMillis = timerPrefs.getLong(task.getTaskId(), 0);
        long timestamp = timerPrefs.getLong(task.getTaskId() + "_timestamp", 0);

        if (savedMillis > 0 && timestamp > 0) {
            long elapsed = System.currentTimeMillis() - timestamp;
            long remaining = savedMillis - elapsed;

            if (remaining > 0) {
                startTimer(task, holder, position, (int) (remaining / 60000));
            } else {
                clearTimerState(task.getTaskId());
            }
        }
    }


    private void clearTimerState(String taskId) {
        SharedPreferences.Editor editor = timerPrefs.edit();
        editor.remove(taskId);
        editor.apply();
    }


    private void cancelTimerForTask(TaskModel task) {
        CountDownTimer existingTimer = ongoingTimers.get(task.getTaskId());
        if (existingTimer != null) {
            existingTimer.cancel();  // Cancel the previous timer
            ongoingTimers.remove(task.getTaskId());  // Remove from map
        }
    }


    private void updateRoutineCheckInDatabase(TaskModel routine) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference routineRef = mDatabase.child("users").child(userId)
                    .child("routines").child(routine.getDate()).child(routine.getTaskId());

            routineRef.child("completed").setValue(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Task marked as completed!");
                        } else {
                            Log.e("Firebase", "Failed to update routine");
                        }
                    });
        }
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


    private void applyStrikethrough(TextView textView, TextView desTextView, TextView timeTextView, boolean isCompleted, TextView duration) {
        if (isCompleted) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(Color.GRAY); // Set text color to grey
            desTextView.setTextColor(Color.GRAY); // Set text color to grey
            timeTextView.setTextColor(Color.GRAY); // Set text color to grey
            duration.setTextColor(Color.GRAY);

        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setTextColor(Color.BLACK); // Set text color to black
            desTextView.setTextColor(Color.BLACK); // Set text color to black
            timeTextView.setTextColor(Color.BLACK); // Set text color to black
            duration.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    public static class RoutineViewHolder extends RecyclerView.ViewHolder {
        public View deleteButton;
        TextView titleTextView, desTextView, TimeTextView, timerCountdownTextView, durationTextView;  // Add the new TextView for timer
        CheckBox taskCheckbox;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_title);
            desTextView = itemView.findViewById(R.id.task_des);
            TimeTextView = itemView.findViewById(R.id.task_time);
            timerCountdownTextView = itemView.findViewById(R.id.timer_countdown);  // Initialize the new TextView
            taskCheckbox = itemView.findViewById(R.id.task_checkbox);
            deleteButton = itemView.findViewById(R.id.delete_button);
            durationTextView = itemView.findViewById(R.id.task_duration);
        }
    }


    // Callback interface for task completion updates
    public interface TaskCompletionCallback {
        void onTaskCompletionChanged(TaskModel task);
        void onTaskDeleted(TaskModel task); // New method for deleting tasks
    }

    @Override
    public void onViewRecycled(@NonNull RoutineViewHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && routines != null && !routines.isEmpty()) {
            TaskModel task = routines.get(position);
            cancelTimerForTask(task);
        }
    }


}
