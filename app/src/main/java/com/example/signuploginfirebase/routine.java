package com.example.signuploginfirebase;

import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class routine extends Fragment implements RoutinesAdapter.TaskCompletionCallback {

    private MaterialCalendarView calendarView;
    private TextView selectedDateTextView;
    private TextView currentMonthYearTextView;
    private RoutinesAdapter routinesAdapter;
    private List<TaskModel> routines = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        calendarView = view.findViewById(R.id.calendarView);
        RecyclerView routinesRecyclerView = view.findViewById(R.id.routinesRecyclerView);
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
        currentMonthYearTextView=view.findViewById(R.id.currentMonthYearTextView);

        // Set up RecyclerView for displaying routines
        routinesAdapter = new RoutinesAdapter(routines, this,getContext());
        routinesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        routinesRecyclerView.setAdapter(routinesAdapter);

        // Highlight today's date and set it as selected
        highlightTodayAndSetDefaultDate();

        // Load routines and decorate dates
        //loadRoutineDatesWithLines();

        // Set the current month and year at startup
        updateMonthYear(calendarView.getCurrentDate());

        // Add listener to detect month changes
        calendarView.setOnMonthChangedListener((widget, date) -> updateMonthYear(date));

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String day = String.format("%02d", date.getDay());
            String month = String.format("%02d", date.getMonth() + 1); // Month is zero-based
            String selectedDate = day + "/" + month + "/" + date.getYear();
            selectedDateTextView.setText(selectedDate);
            loadRoutinesForDate(selectedDate);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the adapter here to ensure getContext() is not null
        if (getContext() != null) {
            routinesAdapter = new RoutinesAdapter(routines, this, getContext());
            RecyclerView routinesRecyclerView = view.findViewById(R.id.routinesRecyclerView);
            routinesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            routinesRecyclerView.setAdapter(routinesAdapter);
        } else {
            // Handle the case where context is null (optional)
            Toast.makeText(getActivity(), "Context is unavailable", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateMonthYear(CalendarDay date) {
        // Get the full month name
        String monthName = new DateFormatSymbols().getMonths()[date.getMonth()];
        // Combine month name and year
        String monthYear = monthName + " " + date.getYear();
        // Set it to the TextView
        currentMonthYearTextView.setText(monthYear);
    }

    private void highlightTodayAndSetDefaultDate() {
        CalendarDay today = CalendarDay.today();

        // Set today's date as selected in the calendar
        calendarView.setSelectedDate(today);

        // Format today's date with leading zeros if necessary
        String day = String.format("%02d", today.getDay());
        String month = String.format("%02d", today.getMonth() + 1);  // Month is zero-based
        String selectedDate = day + "/" + month + "/" + today.getYear();
        selectedDateTextView.setText(selectedDate);

        // Optionally, load routines for today's date
        loadRoutinesForDate(selectedDate);

        // Highlight today's date with custom styling (optional)
        DayViewDecorator decorator = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.equals(today);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorAccent)));
            }
        };

        calendarView.addDecorator(decorator);
    }

    private void loadRoutinesForDate(String selectedDate) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference routinesRef = mDatabase.child("users").child(userId).child("routines").child(selectedDate);

            // Clear any existing routines before loading new data
            routines.clear();

            // Fetch routines from Firebase
            routinesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    if (result != null && result.exists()) {
                        Log.d("loadRoutines", "Routines found: " + result.getChildrenCount());
                        // Process each routine in the snapshot
                        for (DataSnapshot snapshot : result.getChildren()) {
                            TaskModel routine = snapshot.getValue(TaskModel.class);
                            if (routine != null) {
                                routines.add(routine);
                            }
                        }
                        // Notify the adapter that data has been updated
                        routinesAdapter.notifyDataSetChanged();

                        // Show message if no routines are available
                        showNoRoutinesMessage(false);
                    } else {
                        Log.d("loadRoutines", "No routines for this date: " + selectedDate);
                        showNoRoutinesMessage(true);
                    }
                } else {
                    // Handle the case when fetching data failed
                    Log.e("loadRoutines", "Failed to get data: " + task.getException());
                    Toast.makeText(getActivity(), "Failed to load routines", Toast.LENGTH_SHORT).show();
                    showNoRoutinesMessage(true);
                }
            }).addOnFailureListener(e -> {
                // Handle any failure during the data fetch operation
                Log.e("loadRoutines", "Error: ", e);
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                showNoRoutinesMessage(true);
            });
        } else {
            // Handle the case when the user is not logged in
            Toast.makeText(getActivity(), "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void showNoRoutinesMessage(boolean show) {
        // Make sure that the view is not null before accessing it
        if (getView() != null) {
            TextView noRoutinesTextView = getView().findViewById(R.id.noRoutinesMessage);
            if (noRoutinesTextView != null) {
                if (show) {
                    noRoutinesTextView.setVisibility(View.VISIBLE);
                } else {
                    noRoutinesTextView.setVisibility(View.GONE);
                }
            }
        }
    }


    @Override
    public void onTaskCompletionChanged(TaskModel task) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference taskRef = mDatabase.child("users").child(userId)
                    .child("routines").child(task.getDate()).child(task.getTaskId());

            taskRef.child("completed").setValue(task.isCompleted())
                    .addOnCompleteListener(updateTask -> {
                        if (!updateTask.isSuccessful()) {
                            Toast.makeText(getActivity(), "Failed to update task completion", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onTaskDeleted(TaskModel task) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference taskRef = mDatabase.child("users").child(userId)
                    .child("routines").child(task.getDate()).child(task.getTaskId());

            taskRef.removeValue()
                    .addOnCompleteListener(taskDelete -> {
                        if (taskDelete.isSuccessful()) {
                            // Show success message
                            Toast.makeText(getActivity(), "Task deleted successfully", Toast.LENGTH_SHORT).show();

                            // Remove the task from the list in the adapter (if you have access to it)
                            if (getActivity() != null && routinesAdapter != null) {
                                // Assuming 'routines' is the list in your adapter
                                routines.remove(task);  // Remove the task from the list
                                routinesAdapter.notifyDataSetChanged();  // Notify the adapter to update the RecyclerView
                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed to delete task", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

