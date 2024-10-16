package com.example.signuploginfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoutinesAdapter extends RecyclerView.Adapter<RoutinesAdapter.RoutineViewHolder> {

    private List<TaskModel> routines; // List of routines to display

    // Constructor to initialize the list
    public RoutinesAdapter(List<TaskModel> routines) {
        this.routines = routines;
    }

    // ViewHolder class to hold each item view
    public static class RoutineViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView dateTextView;
        public TextView timeTextView;

        public RoutineViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView); // Replace with your actual TextView IDs
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false); // Inflate your item layout
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        TaskModel routine = routines.get(position);

        // Bind the data to the views
        holder.titleTextView.setText(routine.getTitle());
        holder.descriptionTextView.setText(routine.getDescription());
        holder.dateTextView.setText(routine.getDate());
        holder.timeTextView.setText(routine.getTime());
    }

    @Override
    public int getItemCount() {
        return routines.size(); // Return the size of the list
    }
}
