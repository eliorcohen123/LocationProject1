package com.eliorcohen12345.locationproject.ViewModelsPackage;

import android.app.Application;

import java.util.ArrayList;

import androidx.lifecycle.AndroidViewModel;

import com.eliorcohen12345.locationproject.DataAppPackage.MapDBHelperSearch;
import com.eliorcohen12345.locationproject.ModelsPackage.Results;

public class PlaceViewModelSearchDB extends AndroidViewModel {

    private MapDBHelperSearch mapDBHelperSearch;  // The SQLiteHelper of the app

    public PlaceViewModelSearchDB(Application application) {
        super(application);

        mapDBHelperSearch = new MapDBHelperSearch(application);
    }

    public ArrayList<Results> getAllPlaces() {
        return mapDBHelperSearch.getAllMaps();
    }

    public void addMapPlaces(ArrayList<Results> results) {
        mapDBHelperSearch.addMapList(results);
    }

    public void deleteAll() {
        mapDBHelperSearch.deleteData();
    }

}
