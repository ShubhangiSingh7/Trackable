package com.example.signuploginfirebase;

public class DateItem {
    private String date;
    private String day;
    private boolean isToday;
    private boolean isSelected;

    public DateItem(String date, String day, boolean isToday) {
        this.date = date;
        this.day = day;
        this.isToday = isToday;
        this.isSelected = false;
    }

    public String getDate() { return date; }
    public String getDay() { return day; }
    public boolean isToday() { return isToday; }
    public boolean isSelected() { return isSelected; }
    public void setToday(boolean today) {
        isToday = today;
    }

}


