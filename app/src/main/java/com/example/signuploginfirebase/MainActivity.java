package com.example.signuploginfirebase;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

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
import java.util.concurrent.TimeUnit;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "routine_notifications",
                    "Routine Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Routine Notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


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

        // Get today's date for comparison
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
                        String formattedMonth = String.format("%02d", month + 1);
                        String selectedDate = formattedDay + "/" + formattedMonth + "/" + year;
                        selectDateButton.setText(selectedDate);

                        // Check if the selected date is before today
                        if (year < currentYear || (year == currentYear && month < currentMonth) || (year == currentYear && month == currentMonth && dayOfMonth < currentDay)) {
                            Toast.makeText(MainActivity.this, "You cannot select a past date", Toast.LENGTH_SHORT).show();
                        }
                    },
                    currentYear, currentMonth, currentDay);

            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
            datePickerDialog.show();
        });

        selectTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        // Convert the hour from 24-hour format to 12-hour format
                        int displayHour = (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12;  // Handle 12-hour format (1-12)
                        String amPm = (hourOfDay >= 12) ? "PM" : "AM";  // Set AM/PM based on hourOfDay

                        // Format the time correctly
                        String selectedTime = String.format("%02d:%02d %s", displayHour, minute, amPm);
                        selectTimeButton.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });

        saveTaskButton.setOnClickListener(v -> {
            String title = taskTitle.getText().toString().trim();
            String description = taskDescription.getText().toString().trim();
            String date = selectDateButton.getText().toString().trim();
            String time = selectTimeButton.getText().toString().trim();
            boolean repeatWeekly = repeatToggle.isChecked();

            // Validate user inputs
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in the title and description", Toast.LENGTH_SHORT).show();
                return;
            }

            if (date.equals("Select Date")) {
                Toast.makeText(MainActivity.this, "Please select a valid date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (time.equals("Select Time")) {
                Toast.makeText(MainActivity.this, "Please select a valid time", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Parse date
                String[] dateParts = date.split("/");
                int selectedDay = Integer.parseInt(dateParts[0]);
                int selectedMonth = Integer.parseInt(dateParts[1]) - 1; // month is 0-indexed
                int selectedYear = Integer.parseInt(dateParts[2]);

                // Parse time
                String[] timeParts = time.split(":");
                int selectedHour = Integer.parseInt(timeParts[0]);
                int selectedMinute = Integer.parseInt(timeParts[1].split(" ")[0]);
                String amPm = timeParts[1].split(" ")[1];

                // Convert to 24-hour format
                if (amPm.equals("PM") && selectedHour < 12) {
                    selectedHour += 12;
                } else if (amPm.equals("AM") && selectedHour == 12) {
                    selectedHour = 0;
                }

                // Build calendar object
                Calendar calendar = Calendar.getInstance();
                calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0);

                // Schedule notification and save task
                long triggerTimeInMillis = calendar.getTimeInMillis() - (5 * 60 * 1000); // 5 minutes before
                addTaskToFirebase(title, description, date, time, repeatWeekly);
                scheduleNotification(MainActivity.this, title, triggerTimeInMillis);

            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid date or time format", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void scheduleNotification(Context context, String routineName, long triggerTimeInMillis) {
        // Subtract 5 minutes (in milliseconds) from the trigger time
        long adjustedTriggerTime = triggerTimeInMillis - (5 * 60 * 1000);
        long delayInMillis = adjustedTriggerTime - System.currentTimeMillis(); // Calculate delay


        // Build the data to pass to the Worker
        Data inputData = new Data.Builder()
                .putString("routine_name", routineName)
                .build();

        // Create the WorkRequest with the calculated delay and input data
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData) // Pass the input data
                .build();

        // Enqueue the work
        WorkManager.getInstance(context).enqueue(workRequest);

    }




    private void addTaskToFirebase(String title, String description, String date, String time, boolean repeatWeekly) {
        // Get the current user from FirebaseAuth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get the user's unique ID
            String userId = currentUser.getUid();

            // Generate a unique task ID
            String taskId = mDatabase.child("users").child(userId).child("routines").push().getKey();

            if (taskId != null) {
                // Create a new TaskModel object
                TaskModel newTask = new TaskModel(title, description, date, time, repeatWeekly, false, taskId, 0);

                // Reference to the "routines" node in Firebase, grouped by date
                DatabaseReference taskRef = mDatabase.child("users").child(userId).child("routines").child(date).child(taskId);

                // Save the task to Firebase
                taskRef.setValue(newTask)
                        .addOnSuccessListener(aVoid -> {
                            // Notify the user of success
                            Toast.makeText(MainActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Notify the user of failure
                            Toast.makeText(MainActivity.this, "Failed to add task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Handle the case where the task ID could not be generated
                Toast.makeText(MainActivity.this, "Failed to generate task ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where the user is not authenticated
            Toast.makeText(MainActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
