package com.example.signuploginfirebase;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.signuploginfirebase.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView userName;
    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;
    ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView routinesRecyclerView;
    private List<TaskModel> routines = new ArrayList<>();
    private RoutinesAdapter routinesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        if (savedInstanceState == null) {
            new Handler().postDelayed(() -> {
                replaceFragment(new home()); // Open ProfileFragment after milliseconds
            }, 5);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.routine) {
                replaceFragment(new routine());
                return true;
            } else if (item.getItemId() == R.id.settings) {
                replaceFragment(new settings());
                return true;
            } else if (item.getItemId() == R.id.add) {
                showAddTaskDialog();
            } else if (item.getItemId() == R.id.home) {
                replaceFragment(new home());
                return true;
            } else if (item.getItemId() == R.id.meter) {
                replaceFragment(new stress_meter());
                return true;
            }
            return false; // If none of the items match
        });

        //userName = findViewById(R.id.userName);

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null) {
            String gName = gAccount.getDisplayName();
            userName.setText(gName);
        }

        // Initialize Firebase Auth (use default instance)
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to login screen or prompt user to sign in
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // User is signed in
            String userId = currentUser.getUid();
            // Proceed with calling Firebase methods
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);  // Add to back stack
        fragmentTransaction.commit();
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add, null);
        builder.setView(dialogView);

        // Initialize UI elements
        EditText taskTitle = dialogView.findViewById(R.id.task_title);
        EditText taskDescription = dialogView.findViewById(R.id.task_description);
        Button selectDateButton = dialogView.findViewById(R.id.select_date_button);
        Button selectTimeButton = dialogView.findViewById(R.id.select_time_button);
        ToggleButton repeatToggle = dialogView.findViewById(R.id.repeat_toggle);
        Button saveTaskButton = dialogView.findViewById(R.id.save_task_button);

        // Get today's date in the same format for comparison
        Calendar today = Calendar.getInstance();
        final int currentYear = today.get(Calendar.YEAR);
        final int currentMonth = today.get(Calendar.MONTH);
        final int currentDay = today.get(Calendar.DAY_OF_MONTH);

        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        // Format day and month to have leading zero if needed
                        String formattedDay = String.format("%02d", dayOfMonth);
                        String formattedMonth = String.format("%02d", month + 1); // month + 1 because Calendar month is 0-indexed
                        String selectedDate = formattedDay + "/" + formattedMonth + "/" + year;
                        selectDateButton.setText(selectedDate);

                        // Check if the selected date is before today
                        if (year < currentYear || (year == currentYear && month < currentMonth) || (year == currentYear && month == currentMonth && dayOfMonth < currentDay)) {
                            // Show a message if the selected date is in the past
                            Toast.makeText(MainActivity.this, "You cannot select a past date", Toast.LENGTH_SHORT).show();
                        }
                    },
                    currentYear, currentMonth, currentDay);

            // Disable past dates in the date picker
            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
            datePickerDialog.show();
        });

        // Set Time Picker listener with AM/PM option
        selectTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        // Convert 24-hour format to 12-hour format with AM/PM
                        boolean isPM = (hourOfDay >= 12);
                        int displayHour = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                        String amPm = isPM ? "PM" : "AM";
                        String selectedTime = String.format("%02d:%02d %s", displayHour, minute, amPm);
                        selectTimeButton.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);  // 'false' for 12-hour format
            timePickerDialog.show();
        });

        // Set Repeat Toggle Button
        repeatToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            repeatToggle.setText(isChecked ? "Weekly" : "No Repeat");
        });

        // Initialize and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        saveTaskButton.setOnClickListener(v -> {
            // Get input values from UI components
            String title = taskTitle.getText().toString().trim();
            String description = taskDescription.getText().toString().trim();
            String date = selectDateButton.getText().toString().trim();
            String time = selectTimeButton.getText().toString().trim();
            boolean repeatWeekly = repeatToggle.isChecked();

            // Check if the required fields are filled
            if (!title.isEmpty() && !description.isEmpty() && !date.isEmpty() && !date.equals("Select Date") && !time.isEmpty() && !time.equals("Select Time")) {
                // Check if the selected date is before today's date
                String[] dateParts = date.split("/");
                int selectedDay = Integer.parseInt(dateParts[0]);
                int selectedMonth = Integer.parseInt(dateParts[1]) - 1;  // month is 0-indexed
                int selectedYear = Integer.parseInt(dateParts[2]);

                if (selectedYear < currentYear || (selectedYear == currentYear && selectedMonth < currentMonth) || (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay < currentDay)) {
                    // Show an error message if the date is in the past
                    Toast.makeText(MainActivity.this, "You cannot select a past date", Toast.LENGTH_SHORT).show();
                } else {
                    // Add task to Firebase
                    addTaskToFirebase(title, description, date, time, repeatWeekly);
                    dialog.dismiss();  // Dismiss the dialog after saving
                }
            } else {
                // Show error message if any field is empty or has a placeholder value
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTaskToFirebase(String title, String description, String date, String time, boolean repeatWeekly) {
        // Get the user ID from FirebaseAuth and store task in the database
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String taskId = mDatabase.child("users").child(userId).child("routines").push().getKey();
            boolean isCompleted = false;
            TaskModel newTask = new TaskModel(title, description, date, time, repeatWeekly, isCompleted, taskId, 0);
            if (taskId != null) {
                mDatabase.child("users").child(userId).child("routines").child(date).push().setValue(newTask)
                        .addOnSuccessListener(aVoid -> {
                            // Task added successfully
                            Toast.makeText(MainActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Failed to add task
                            Toast.makeText(MainActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }
}
