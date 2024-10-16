package com.example.signuploginfirebase;

public class TaskModel {
    public String title;
    public String description;
    public String date;
    public String time;
    public boolean repeatWeekly;

    // Default constructor required for calls to DataSnapshot.getValue(TaskModel.class)
    public TaskModel() {
    }

    // Parameterized constructor to create a task object
    public TaskModel(String title, String description, String date, String time, boolean repeatWeekly) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.repeatWeekly = repeatWeekly;
    }

    // Getter and setter methods (optional, depending on how you want to use the class)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRepeatWeekly() {
        return repeatWeekly;
    }

    public void setRepeatWeekly(boolean repeatWeekly) {
        this.repeatWeekly = repeatWeekly;
    }
}
