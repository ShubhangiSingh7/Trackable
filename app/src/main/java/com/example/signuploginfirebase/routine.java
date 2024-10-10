package com.example.signuploginfirebase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class routine extends Fragment { // Updated the class name to follow Java conventions

    private RecyclerView calendarRecyclerView;
    private TextView selectedDateTextView;
    private CalendarAdapter calendarAdapter;
    private List<DateItem> dateItemList;
    private TextView currentMonthYearTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine, container, false); // Inflate the layout

        // Initialize your views
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
        currentMonthYearTextView = view.findViewById(R.id.currentMonthYearTextView); // Initialize month and year TextView
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);

        // Get dates for the current month
        dateItemList = getDatesForCurrentMonth();

        // Set the LayoutManager programmatically
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize and set the adapter
        calendarAdapter = new CalendarAdapter(dateItemList, dateItem -> {
            selectedDateTextView.setText("Selected Date: " + dateItem.getDate() + " (" + dateItem.getDay() + ")");
        });

        calendarRecyclerView.setAdapter(calendarAdapter);

        // Display current month and year
        displayCurrentMonthAndYear();

        return view; // Return the inflated view
    }

    private void displayCurrentMonthAndYear() {
        Calendar calendar = Calendar.getInstance();
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        int year = calendar.get(Calendar.YEAR);
        currentMonthYearTextView.setText(month + " " + year);
    }

    private List<DateItem> getDatesForCurrentMonth() {
        List<DateItem> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Start at the first day of the month

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        Calendar today = Calendar.getInstance(); // Get the current date
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int currentMonth = today.get(Calendar.MONTH);
        int currentYear = today.get(Calendar.YEAR);

        for (int day = 1; day <= maxDays; day++) {
            String date = String.valueOf(day);
            String dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]; // Get day of week

            // Check if this date is the current date
            boolean isCurrentDate = (day == currentDay && currentMonth == calendar.get(Calendar.MONTH) &&
                    currentYear == calendar.get(Calendar.YEAR));

            dates.add(new DateItem(date, dayOfWeek, isCurrentDate));
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }

        return dates;
    }
}
