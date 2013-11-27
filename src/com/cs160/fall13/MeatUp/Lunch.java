package com.cs160.fall13.MeatUp;

import java.util.ArrayList;
import java.util.Date;

public class Lunch {

    private int hour;
    private int minute;

    private String day;

    private String location;
    private ArrayList<String> attendees = new ArrayList<String>();

    public Lunch (int hour, int minute, String day, String location) {
        setHour(hour);
        setMinute(minute);
        setLocation(location);
        setDay(day);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
