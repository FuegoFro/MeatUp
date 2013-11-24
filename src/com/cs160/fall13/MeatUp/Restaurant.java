package com.cs160.fall13.MeatUp;

/**
 * Created with IntelliJ IDEA.
 * User: cjrd
 * Date: 11/24/13
 * Time: 3:38 PM
 */
public class Restaurant {

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
