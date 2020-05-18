package com.eliorcohen12345.locationproject.DataAppPackage;

import android.app.Application;

import java.util.ArrayList;

import androidx.lifecycle.AndroidViewModel;

public class PlaceViewModelFavorites extends AndroidViewModel {

    private MapDBHelperFavorites mapDBHelperFavorites;  // The SQLiteHelper of the app
    private ArrayList<PlaceModel> mAllPlacesFavorites;

    public PlaceViewModelFavorites(Application application) {
        super(application);

        mapDBHelperFavorites = new MapDBHelperFavorites(application);
        mAllPlacesFavorites = mapDBHelperFavorites.getAllMaps();
    }

    public ArrayList<PlaceModel> getAllPlaces() {
        return mAllPlacesFavorites;
    }

    public void insertPlace(String name, String address, Double lat, Double lng, String photo) {
        mapDBHelperFavorites.addMapFav(name, address, lat, lng, photo);
    }

    public void deleteAll() {
        mapDBHelperFavorites.deleteData();
    }

    public void deletePlace(PlaceModel places) {
        mapDBHelperFavorites.deleteMap(places);
    }

    public void updatePlace(String name, String address, Double lat, Double lng, String photo, String id) {
        mapDBHelperFavorites.updateMap(name, address, lat, lng, photo, id);
    }

}
