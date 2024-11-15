package com.example.signuploginfirebase;

public class TaskModel {
    private String title;
    private String description;
    private String date;
    private boolean isCompleted;
    private boolean repeatWeekly;
    private String taskId;
    private int duration;
    private String time;

    // Default constructor required for calls to DataSnapshot.getValue(TaskModel.class)
    public TaskModel() {
    }

    // Parameterized constructor to create a task object
    public TaskModel(String title, String description, String date, String time, boolean repeatWeekly, boolean isCompleted, String taskId, int duration) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.repeatWeekly = repeatWeekly;
        this.isCompleted = isCompleted;
        this.taskId = taskId;
        this.duration = duration;
    }

    // Getter and setter for taskId
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    // Getter and setter methods for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and setter methods for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter methods for date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getter and setter methods for time
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Getter and setter methods for repeatWeekly
    public boolean isRepeatWeekly() {
        return repeatWeekly;
    }

    public void setRepeatWeekly(boolean repeatWeekly) {
        this.repeatWeekly = repeatWeekly;
    }

    // Getter and setter methods for isCompleted
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Getter and setter methods for duration
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
