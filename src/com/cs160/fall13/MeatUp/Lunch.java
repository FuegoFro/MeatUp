package com.cs160.fall13.MeatUp;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class Lunch implements Parcelable, Serializable {

    private Calendar time;
    private String location;
    private ArrayList<String> attendees = new ArrayList<String>();
    private int id;

    public Lunch() {
        id = new Random().nextInt();
    } // Default constructor for Jackson

    public Lunch (Calendar time, String location, ArrayList<String> attendees) {
        this(time, location, attendees, new Random().nextInt());
    }

    public Lunch (Calendar time, String location, ArrayList<String> attendees, int id) {
        this.time = time;
        this.location = location;
        this.attendees = attendees;
        this.id = id;
    }
    public Calendar getTime() {
        return time;
    }

    @JsonIgnore
    public void setTime(Calendar time) {
        this.time = time;
    }

    @JsonProperty("time")
    public void setTime(long milliseconds) {
        time = Calendar.getInstance();
        time.setTimeInMillis(milliseconds);
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

    @JsonIgnore
    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    public int getId() {
        return id;
    }

    @JsonProperty("attendees")
    public void setAttendees(String[] attendees) {
        this.attendees = new ArrayList<String>(Arrays.asList(attendees));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Lunch> CREATOR = new Parcelable.Creator<Lunch>() {
        public Lunch createFromParcel(Parcel in) {
            return new Lunch(in);
        }

        @Override
        public Lunch[] newArray(int i) {
            return new Lunch[i];
        }
    };


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(location);
        parcel.writeStringList(attendees);
        parcel.writeSerializable(time);
        parcel.writeInt(id);
    }

    private Lunch(Parcel in) {
        location = in.readString();
        in.readStringList(attendees);
        time = (Calendar) in.readSerializable();
        id = in.readInt();
    }
}
