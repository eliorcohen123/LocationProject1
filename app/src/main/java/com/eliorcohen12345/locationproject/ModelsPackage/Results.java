package com.eliorcohen12345.locationproject.ModelsPackage;

import java.io.Serializable;
import java.util.List;

public class Results implements Serializable {

    private String place_id;
    private String name;
    private String vicinity;
    private String distance;
    private double rating;
    private int user_ratings_total;
    private List<Photos> photos;
    private Geometry geometry;
    private OpeningHours opening_hours;

    // Subject class to the SQLiteHelper
    public Results(String name, String vicinity, Geometry geometry, List<Photos> photos) {
        this.name = name;
        this.vicinity = vicinity;
        this.geometry = geometry;
        this.photos = photos;
    }

    public Results() {

    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

    public String getPhoto_reference() {
        return photos.get(0).getPhoto_reference();
    }

    public void setPhoto_reference(String photo_reference) {
        this.photos.get(0).setPhoto_reference(photo_reference);
    }

    public double getLat() {
        return geometry.getLocation().getLat();
    }

    public void setLat(double lat) {
        this.geometry.getLocation().setLat(lat);
    }

    public double getLng() {
        return geometry.getLocation().getLng();
    }

    public void setLng(double lng) {
        this.geometry.getLocation().setLng(lng);
    }

    public boolean getOpening_hours() {
        return opening_hours.isOpen_now();
    }

    public void setOpening_hours(OpeningHours opening_hours) {
        this.opening_hours.isOpen_now();
    }

}