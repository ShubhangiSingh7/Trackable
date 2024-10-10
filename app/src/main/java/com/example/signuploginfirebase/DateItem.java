package com.example.signuploginfirebase;

public class DateItem {
    private String date;
    private String day;
    private boolean isCurrentDate;

    public DateItem(String date, String day,boolean isCurrentDate) {
        this.date = date;
        this.day = day;
        this.isCurrentDate = isCurrentDate;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }
    public boolean isCurrentDate() {
        return isCurrentDate;
    }
}
