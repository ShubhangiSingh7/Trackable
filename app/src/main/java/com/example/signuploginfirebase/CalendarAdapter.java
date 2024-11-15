package com.example.signuploginfirebase;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<DateItem> dateItemList;
    private OnDateClickListener dateClickListener;

    public CalendarAdapter(List<DateItem> dateItemList, OnDateClickListener dateClickListener) {
        this.dateItemList = dateItemList;
        this.dateClickListener = dateClickListener;
    }


    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        DateItem dateItem = dateItemList.get(position);
        holder.dateTextView.setText(dateItem.getDate());
        holder.dayTextView.setText(dateItem.getDay());

        if (dateItem.isToday()) {
            holder.dateTextView.setTextColor(Color.RED); // Highlight today's date in red
        } else {
            holder.dateTextView.setTextColor(Color.BLACK);
        }

        if (dateItem.isSelected()) {
            holder.itemView.setBackgroundColor(Color.LTGRAY); // Highlight selected date
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Set the click listener
        holder.itemView.setOnClickListener(v -> dateClickListener.onDateClick(dateItem));

    }

    @Override
    public int getItemCount() {
        return dateItemList.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView dayTextView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView); // Assuming you have dateTextView in your item layout
            dayTextView = itemView.findViewById(R.id.dayTextView); // Assuming you have dayTextView in your item layout
        }
    }

    public interface OnDateClickListener {
        void onDateClick(DateItem dateItem);
    }
}
