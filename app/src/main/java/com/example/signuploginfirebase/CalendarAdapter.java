package com.example.signuploginfirebase;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private List<DateItem> dateItems;
    private OnDateClickListener onDateClickListener;
    private int selectedPosition = -1; // Track selected position

    public interface OnDateClickListener {
        void onDateClick(DateItem dateItem);
    }

    public CalendarAdapter(List<DateItem> dateItems, OnDateClickListener listener) {
        this.dateItems = dateItems;
        this.onDateClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateItem dateItem = dateItems.get(position);
        holder.dateTextView.setText(dateItem.getDate());
        holder.dayTextView.setText(dateItem.getDay());

        // Highlight the current date
        if (dateItem.isCurrentDate()) {
            holder.itemView.setBackgroundColor(Color.CYAN); // Change color to highlight
            holder.dateTextView.setTextColor(Color.BLACK); // Optional: change text color for visibility
        } else if (selectedPosition == position) { // Highlight selected position
            holder.itemView.setBackgroundColor(Color.LTGRAY); // Change color for selected item
            holder.dateTextView.setTextColor(Color.BLACK); // Optional: change text color for visibility
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Default color
            holder.dateTextView.setTextColor(Color.GRAY); // Optional: default text color
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position; // Update selected position
            onDateClickListener.onDateClick(dateItem); // Call the listener
            notifyDataSetChanged(); // Refresh the adapter to highlight the new selected item
        });
    }

    @Override
    public int getItemCount() {
        return dateItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView dayTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}
