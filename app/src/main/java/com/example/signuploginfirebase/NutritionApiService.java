package com.example.signuploginfirebase;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NutritionApiService {
    @GET("nutrition")
    Call<List<Nutrition>> getNutrition(
            @Query("query") String query,
            @Header("X-Api-Key") String apiKey
    );
}
