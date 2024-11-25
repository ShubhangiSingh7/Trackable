package com.example.signuploginfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NutritionAdapter extends RecyclerView.Adapter<NutritionAdapter.NutritionViewHolder> {

    private List<Nutrition> nutritionList;

    // Constructor
    public NutritionAdapter(List<Nutrition> nutritionList) {
        this.nutritionList = nutritionList;
    }

    @NonNull
    @Override
    public NutritionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the CardView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutrition, parent, false);
        return new NutritionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NutritionViewHolder holder, int position) {
        // Get the current Nutrition item
        Nutrition nutrition = nutritionList.get(position);

        // Bind the data to the TextViews
        holder.name.setText(nutrition.getName());
        holder.fatTotal.setText(String.format("%s g", nutrition.getFat_total_g()));
        holder.cholesterol.setText(String.format("%s mg", nutrition.getCholesterol_mg()));
        holder.carbs.setText(String.format("%s g", nutrition.getCarbohydrates_total_g()));
        holder.fiber.setText(String.format("%s g", nutrition.getFiber_g()));
        holder.sugar.setText(String.format("%s g", nutrition.getSugar_g()));
    }

    @Override
    public int getItemCount() {
        // Return the size of the list
        return nutritionList.size();
    }

    // Update the nutrition list dynamically
    public void updateNutritionList(List<Nutrition> newNutritionList) {
        this.nutritionList.clear();
        this.nutritionList.addAll(newNutritionList);
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class NutritionViewHolder extends RecyclerView.ViewHolder {
        TextView name, fatTotal, cholesterol, carbs, fiber, sugar;

        public NutritionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            name = itemView.findViewById(R.id.name);
            fatTotal = itemView.findViewById(R.id.fat_total_g);
            cholesterol = itemView.findViewById(R.id.cholesterol_mg);
            carbs = itemView.findViewById(R.id.carbohydrates_total_g);
            fiber = itemView.findViewById(R.id.fiber_g);
            sugar = itemView.findViewById(R.id.sugar_g);
        }
    }
}
