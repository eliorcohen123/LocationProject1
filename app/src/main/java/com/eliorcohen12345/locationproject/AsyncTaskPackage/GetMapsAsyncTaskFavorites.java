package com.eliorcohen12345.locationproject.AsyncTaskPackage;

import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.eliorcohen12345.locationproject.CustomAdapterPackage.PlaceCustomAdapterFavorites;
import com.eliorcohen12345.locationproject.DataAppPackage.MapDBHelperFavorites;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;

import java.util.ArrayList;

public class GetMapsAsyncTaskFavorites extends AsyncTask<MapDBHelperFavorites, Integer, ArrayList<PlaceModel>> {

    private RecyclerView mRecyclerView;
    private PlaceCustomAdapterFavorites mPlaceCustomAdapterFavorites;  // Adapter
    private ArrayList<PlaceModel> mMapList;  // ArrayList of PlaceModel

    // AsyncTask to the RecyclerView
    public GetMapsAsyncTaskFavorites(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    // DoInBackground of the ArrayList of PlaceModel that put the getAllMaps in the SQLiteHelper in the ArrayList of PlaceModel
    @Override
    protected ArrayList<PlaceModel> doInBackground(MapDBHelperFavorites... mapDBHelperFavorites) {
        MapDBHelperFavorites myDb = mapDBHelperFavorites[0];
        mMapList = myDb.getAllMaps();

        return mMapList;
    }

    // execute to add maps manually
    @Override
    protected void onPostExecute(ArrayList<PlaceModel> placeModels) {
        super.onPostExecute(placeModels);

        mPlaceCustomAdapterFavorites = new PlaceCustomAdapterFavorites(mRecyclerView.getContext(), placeModels);
        mRecyclerView.setAdapter(mPlaceCustomAdapterFavorites);
    }

}
