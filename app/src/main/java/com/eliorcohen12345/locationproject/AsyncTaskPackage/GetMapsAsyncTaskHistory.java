package com.eliorcohen12345.locationproject.AsyncTaskPackage;

import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.eliorcohen12345.locationproject.CustomAdapterPackage.PlaceCustomAdapterSearch;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceViewModelSearchDB;

import java.util.ArrayList;

public class GetMapsAsyncTaskHistory extends AsyncTask<PlaceViewModelSearchDB, Integer, ArrayList<PlaceModel>> {

    private RecyclerView mRecyclerView;
    private PlaceCustomAdapterSearch mPlaceCustomAdapterSearch;  // Adapter
    private ArrayList<PlaceModel> mMapList;  // ArrayList of PlaceModel

    // AsyncTask to the RecyclerView
    public GetMapsAsyncTaskHistory(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    // DoInBackground of the ArrayList of PlaceModel that put the getAllMaps in the SQLiteHelper in the ArrayList of PlaceModel
    @Override
    protected ArrayList<PlaceModel> doInBackground(PlaceViewModelSearchDB... placeViewModelSearchDBS) {
        PlaceViewModelSearchDB myDb = placeViewModelSearchDBS[0];
        mMapList = myDb.getAllPlaces();

        return mMapList;
    }

    // Execute to add places manually
    @Override
    protected void onPostExecute(ArrayList<PlaceModel> placeModels) {
        super.onPostExecute(placeModels);

        mMapList = placeModels;
        mPlaceCustomAdapterSearch = new PlaceCustomAdapterSearch(mRecyclerView.getContext(), mMapList);
        mRecyclerView.setHasFixedSize(true);
        mPlaceCustomAdapterSearch.setMapsCollections();
        mRecyclerView.setAdapter(mPlaceCustomAdapterSearch);
    }

}
