package com.cs160.fall13.MeatUp;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: cjrd
 * Date: 11/24/13
 * Time: 3:38 PM
 */
public class Restaurant implements Serializable {

    private String title;
    private double lat,
            lon,
            rating;
    private boolean isVegatarian;
    private boolean isVegan;

    public Restaurant(String title, double lat, double lon, boolean vegatarian, boolean vegan, double rating) {
        this.title = title;
        this.lat = lat;
        this.lon = lon;
        isVegatarian = vegatarian;
        isVegan = vegan;
        this.rating = rating;
    }

    public LatLng getLatLng(){
        return new LatLng(this.lat,this.lon);
    }

    public MarkerOptions getMarkerOptions(){
        MarkerOptions mOpts =  new MarkerOptions();
        mOpts.title(this.getTitle())
                .snippet("Determining Distance...") // FIXME how to get distance? -- or should we get it here?    -CJR
                .position(this.getLatLng());
        return mOpts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public boolean isVegatarian() {
        return isVegatarian;
    }

    public void setVegatarian(boolean vegatarian) {
        isVegatarian = vegatarian;
    }

    public boolean isVegan() {
        return isVegan;
    }

    public void setVegan(boolean vegan) {
        isVegan = vegan;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
