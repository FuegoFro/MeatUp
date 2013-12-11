package com.cs160.fall13.MeatUp;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Calendar;

public class Lunch implements Parcelable {

    private Calendar time;
    private String location;
    private ArrayList<String> attendees = new ArrayList<String>();
    private int id;

    public Lunch (Calendar time, String location) {
        this.time = time;
        this.location = location;
        this.id = (int) (Math.random() * Integer.MAX_VALUE);
    }

    public Lunch (Calendar time, String location, int id) {
        this.time = time;
        this.location = location;
        this.id = id;
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

    public void setLocation(String locahtion) {
        this.location = location;
    }

    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static final Parcelable.Creator<Lunch> CREATOR = new Parcelable.Creator<Lunch>() {
        public Lunch createFromParcel(Parcel in) {
            return new Lunch(in);
        }

        @Override
        public Lunch[] newArray(int i) {
            return new Lunch[i];  //To change body of implemented methods use File | Settings | File Templates.
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
