package com.eliorcohen12345.locationproject.ModelsPackage;

import java.io.Serializable;

// class top of Lat and lng
public class Location implements Serializable {

    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
