package com.eliorcohen12345.locationproject.AsyncTaskPackage;

import android.os.AsyncTask;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.eliorcohen12345.locationproject.CustomAdapterPackage.PlaceCustomAdapterFavorites;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceViewModelFavorites;

import java.util.ArrayList;

public class GetMapsAsyncTaskFavorites extends AsyncTask<PlaceViewModelFavorites, Integer, ArrayList<PlaceModel>> {

    private RecyclerView mRecyclerView;
    private PlaceCustomAdapterFavorites mPlaceCustomAdapterFavorites;  // Adapter
    private ArrayList<PlaceModel> mMapList;  // ArrayList of PlaceModel

    // AsyncTask to the RecyclerView
    public GetMapsAsyncTaskFavorites(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    // DoInBackground of the ArrayList of PlaceModel that put the getAllMaps in the SQLiteHelper in the ArrayList of PlaceModel
    @Override
    protected ArrayList<PlaceModel> doInBackground(PlaceViewModelFavorites... placeViewModelFavorite) {
        PlaceViewModelFavorites placeViewModelFavorites = placeViewModelFavorite[0];
        mMapList = placeViewModelFavorites.getAllPlaces();

        return mMapList;
    }

    // Execute to add maps manually
    @Override
    protected void onPostExecute(ArrayList<PlaceModel> placeModels) {
        super.onPostExecute(placeModels);

        mMapList = placeModels;
        mPlaceCustomAdapterFavorites = new PlaceCustomAdapterFavorites(mRecyclerView.getContext(), mMapList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mPlaceCustomAdapterFavorites.setMapsCollections();
        mRecyclerView.setAdapter(mPlaceCustomAdapterFavorites);
    }

}
