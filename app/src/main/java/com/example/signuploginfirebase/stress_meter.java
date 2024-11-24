package com.example.signuploginfirebase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class stress_meter extends Fragment {
    private SeekBar stressSeekBar;
    private TextView currentStressLevel, stressDescription;
    private ImageView emojiHappy, emojiNeutral, emojiSad;
    private int stressLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stress_meter, container, false);

        // Initialize views after inflating the layout
        stressSeekBar = view.findViewById(R.id.stressSeekBar);
        currentStressLevel = view.findViewById(R.id.currentStressLevel);
        stressDescription = view.findViewById(R.id.stressDescription);
        emojiHappy = view.findViewById(R.id.emojiHappy);
        emojiNeutral = view.findViewById(R.id.emojiNeutral);
        emojiSad = view.findViewById(R.id.emojiSad);

        // Set initial values
        stressLevel = stressSeekBar.getProgress();
        updateStressLevel(stressLevel);

        // Set SeekBar listener
        stressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stressLevel = progress;
                updateStressLevel(stressLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Animate or highlight the scale when user starts dragging
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Perform an action after dragging stops
            }
        });

        return view;  // Return the inflated view at the end
    }

    private void updateStressLevel(int level) {
        // Update emoji and text dynamically based on stress level
        if (level >= 70) {
            currentStressLevel.setText("Stressed 😟");
            stressDescription.setText("It seems like you're stressed. Try relaxation!");
            emojiHappy.setVisibility(View.GONE);
            emojiNeutral.setVisibility(View.GONE);
            emojiSad.setVisibility(View.VISIBLE);
        } else if (level >= 40) {
            currentStressLevel.setText("Okay 😐");
            stressDescription.setText("You're doing fine. Take a deep breath.");
            emojiHappy.setVisibility(View.GONE);
            emojiSad.setVisibility(View.GONE);
            emojiNeutral.setVisibility(View.VISIBLE);
        } else {
            currentStressLevel.setText("Relaxed 😊");
            stressDescription.setText("You're feeling great! Keep it up.");
            emojiNeutral.setVisibility(View.GONE);
            emojiSad.setVisibility(View.GONE);
            emojiHappy.setVisibility(View.VISIBLE);
        }
    }
}
