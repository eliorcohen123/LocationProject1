package com.eliorcohen12345.locationproject.AsyncTaskPackage;

import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.eliorcohen12345.locationproject.CustomAdapterPackage.PlaceCustomAdapterSearch;
import com.eliorcohen12345.locationproject.DataAppPackage.MapDBHelperSearch;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;

import java.util.ArrayList;

public class GetMapsAsyncTaskHistory extends AsyncTask<MapDBHelperSearch, Integer, ArrayList<PlaceModel>> {

    private RecyclerView mRecyclerView;
    private PlaceCustomAdapterSearch mPlaceCustomAdapterSearch;  // Adapter
    private ArrayList<PlaceModel> mMapList;  // ArrayList of PlaceModel

    // AsyncTask to the RecyclerView
    public GetMapsAsyncTaskHistory(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    // DoInBackground of the ArrayList of PlaceModel that put the getAllMaps in the SQLiteHelper in the ArrayList of PlaceModel
    @Override
    protected ArrayList<PlaceModel> doInBackground(MapDBHelperSearch... mapDBHelperSearches) {
        MapDBHelperSearch myDb = mapDBHelperSearches[0];
        mMapList = myDb.getAllMaps();

        return mMapList;
    }

    // execute to add places manually
    @Override
    protected void onPostExecute(ArrayList<PlaceModel> placeModels) {
        super.onPostExecute(placeModels);

        mPlaceCustomAdapterSearch = new PlaceCustomAdapterSearch(mRecyclerView.getContext(), placeModels);
        mPlaceCustomAdapterSearch.setMapsCollections();
        mRecyclerView.setAdapter(mPlaceCustomAdapterSearch);
    }

}
