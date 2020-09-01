package com.eliorcohen12345.locationproject.AsyncTaskPackage;

import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.eliorcohen12345.locationproject.ModelsPackage.PlaceModel;
import com.eliorcohen12345.locationproject.ViewModelsPackage.PlaceViewModelSearchDB;
import com.eliorcohen12345.locationproject.PagesPackage.SearchFragment;
import com.eliorcohen12345.locationproject.OthersPackage.ConApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetMapsAsyncTaskSearch extends AsyncTask<String, Integer, ArrayList<PlaceModel>> {

    private PlaceViewModelSearchDB placeViewModelSearchDB;
    private double diagonalInches;
    private ArrayList<PlaceModel> mPlaceModels = new ArrayList<>();

    // startShowingProgressDialog of FragmentSearch
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Tablet/Phone mode
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) ConApp.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches <= 6.5) {
            SearchFragment.startShowingProgressDialog();
        }
    }

    // DoInBackground of the JSON
    @Override
    protected ArrayList<PlaceModel> doInBackground(String... urls) {
        OkHttpClient client = new OkHttpClient();
        String urlQuery = urls[0];
        Request request = new Request.Builder()
                .url(urlQuery)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        if (!response.isSuccessful()) try {
            throw new IOException("Unexpected code " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert response.body() != null;
            mPlaceModels = getMapsListFromJson(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mPlaceModels;
    }

    // Get places from the JSON
    private ArrayList<PlaceModel> getMapsListFromJson(String jsonResponse) {
        List<PlaceModel> stubMapData;
        Gson gson = new GsonBuilder().create();
        MapResponse response = gson.fromJson(jsonResponse, MapResponse.class);
        stubMapData = response.results;
        ArrayList<PlaceModel> arrList = new ArrayList<>(stubMapData);

        return arrList;
    }

    // The response of the JSON
    private static class MapResponse {

        private List<PlaceModel> results;

        public MapResponse() {
            results = new ArrayList<>();
        }

    }

    // Execute the following:
    @Override
    protected void onPostExecute(ArrayList<PlaceModel> placeModels) {
        super.onPostExecute(placeModels);

        // Tablet/Phone mode
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) ConApp.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches <= 6.5) {
            SearchFragment.stopShowingProgressDialog();
        }

        placeViewModelSearchDB = new PlaceViewModelSearchDB(ConApp.getApplication());
        try {
            placeViewModelSearchDB.addMapPlaces(placeModels);
            SearchFragment.getData(placeModels);
        } catch (Exception e) {
            SearchFragment.getData(placeModels);
        }
    }

}
