package com.example.signuploginfirebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settings extends Fragment { // Updated class name to follow Java naming conventions

    private GoogleSignInClient gClient; // Google Sign-In client
    private ToggleButton toggleButton;
    private TextView usernameTextView;  // TextView to display username

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the logout button
        Button logout = view.findViewById(R.id.logout); // Ensure this ID matches your layout
        usernameTextView = view.findViewById(R.id.userName); // Ensure the ID matches the TextView in your layout

        // Retrieve and display the current user's username
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName(); // Get the username (display name)
            if (displayName != null) {
                usernameTextView.setText(displayName); // Set username in TextView
            } else {
                usernameTextView.setText("No Username Set"); // Handle case if no username
            }
        }

        // Initialize Google Sign-In options and client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // Request additional options as necessary
                .build();
        gClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Set OnClickListener for the logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Access SharedPreferences
                SharedPreferences preferences = requireActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("rememberMe", false);
                editor.apply();

                // Log out from Google Sign-In
                gClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Start the LoginActivity after signing out
                            Intent intent = new Intent(requireActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish(); // Finish the current activity
                        } else {
                            // Handle sign-out failure (optional)
                            Log.e("SettingsFragment", "Sign-out failed");
                        }
                    }
                });
            }
        });

        toggleButton = view.findViewById(R.id.toggleButton);

        // Set an OnCheckedChangeListener to handle the toggle button's state change
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Code to execute when toggle button is ON
                    toggleButton.setText("ON");
                } else {
                    // Code to execute when toggle button is OFF
                    toggleButton.setText("OFF");
                }
            }
        });

        return view; // Return the inflated view
    }
}
