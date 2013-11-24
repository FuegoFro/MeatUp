package com.cs160.fall13.MeatUp;

/**
 * Created with IntelliJ IDEA.
 * User: cjrd
 * Date: 11/24/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class Restaurant {

    private String title;
    private double lat,
                   lon;
    private boolean isVegatarian;
    private boolean isVegan;
    private int rating;

    public Restaurant(String title, double lat, double lon, boolean vegatarian, boolean vegan, int rating) {
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
