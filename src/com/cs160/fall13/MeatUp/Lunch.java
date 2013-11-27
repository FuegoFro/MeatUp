package com.cs160.fall13.MeatUp;

import java.util.ArrayList;
import java.util.Calendar;

public class Lunch {

    private Calendar time;
    private String location;
    private ArrayList<String> attendees = new ArrayList<String>();

    public Lunch (Calendar time, String location) {
        this.time = time;
        this.location = location;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
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
}
