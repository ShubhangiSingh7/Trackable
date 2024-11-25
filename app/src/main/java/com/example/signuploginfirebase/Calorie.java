package com.example.signuploginfirebase;

public class Calorie {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories_per_hour() {
        return calories_per_hour;
    }

    public void setCalories_per_hour(int calories_per_hour) {
        this.calories_per_hour = calories_per_hour;
    }

    public int getDuration_minutes() {
        return duration_minutes;
    }

    public void setDuration_minutes(int duration_minutes) {
        this.duration_minutes = duration_minutes;
    }

    public int getTotal_calories() {
        return total_calories;
    }

    public void setTotal_calories(int total_calories) {
        this.total_calories = total_calories;
    }

    private String name;
    private int calories_per_hour;
    private int duration_minutes;
    private int total_calories;
}
