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

import com.example.signuploginfirebase.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    TextView userName;
    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;
    ActivityMainBinding binding ;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;



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
            }, 1);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.routine) {
                replaceFragment(new routine());
                return true;
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new profile());
                return true;
            } else if (item.getItemId() == R.id.settings) {
                replaceFragment(new settings());
                return true;
            } else if (item.getItemId() == R.id.add) {
                showAddTaskDialog();
            } else if (item.getItemId() == R.id.home) {
                replaceFragment(new home());
                return true;
            }
            return false; // If none of the items match
        });

        /*userName = findViewById(R.id.userName);*/

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null){
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

    private void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
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

        // Set Date Picker listener
        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        selectDateButton.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Set Time Picker listener
        selectTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        String selectedTime = hourOfDay + ":" + String.format("%02d", minute);
                        selectTimeButton.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Set Repeat Toggle Button (optional)
        repeatToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                repeatToggle.setTextOn("Weekly");
            } else {
                repeatToggle.setTextOff("No Repeat");
            }
        });

        // Save Task button functionality
        saveTaskButton.setOnClickListener(v -> {
            String title = taskTitle.getText().toString();
            String description = taskDescription.getText().toString();
            String date = selectDateButton.getText().toString();
            String time = selectTimeButton.getText().toString();
            boolean repeatWeekly = repeatToggle.isChecked();

            if (!title.isEmpty() && !description.isEmpty() && !date.equals("Select Date") && !time.equals("Select Time")) {
                // Save task to Firebase (call your Firebase method here)
                addTaskToFirebase(title, description, date, time, repeatWeekly);
            } else {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addTaskToFirebase(String title, String description, String date, String time, boolean repeatWeekly) {
        FirebaseUser user = mAuth.getCurrentUser(); // Get the currently signed-in user

        if (user != null) {
            String userId = user.getUid(); // Securely fetch the user ID

            // Generate a unique task ID
            String taskId = mDatabase.child("users").child(userId).child("routines").push().getKey();

            // Create a task model object
            TaskModel newTask = new TaskModel(title, description, date, time, repeatWeekly);

            // Add the task to the user's routines in Firebase
            mDatabase.child("users").child(userId).child("routines").child(taskId)
                    .setValue(newTask)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                            // Clear the fields after successfully adding the task
                            /*taskTitle.setText(""); // Clear the task title
                            taskDescription.setText(""); // Clear the task description
                            selectDateButton.setText("Select Date"); // Reset the date button
                            selectTimeButton.setText("Select Time"); // Reset the time button
                            repeatToggle.setChecked(false); // Reset the toggle button*/

                        } else {
                            Toast.makeText(MainActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(MainActivity.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

}

