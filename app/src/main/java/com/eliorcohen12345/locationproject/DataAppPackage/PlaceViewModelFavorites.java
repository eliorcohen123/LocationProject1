package com.eliorcohen12345.locationproject.DataAppPackage;

import android.app.Application;

import java.util.ArrayList;

import androidx.lifecycle.AndroidViewModel;

import com.eliorcohen12345.locationproject.ModelsPackage.Results;

public class PlaceViewModelFavorites extends AndroidViewModel {

    private MapDBHelperFavorites mapDBHelperFavorites;  // The SQLiteHelper of the app

    public PlaceViewModelFavorites(Application application) {
        super(application);

        mapDBHelperFavorites = new MapDBHelperFavorites(application);
    }

    public ArrayList<Results> getAllPlaces() {
        return mapDBHelperFavorites.getAllMaps();
    }

    public void insertPlace(String name, String address, Double lat, Double lng, String photo) {
        mapDBHelperFavorites.addMap(name, address, lat, lng, photo);
    }

    public void deleteAll() {
        mapDBHelperFavorites.deleteData();
    }

    public void deletePlace(Results places) {
        mapDBHelperFavorites.deleteMap(places);
    }

    public void updatePlace(String name, String address, Double lat, Double lng, String photo, String id) {
        mapDBHelperFavorites.updateMap(name, address, lat, lng, photo, id);
    }

}
