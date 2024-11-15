package com.example.signuploginfirebase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home extends Fragment {

    private TextView usernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the username TextView
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
        return view;
    }

}
