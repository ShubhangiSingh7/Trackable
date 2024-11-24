package com.example.signuploginfirebase;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Header;

public interface ExerciseApiService {
    @GET("exercises")
    Call<List<Exercise>> getExercises(
            @Query("name") String name,
            @Query("type") String type,
            @Query("muscle") String muscle,
            @Query("difficulty") String difficulty,
            @Header("X-Api-Key") String apiKey
    );
}

