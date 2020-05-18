package com.eliorcohen12345.locationproject.DataAppPackage;

import android.app.Application;

import java.util.ArrayList;

import androidx.lifecycle.AndroidViewModel;

public class PlaceViewModelSearchDB extends AndroidViewModel {

    private MapDBHelperSearch mapDBHelperSearch;  // The SQLiteHelper of the app

    public PlaceViewModelSearchDB(Application application) {
        super(application);

        mapDBHelperSearch = new MapDBHelperSearch(application);
    }

    public ArrayList<PlaceModel> getAllPlaces() {
        return mapDBHelperSearch.getAllMaps();
    }

    public void addMapPlaces(ArrayList<PlaceModel> placeModels) {
        mapDBHelperSearch.addMapList(placeModels);
    }

    public void deleteAll() {
        mapDBHelperSearch.deleteData();
    }

}
