package com.example.signuploginfirebase;

import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class routine extends Fragment implements RoutinesAdapter.TaskCompletionCallback {

    private MaterialCalendarView calendarView;
    private TextView selectedDateTextView;
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

        // Set up RecyclerView for displaying routines
        routinesAdapter = new RoutinesAdapter(routines, this);
        routinesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        routinesRecyclerView.setAdapter(routinesAdapter);

        // Highlight today's date
        highlightToday();

        // Load routines and decorate dates
        //loadRoutineDatesWithLines();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
            selectedDateTextView.setText(selectedDate);
            loadRoutinesForDate(selectedDate);
        });

        return view;
    }

    private void highlightToday() {
        CalendarDay today = CalendarDay.today();

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

            routines.clear();
            routinesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    if (result != null && result.exists()) {
                        for (DataSnapshot snapshot : result.getChildren()) {
                            TaskModel routine = snapshot.getValue(TaskModel.class);
                            if (routine != null) {
                                routines.add(routine);
                            }
                        }
                        routinesAdapter.notifyDataSetChanged();
                        showNoRoutinesMessage(false);
                    } else {
                        showNoRoutinesMessage(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to load routines", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showNoRoutinesMessage(boolean show) {
        TextView noRoutinesMessage = getView().findViewById(R.id.noRoutinesMessage);
        if (show) {
            noRoutinesMessage.setVisibility(View.VISIBLE);
        } else {
            noRoutinesMessage.setVisibility(View.GONE);
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
                            Toast.makeText(getActivity(), "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to delete task", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

