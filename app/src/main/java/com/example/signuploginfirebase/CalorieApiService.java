package com.example.signuploginfirebase;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CalorieApiService {
    @GET("caloriesburned")
    Call<List<Calorie>> getCalories(
            @Query("activity") String activity,
            @Query("weight") int weight,
            @Query("duration") int duration,
            @Header("X-Api-Key") String apiKey
    );
}
