package com.example.signuploginfirebase;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class home extends Fragment {

    private TextView usernameTextView;
    private ImageView imageView1, imageView2, imageView3, imageView4;
    private CardView cardView1, cardView2, cardView3, cardView4;
    private Retrofit retrofit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        retrofit = RetrofitClient.getRetrofitInstance();
        ExerciseApiService exerciseApiService = retrofit.create(ExerciseApiService.class);

        usernameTextView = view.findViewById(R.id.userName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null) {
                usernameTextView.setText(displayName);
            } else {
                usernameTextView.setText("No Username Set");
            }
        }

        cardView1 = view.findViewById(R.id.exercise);
        cardView2 = view.findViewById(R.id.nutrition);
        cardView3 = view.findViewById(R.id.calorie);
        cardView4 = view.findViewById(R.id.progress);

        cardView1.setOnClickListener(v -> {
            v.setTranslationZ(20f);
            v.postDelayed(() -> v.setTranslationZ(5f), 100);
            showDialog1();
        });

        cardView2.setOnClickListener(v -> {
            v.setTranslationZ(20f);
            v.postDelayed(() -> v.setTranslationZ(5f), 100);
            showDialog2();
        });

        cardView3.setOnClickListener(v -> {
            v.setTranslationZ(20f);
            v.postDelayed(() -> v.setTranslationZ(5f), 100);
            showDialog3();
        });

        cardView4.setOnClickListener(v -> {
            v.setTranslationZ(20f);
            Toast.makeText(requireActivity(), "Card clicked!", Toast.LENGTH_SHORT).show();
            v.postDelayed(() -> v.setTranslationZ(5f), 100);
        });

        imageView1 = view.findViewById(R.id.image_exercise);
        imageView2 = view.findViewById(R.id.image_nutrition);
        imageView3 = view.findViewById(R.id.image_calorie);
        imageView4 = view.findViewById(R.id.image_progress);
        imageView1.animate().alpha(0.8f).setDuration(1200).start();
        imageView2.animate().alpha(0.8f).setDuration(1200).start();
        imageView3.animate().alpha(0.8f).setDuration(1200).start();
        imageView4.animate().alpha(0.8f).setDuration(1200).start();

        return view;
    }

    private void showDialog3() {
        LayoutInflater inflater1 = getLayoutInflater();
        View dialogView = inflater1.inflate(R.layout.calorie_box, null);

        EditText ActivityEditText = dialogView.findViewById(R.id.activity_name);
        EditText WeigthEditText = dialogView.findViewById(R.id.activity_weigth);
        EditText DurationEditText = dialogView.findViewById(R.id.activity_duration);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the CalorieAdapter
        List<Calorie> caloriesList = new ArrayList<>();
        CalorieAdapter calorieAdapter = new CalorieAdapter(caloriesList);
        recyclerView.setAdapter(calorieAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(dialogView);

        builder.setPositiveButton("Search", null); // Set null for custom listener
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Custom click listener for the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String activity = ActivityEditText.getText().toString().trim();
            String weightStr = WeigthEditText.getText().toString().trim();
            String durationStr = DurationEditText.getText().toString().trim();

            if (activity.isEmpty()||weightStr.isEmpty()) {
                Toast.makeText(requireActivity(), "Please fill in Activity and Weight fields", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int weight = Integer.parseInt(weightStr);
                    int duration = Integer.parseInt(durationStr);
                    fetchCalorie(activity, weight, duration, calorieAdapter);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireActivity(), "Enter valid numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchCalorie(String activity,int weight, int duration, CalorieAdapter calorieAdapter) {
        String apiKey = getResources().getString(R.string.api_key);

        CalorieApiService apiService = RetrofitClient.getRetrofitInstance().create(CalorieApiService.class);

        Call<List<Calorie>> call = apiService.getCalories(activity,weight,duration,apiKey);

        call.enqueue(new Callback<List<Calorie>>() {
            @Override
            public void onResponse(Call<List<Calorie>> call, Response<List<Calorie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Calorie> calorieList = response.body();

                    // Log the fetched calorie data
                    for (Calorie calorie : calorieList) {
                        Log.d("Calorie Data", "Name: " + calorie.getName() +
                                ", Calories per Hour: " + calorie.getCalories_per_hour() +
                                ", Duration: " + calorie.getDuration_minutes() +
                                ", Total Calories: " + calorie.getTotal_calories());
                    }

                    if (calorieList != null && !calorieList.isEmpty()) {
                        calorieAdapter.updateCalories(calorieList);  // Update adapter with new data
                        Toast.makeText(getActivity(), "Calorie data fetched successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "No data found for this query", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "API Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Calorie>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDialog2() {
        LayoutInflater inflater1 = getLayoutInflater();
        View dialogView = inflater1.inflate(R.layout.nutrition_box, null);

        EditText queryEditText = dialogView.findViewById(R.id.text_input);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the NutritionAdapter
        List<Nutrition> nutritionList = new ArrayList<>();
        NutritionAdapter nutritionAdapter = new NutritionAdapter(nutritionList);
        recyclerView.setAdapter(nutritionAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(dialogView);

        builder.setPositiveButton("Search", null); // Set null for custom listener
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Custom click listener for the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String query = queryEditText.getText().toString().trim();

            if (query.isEmpty()) {
                Toast.makeText(requireActivity(), "Please enter a food item to search", Toast.LENGTH_SHORT).show();
            } else {
                fetchNutrition(query, nutritionAdapter);
            }
        });
    }

    private void fetchNutrition(String query, NutritionAdapter nutritionAdapter) {
        String apiKey = getResources().getString(R.string.api_key);

        NutritionApiService apiService = RetrofitClient.getRetrofitInstance().create(NutritionApiService.class);

        Call<List<Nutrition>> call = apiService.getNutrition(query, apiKey);

        call.enqueue(new Callback<List<Nutrition>>() {
            @Override
            public void onResponse(Call<List<Nutrition>> call, Response<List<Nutrition>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Nutrition> nutritionList = response.body();

                    // Log the fetched nutrition data
                    for (Nutrition nutrition : nutritionList) {
                        Log.d("Nutrition Data", "Name: " + nutrition.getName() +
                                ", Fat: " + nutrition.getFat_total_g() +
                                ", Carbs: " + nutrition.getCarbohydrates_total_g() +
                                ", Sugar: " + nutrition.getSugar_g());
                    }

                    if (nutritionList != null && !nutritionList.isEmpty()) {
                        nutritionAdapter.updateNutritionList(nutritionList);
                        Toast.makeText(getActivity(), "Nutrition data fetched successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "No data found for this query", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "API Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Nutrition>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog1() {
        LayoutInflater inflater1 = getLayoutInflater();
        View dialogView = inflater1.inflate(R.layout.exercise_box, null);

        EditText nameEditText = dialogView.findViewById(R.id.exercise_name);
        EditText typeEditText = dialogView.findViewById(R.id.exercise_type);
        EditText muscleEditText = dialogView.findViewById(R.id.exercise_muscle);
        EditText difficultyEditText = dialogView.findViewById(R.id.exercise_difficulty);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view);

        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(exerciseAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(dialogView);

        builder.setPositiveButton("Search", null); // Set null for now
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set a custom click listener for the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String type = typeEditText.getText().toString().trim();
            String muscle = muscleEditText.getText().toString().trim();
            String difficulty = difficultyEditText.getText().toString().trim();

            if (name.isEmpty() && type.isEmpty() && muscle.isEmpty() && difficulty.isEmpty()) {
                Toast.makeText(requireActivity(), "Please fill at least one field to search", Toast.LENGTH_SHORT).show();
            } else {
                // Call fetchExercises to update the RecyclerView
                fetchExercises(name, type, muscle, difficulty, exerciseAdapter);

                // Optionally close the dialog if needed
                // dialog.dismiss();
            }
        });
    }


    private void fetchExercises(String name, String type, String muscle, String difficulty, ExerciseAdapter exerciseAdapter) {
        String apiKey = getResources().getString(R.string.api_key);

        ExerciseApiService apiService = RetrofitClient.getRetrofitInstance().create(ExerciseApiService.class);

        Call<List<Exercise>> call = apiService.getExercises(name,type, muscle, difficulty, apiKey);

        call.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Exercise> exercises = response.body();

                // Log the fetched exercises
                for (Exercise exercise : exercises) {
                    Log.d("Exercise Data", "Name: " + exercise.getName() +
                            ", Muscles: " + exercise.getMuscles() +
                            ", Instructions: " + exercise.getInstructions());
                }

                    if (exercises != null && !exercises.isEmpty()) {
                        exerciseAdapter.updateExercises(exercises);
                        Toast.makeText(getActivity(), "Exercises fetched successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "No exercises found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "API Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
