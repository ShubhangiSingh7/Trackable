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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settings extends Fragment {

    private GoogleSignInClient gClient;
    private ToggleButton toggleButton;
    private TextView usernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the logout button
        Button logout = view.findViewById(R.id.logout);
        usernameTextView = view.findViewById(R.id.userName);

        // Retrieve and display the current user's username
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            usernameTextView.setText(displayName != null ? displayName : "No Username Set");
        }

        // Initialize Google Sign-In options and client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Set OnClickListener for the logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = requireActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("rememberMe", false);
                editor.apply();

                gClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(requireActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            Log.e("SettingsFragment", "Sign-out failed");
                        }
                    }
                });
            }
        });

        // Initialize the theme toggle button
        toggleButton = view.findViewById(R.id.toggleButton);

        // Check the current theme preference and set the toggle button state
        SharedPreferences preferences = requireActivity().getSharedPreferences("theme_pref", Context.MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        toggleButton.setChecked(isDarkMode);

        // Set an OnCheckedChangeListener to handle the toggle button's state change
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("dark_mode", isChecked);
                editor.apply();

                // Apply the theme dynamically
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                // Optional: Recreate the activity to apply changes immediately
                requireActivity().recreate();
            }
        });

        return view;
    }
}
