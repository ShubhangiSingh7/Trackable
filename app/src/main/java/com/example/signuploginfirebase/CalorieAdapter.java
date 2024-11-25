package com.example.signuploginfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalorieAdapter extends RecyclerView.Adapter<CalorieAdapter.CalorieViewHolder> {

    private List<Calorie> calorieList;

    // Constructor to initialize the list
    public CalorieAdapter(List<Calorie> calorieList) {
        this.calorieList = calorieList;
    }

    @NonNull
    @Override
    public CalorieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calorie, parent, false);
        return new CalorieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalorieViewHolder holder, int position) {
        // Get the Calorie object for the current position
        Calorie calorie = calorieList.get(position);

        // Bind data to the views
        holder.activity_name.setText(calorie.getName());
        holder.caloriesPerHour.setText(""+calorie.getCalories_per_hour());
        holder.durationMinutes.setText(""+calorie.getDuration_minutes());
        holder.totalCalories.setText(""+calorie.getTotal_calories());
    }

    @Override
    public int getItemCount() {
        // Return the total number of items
        return calorieList.size();
    }

    // Method to update the calorie list dynamically
    public void updateCalories(List<Calorie> newCalories) {
        this.calorieList.clear();
        this.calorieList.addAll(newCalories);
        notifyDataSetChanged();
    }

    // ViewHolder class to hold individual item views
    public static class CalorieViewHolder extends RecyclerView.ViewHolder {
        TextView activity_name, caloriesPerHour, durationMinutes, totalCalories;

        public CalorieViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the TextViews
            activity_name = itemView.findViewById(R.id.activity_name);
            caloriesPerHour = itemView.findViewById(R.id.calories_per_hour);
            durationMinutes = itemView.findViewById(R.id.duration_minutes);
            totalCalories = itemView.findViewById(R.id.total_calories);
        }
    }
}

