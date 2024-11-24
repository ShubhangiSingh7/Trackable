package com.example.signuploginfirebase;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exerciseList;

    // Constructor to initialize the exercise list
    public ExerciseAdapter(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    // onCreateViewHolder should inflate the layout for each item in the RecyclerView
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_exercise layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    // onBindViewHolder binds the data to the views in each item
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        // Null check for exerciseList to prevent crashes
        if (exerciseList != null && position < exerciseList.size()) {
            Exercise exercise = exerciseList.get(position);

            // Set exercise data to the views in the ViewHolder
            holder.exerciseNameTextView.setText(exercise.getName());
            holder.exerciseTypeTextView.setText(exercise.getType());
            // Bind muscles to TextView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                if (exercise.getMuscles() != null && !exercise.getMuscles().isEmpty()) {
                    StringBuilder musclesString = new StringBuilder();
                    for (String muscle : exercise.getMuscles()) {
                        musclesString.append(muscle).append(", ");
                    }
                    // Remove the last comma and space
                    if (!musclesString.isEmpty()) {
                        musclesString.setLength(musclesString.length() - 2);
                    }
                    holder.exerciseMusclesTextView.setText(musclesString.toString());
                } else {
                    holder.exerciseMusclesTextView.setText("No muscles listed");
                }
            }
            holder.exerciseEquipmentTextView.setText(exercise.getEquipment());
            holder.exerciseDifficultyTextView.setText(exercise.getDifficulty());
            // Bind instructions to TextView
            if (exercise.getInstructions() != null) {
                holder.exerciseInstructionTextView.setText(exercise.getInstructions());
            } else {
                holder.exerciseInstructionTextView.setText("No instructions available");
            }
        }
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return (exerciseList != null) ? exerciseList.size() : 0;
    }

    // Update exercises data
    public void updateExercises(List<Exercise> exercises) {
        // Check if the new list is not null and assign it
        if (exercises != null) {
            this.exerciseList = exercises;
            notifyDataSetChanged();  // Notify the adapter that the data has changed and needs to be refreshed
        }
    }

    // ViewHolder class to hold references to the views in the layout for each item
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseInstructionTextView;
        TextView exerciseNameTextView;
        TextView exerciseTypeTextView;
        TextView exerciseMusclesTextView;
        TextView exerciseEquipmentTextView;
        TextView exerciseDifficultyTextView;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exercise_name);
            exerciseTypeTextView = itemView.findViewById(R.id.exercise_type);
            exerciseMusclesTextView = itemView.findViewById(R.id.exercise_muscles);
            exerciseEquipmentTextView = itemView.findViewById(R.id.exercise_equipment);
            exerciseDifficultyTextView = itemView.findViewById(R.id.exercise_difficulty);
            exerciseInstructionTextView = itemView.findViewById(R.id.exercise_instruction);
        }
    }
}
