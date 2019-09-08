package com.eliorcohen12345.locationproject.MapsDataPackage;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eliorcohen12345.locationproject.AsyncTaskPackage.GetMapsAsyncTaskHistory;
import com.eliorcohen12345.locationproject.AsyncTaskPackage.GetMapsAsyncTaskSearch;
import com.eliorcohen12345.locationproject.CustomAdapterPackage.PlaceCustomAdapterSearch;
import com.eliorcohen12345.locationproject.DataAppPackage.MapDBHelperSearch;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.MainAndOtherPackage.ConApp;
import com.eliorcohen12345.locationproject.MainAndOtherPackage.ItemDecoration;
import com.eliorcohen12345.locationproject.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import eliorcohen.com.googlemapsapi.GoogleMapsApi;

public class FragmentSearch extends Fragment implements View.OnClickListener {

    private static ArrayList<PlaceModel> mMapList;
    private static PlaceCustomAdapterSearch mAdapter;
    private static RecyclerView mRecyclerView;
    private static MapDBHelperSearch mMapDBHelperSearch;
    private static ProgressDialog mProgressDialogInternet;
    private static FragmentSearch mFragmentSearch;
    private GetMapsAsyncTaskSearch mGetMapsAsyncTaskSearch;
    private GetMapsAsyncTaskHistory mGetMapsAsyncTaskHistory;
    private View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static Location location;
    private static LocationManager locationManager;
    private Criteria criteria;
    private SharedPreferences prefsSeek, prefsOpen, prefsPage, prefsPre;
    private SharedPreferences.Editor editorPage, editorPre;
    private static ItemDecoration itemDecoration;
    private int myRadius, myPage = 1;
    private ImageView imagePre, imageNext, imagePreFirst;
    private TextView textPage;
    private static String provider;
    private String hasPage, myStringQueryPage, myStringQueryType, myStringQueryQuery, pageTokenPre, myOpen;
    private Button btnBank, btnBar, btnBeauty, btnBooks, btnBusStation, btnCars, btnClothing, btnDoctor, btnGasStation,
            btnGym, btnJewelry, btnPark, btnRestaurant, btnSchool, btnSpa;
    private GoogleMapsApi googleMapsApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search_layout, container, false);

        initUI();
        initListeners();
        initLocation();
        refreshUI();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getData();
        getResumeTypeQuery();
    }

    private void initUI() {
        mRecyclerView = mView.findViewById(R.id.places_list_search);

        btnBank = mView.findViewById(R.id.btnBank);
        btnBar = mView.findViewById(R.id.btnBar);
        btnBeauty = mView.findViewById(R.id.btnBeauty);
        btnBooks = mView.findViewById(R.id.btnBooks);
        btnBusStation = mView.findViewById(R.id.btnBusStation);
        btnCars = mView.findViewById(R.id.btnCars);
        btnClothing = mView.findViewById(R.id.btnClothing);
        btnDoctor = mView.findViewById(R.id.btnDoctor);
        btnGasStation = mView.findViewById(R.id.btnGasStation);
        btnGym = mView.findViewById(R.id.btnGym);
        btnJewelry = mView.findViewById(R.id.btnJewelry);
        btnPark = mView.findViewById(R.id.btnPark);
        btnRestaurant = mView.findViewById(R.id.btnRestaurant);
        btnSchool = mView.findViewById(R.id.btnSchool);
        btnSpa = mView.findViewById(R.id.btnSpa);

        imagePre = mView.findViewById(R.id.imagePre);
        imageNext = mView.findViewById(R.id.imageNext);
        imagePreFirst = mView.findViewById(R.id.imagePreFirst);
        textPage = mView.findViewById(R.id.textPage);

        getClearPrefs();

        mFragmentSearch = this;

        mMapDBHelperSearch = new MapDBHelperSearch(getActivity());
        prefsSeek = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefsOpen = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMapList = new ArrayList<>();
        googleMapsApi = new GoogleMapsApi();

        setHasOptionsMenu(true);
    }

    private void initListeners() {
        btnBank.setOnClickListener(this);
        btnBar.setOnClickListener(this);
        btnBeauty.setOnClickListener(this);
        btnBooks.setOnClickListener(this);
        btnBusStation.setOnClickListener(this);
        btnCars.setOnClickListener(this);
        btnClothing.setOnClickListener(this);
        btnDoctor.setOnClickListener(this);
        btnGasStation.setOnClickListener(this);
        btnGym.setOnClickListener(this);
        btnJewelry.setOnClickListener(this);
        btnPark.setOnClickListener(this);
        btnRestaurant.setOnClickListener(this);
        btnSchool.setOnClickListener(this);
        btnSpa.setOnClickListener(this);
        imageNext.setOnClickListener(this);
        imagePre.setOnClickListener(this);
        imagePreFirst.setOnClickListener(this);
    }

    private void initLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
    }

    private void getClearPrefs() {
        prefsPage = getContext().getSharedPreferences("mysettingspage", Context.MODE_PRIVATE);
        prefsPage.edit().clear().apply();

        editorPage = prefsPage.edit();

        prefsPre = getContext().getSharedPreferences("mysettingspre", Context.MODE_PRIVATE);
        prefsPre.edit().clear().apply();

        editorPre = prefsPre.edit();
    }

    private void refreshUI() {
        swipeRefreshLayout = mView.findViewById(R.id.swipe_containerFrag);  // ID of the SwipeRefreshLayout of FragmentSearch
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorOrange));  // Colors of the SwipeRefreshLayout of FragmentSearch
        // Refresh the MapDBHelper of app in RecyclerView of MainActivity
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Vibration for 0.1 second
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(100);
                }

                getActivity().finish();
                startActivity(getActivity().getIntent());  // Refresh activity

                Toast toast = Toast.makeText(getContext(), "The list are refreshed!", Toast.LENGTH_LONG);
                View view = toast.getView();
                view.getBackground().setColorFilter(getResources().getColor(R.color.colorLightBlue), PorterDuff.Mode.SRC_IN);
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(getResources().getColor(R.color.colorDarkBrown));
                toast.show();  // Toast

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private static void getData() {
        if (!isConnected(mFragmentSearch.getContext())) {
            mMapList = mMapDBHelperSearch.getAllMaps();
        }
        mAdapter = new PlaceCustomAdapterSearch(ConApp.getmContext(), mMapList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentSearch.getContext()));
        if (itemDecoration == null) {
            itemDecoration = new ItemDecoration(20);
            mRecyclerView.addItemDecoration(itemDecoration);
        }
        mAdapter.setMaps(mMapList);
        mRecyclerView.setAdapter(mAdapter);
    }

    // Set maps in FragmentSearch
    public static void setMaps(ArrayList<PlaceModel> list) {
        mMapList = list;
        getData();
    }

    // Check network
    private static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    private AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or Wi-Fi to access this. Press OK to Resume");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }

    // Sets off the menu of activity_menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu, menu);

        // SearchView of FragmentSearch
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        // Change colors of the searchView upper panel
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Set styles for expanded state here
                if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Set styles for collapsed state here
                if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                }
                return true;
            }
        });

        // Continued of SearchView of FragmentSearch
        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo((getActivity()).getComponentName()));
            searchView.setQueryHint(Html.fromHtml("<font color = #FFEA54>" + getResources().getString(R.string.hint) + "</font>"));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    getCheckBtnSearch("", newText);

                    stopShowingProgressDialog();
                    return true;
                }
            });
        }
    }

    // Options in the activity_menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            case R.id.nearByMe:
                getCheckBtnSearch("", "");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getResumeTypeQuery() {
        getCheckBtnSearch("", "");
    }

    private void getDataPrefsPage(String type, String query) {
        editorPage.putString("myStringQueryType", type);
        editorPage.putString("myStringQueryQuery", query);
        editorPage.apply();
    }

    @Override
    public void onClick(View v) {
        myStringQueryPage = prefsPage.getString("myStringQueryPage", "");
        myStringQueryType = prefsPage.getString("myStringQueryType", "");
        myStringQueryQuery = prefsPage.getString("myStringQueryQuery", "");
        switch (v.getId()) {
            case R.id.btnBank:
                getCheckBtnSearch("bank", "");
                break;
            case R.id.btnBar:
                getCheckBtnSearch("bar|night_club", "");
                break;
            case R.id.btnBeauty:
                getCheckBtnSearch("beauty_salon|hair_care", "");
                break;
            case R.id.btnBooks:
                getCheckBtnSearch("book_store|library", "");
                break;
            case R.id.btnBusStation:
                getCheckBtnSearch("bus_station", "");
                break;
            case R.id.btnCars:
                getCheckBtnSearch("car_dealer|car_rental|car_repair|car_wash", "");
                break;
            case R.id.btnClothing:
                getCheckBtnSearch("clothing_store", "");
                break;
            case R.id.btnDoctor:
                getCheckBtnSearch("doctor", "");
                break;
            case R.id.btnGasStation:
                getCheckBtnSearch("gas_station", "");
                break;
            case R.id.btnGym:
                getCheckBtnSearch("gym", "");
                break;
            case R.id.btnJewelry:
                getCheckBtnSearch("jewelry_store", "");
                break;
            case R.id.btnPark:
                getCheckBtnSearch("park|amusement_park|parking|rv_park", "");
                break;
            case R.id.btnRestaurant:
                getCheckBtnSearch("food|restaurant|cafe|bakery", "");
                break;
            case R.id.btnSchool:
                getCheckBtnSearch("school", "");
                break;
            case R.id.btnSpa:
                getCheckBtnSearch("spa", "");
                break;
            case R.id.imageNext:
                getTypeQuery(myStringQueryPage, myStringQueryType, myStringQueryQuery);

                myPage++;

                getAllCheckPage(myPage);
                break;
            case R.id.imagePre:
                if (myPage == 2) {
                    pageTokenPre = "";
                } else {
                    pageTokenPre = prefsPre.getString("mystringquerypre", "");
                }

                getTypeQuery(pageTokenPre, myStringQueryType, myStringQueryQuery);

                myPage--;

                getAllCheckPage(myPage);
                break;
            case R.id.imagePreFirst:
                getTypeQuery("", myStringQueryType, myStringQueryQuery);

                myPage = 1;

                getAllCheckPage(myPage);
                break;
        }
    }

    private void getCheckBtnSearch(String type, String query) {
        getClearPrefs();
        getTypeQuery("", type, query);
        getDataPrefsPage(type, query);

        myPage = 1;

        getAllCheckPage(myPage);
    }

    private void getAllCheckPage(int page) {
        getPage0();
        getPage1();
        getPageText(page);
    }

    private void getPage0() {
        if (myPage <= 0) {
            myPage = 1;
        } else {
            imagePre.setVisibility(View.VISIBLE);
        }
    }

    private void getPage1() {
        if (myPage == 1) {
            imagePre.setVisibility(View.GONE);
        } else {
            imagePre.setVisibility(View.VISIBLE);
        }

        if (myPage > 2) {
            imagePreFirst.setVisibility(View.VISIBLE);
        } else {
            imagePreFirst.setVisibility(View.GONE);
        }
    }

    private void getPageText(int page) {
        textPage.setText(String.valueOf(page));
    }

    private void getTypeQuery(String pageToken, String type, String query) {
        if (!isConnected(getContext())) {
            mMapList = mMapDBHelperSearch.getAllMaps();
            mAdapter = new PlaceCustomAdapterSearch(getActivity(), mMapList);
            // Put AsyncTask in the RecyclerView of fragmentSearch to execute the SQLiteHelper
            mGetMapsAsyncTaskHistory = new GetMapsAsyncTaskHistory(mRecyclerView);
            mGetMapsAsyncTaskHistory.execute(mMapDBHelperSearch);
            buildDialog(getContext()).show();
        } else {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            }// TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
            if (provider != null) {
                location = locationManager.getLastKnownLocation(provider);
                // Search maps from that URL and put them in the SQLiteHelper
                if (location != null) {
                    if (mAdapter != null) {
                        if (query.equals("")) {
                            myRadius = prefsSeek.getInt("seek", 5000);
                        } else {
                            myRadius = 50000;
                        }
                        myOpen = prefsOpen.getString("open", "");
                        mMapDBHelperSearch.deleteData();
                        String myQuery = googleMapsApi.getStringGoogleMapsApi(location.getLatitude(), location.getLongitude(), myRadius, pageToken, myOpen, type, query, getString(R.string.api_key_search));
                        mGetMapsAsyncTaskSearch = new GetMapsAsyncTaskSearch();
                        mGetMapsAsyncTaskSearch.execute(myQuery);

                        // Get Pages
                        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                googleMapsApi.getStringGoogleMapsApi(location.getLatitude(), location.getLongitude(), myRadius, pageToken, myOpen, type, query, getString(R.string.api_key_search)), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject mainObj = new JSONObject(response);
                                    if (mainObj.has("next_page_token")) {
                                        imageNext.setVisibility(View.VISIBLE);
                                        hasPage = mainObj.getString("next_page_token");
                                    } else {
                                        imageNext.setVisibility(View.GONE);
                                        hasPage = "";
                                    }
                                    editorPage.putString("myStringQueryPage", hasPage);
                                    editorPage.apply();

                                    if (myPage == 1) {
                                        editorPre.putString("mystringquerypre", hasPage);
                                        editorPre.apply();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        requestQueue.add(stringRequest);
                    }
                }
            }
        }
    }

    // stopShowingProgressDialog
    public static void stopShowingProgressDialog() {
        if (mProgressDialogInternet != null) {
            mProgressDialogInternet.dismiss();
            mProgressDialogInternet = null;
        }
    }

    // startShowingProgressDialog
    public static void startShowingProgressDialog() {
        mProgressDialogInternet = ProgressDialog.show(mFragmentSearch.getActivity(), "Loading...",
                "Please wait...", true);
        mProgressDialogInternet.show();
    }

}
