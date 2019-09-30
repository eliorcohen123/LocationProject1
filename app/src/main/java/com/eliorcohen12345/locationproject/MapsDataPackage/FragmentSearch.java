package com.eliorcohen12345.locationproject.MapsDataPackage;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import java.util.Objects;

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
    private SharedPreferences prefsSeek, prefsOpen, prefsPage, prefsPre, prefsTypeSearch, prefsPageMe, prefsPageMy;
    private SharedPreferences.Editor editorPage, editorPre, editorTypeSearch, editorPageMe, editorPageMy;
    private static ItemDecoration itemDecoration;
    private int myRadius, myPage, myPageMy;
    private ImageView imagePre, imageNext, imagePreFirst;
    private TextView textPage;
    private static String provider;
    private String hasPage, myStringQueryPage, myStringQueryType, myStringQueryQuery, pageTokenPre, myType,
            myTypeSearch, myOpen, myStringPageMe, myPageMeString;
    private Button btnBank, btnBar, btnBeauty, btnBooks, btnBusStation, btnCars, btnClothing, btnDoctor, btnGasStation,
            btnGym, btnJewelry, btnPark, btnRestaurant, btnSchool, btnSpa;
    private GoogleMapsApi googleMapsApi;
    private static double diagonalInches;
    private static ImageView handScrollDownImage;

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

        getData(mMapList);

        myType = prefsTypeSearch.getString("mystringtypesearch", "");
        myStringQueryQuery = prefsPage.getString("myStringQueryQuery", "");
        getCheckBtnSearch(myPage, myType, myStringQueryQuery);
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

        handScrollDownImage = mView.findViewById(R.id.handScrollDownImage);
        imagePre = mView.findViewById(R.id.imagePre);
        imageNext = mView.findViewById(R.id.imageNext);
        imagePreFirst = mView.findViewById(R.id.imagePreFirst);
        textPage = mView.findViewById(R.id.textPage);

        swipeRefreshLayout = mView.findViewById(R.id.swipe_containerFrag);

        initPrefs();

        mFragmentSearch = this;

        mMapDBHelperSearch = new MapDBHelperSearch(getActivity());
        prefsSeek = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefsOpen = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMapList = new ArrayList<>();
        googleMapsApi = new GoogleMapsApi();

        myPageMy = prefsPageMy.getInt("mystringpagemy", 1);
        if (myPageMy == 1) {
            myPage = 1;
        } else if (myPageMy == 2) {
            myPage = 2;
        } else {
            myPage = 3;
        }

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

    private void initPrefs() {
        prefsPage = Objects.requireNonNull(getContext()).getSharedPreferences("mysettingspage", Context.MODE_PRIVATE);
        prefsPre = getContext().getSharedPreferences("mysettingspre", Context.MODE_PRIVATE);
        prefsTypeSearch = getContext().getSharedPreferences("mysettingstypesearch", Context.MODE_PRIVATE);
        prefsPageMe = getContext().getSharedPreferences("mysettingspageme", Context.MODE_PRIVATE);
        prefsPageMy = getContext().getSharedPreferences("mysettingspagemy", Context.MODE_PRIVATE);

        editorPage = prefsPage.edit();
        editorPre = prefsPre.edit();
        editorTypeSearch = prefsTypeSearch.edit();
        editorPageMe = prefsPageMe.edit();
        editorPageMy = prefsPageMy.edit();
    }

    private void refreshUI() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorOrange));  // Colors of the SwipeRefreshLayout of FragmentSearch
        // Refresh the MapDBHelper of app in RecyclerView of MainActivity
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Vibration for 0.1 second
            Vibrator vibrator = (Vibrator) Objects.requireNonNull(getContext()).getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }

            Objects.requireNonNull(getActivity()).finish();
            startActivity(getActivity().getIntent());  // Refresh activity

            Toast toast = Toast.makeText(getContext(), "The list are refreshed!", Toast.LENGTH_LONG);
            View view = toast.getView();
            view.getBackground().setColorFilter(getResources().getColor(R.color.colorLightBlue), PorterDuff.Mode.SRC_IN);
            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(getResources().getColor(R.color.colorDarkBrown));
            toast.show();  // Toast

            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public static void getData(ArrayList<PlaceModel> list) {
        mMapList = list;
        if (!isConnected(Objects.requireNonNull(mFragmentSearch.getContext()))) {
            mMapList = mMapDBHelperSearch.getAllMaps();
        }
        mAdapter = new PlaceCustomAdapterSearch(ConApp.getmContext(), mMapList);
        if (mAdapter.getItemCount() != 0) {
            handScrollDownImage.setVisibility(View.GONE);
        } else {
            handScrollDownImage.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentSearch.getContext()));
        if (itemDecoration == null) {
            itemDecoration = new ItemDecoration(20);
            mRecyclerView.addItemDecoration(itemDecoration);
        }
        mAdapter.setMapsCollections();
        mRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            PlaceModel current = mMapList.get(position);

            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) mFragmentSearch.getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            float yInches = metrics.heightPixels / metrics.ydpi;
            float xInches = metrics.widthPixels / metrics.xdpi;
            diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);

            FragmentMapSearch fragmentMapSearch = new FragmentMapSearch();
            Bundle bundle = new Bundle();
            bundle.putSerializable(mFragmentSearch.getContext().getString(R.string.map_search_key), current);
            fragmentMapSearch.setArguments(bundle);
            FragmentManager fragmentManager = ((AppCompatActivity) mFragmentSearch.getContext()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (diagonalInches >= 6.5) {
                fragmentTransaction.replace(R.id.fragmentLt, fragmentMapSearch);
            } else {
                fragmentTransaction.replace(R.id.fragmentContainer, fragmentMapSearch);
            }
            fragmentTransaction.addToBackStack(null).commit();
        });
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
        builder.setPositiveButton("OK", (dialog, which) -> {

        });
        return builder;
    }

    // Sets off the menu of activity_menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu, menu);

        // SearchView of FragmentSearch
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);

        // Change colors of the searchView upper panel
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Set styles for expanded state here
                if (((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar() != null) {
                    Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Set styles for collapsed state here
                if (((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar() != null) {
                    Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                }
                return true;
            }
        });

        // Continued of SearchView of FragmentSearch
        final SearchView searchView = (SearchView) menuItem.getActionView();
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
                    prefsPageMe.edit().clear().apply();
                    prefsPageMy.edit().clear().apply();

                    startActivity(Objects.requireNonNull(getActivity()).getIntent());

                    myPage = 1;
                    getCheckBtnSearch(myPage, "", newText);

                    myPageMeString = "";

                    editorPageMe.putString("mystringpageme", myPageMeString).apply();
                    editorPageMy.putInt("mystringpagemy", myPage).apply();

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
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "", "");

                myPageMeString = "";

                editorTypeSearch.putString("mystringtypesearch", "").apply();
                editorPageMe.putString("mystringpageme", myPageMeString).apply();
                editorPageMy.putInt("mystringpagemy", myPage).apply();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        myStringQueryPage = prefsPage.getString("myStringQueryPage", "");
        myStringQueryType = prefsPage.getString("myStringQueryType", "");
        myStringQueryQuery = prefsPage.getString("myStringQueryQuery", "");
        myPageMy = prefsPageMy.getInt("mystringpagemy", 1);
        switch (v.getId()) {
            case R.id.btnBank:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "bank", "");

                myTypeSearch = "bank";
                myPageMeString = "";
                break;
            case R.id.btnBar:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "bar|night_club", "");

                myTypeSearch = "bar|night_club";
                myPageMeString = "";
                break;
            case R.id.btnBeauty:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "beauty_salon|hair_care", "");

                myTypeSearch = "beauty_salon|hair_care";
                myPageMeString = "";
                break;
            case R.id.btnBooks:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "book_store|library", "");

                myTypeSearch = "book_store|library";
                myPageMeString = "";
                break;
            case R.id.btnBusStation:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "bus_station", "");

                myTypeSearch = "bus_station";
                myPageMeString = "";
                break;
            case R.id.btnCars:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "car_dealer|car_rental|car_repair|car_wash", "");

                myTypeSearch = "car_dealer|car_rental|car_repair|car_wash";
                myPageMeString = "";
                break;
            case R.id.btnClothing:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "clothing_store", "");

                myTypeSearch = "clothing_store";
                myPageMeString = "";
                break;
            case R.id.btnDoctor:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "doctor", "");

                myTypeSearch = "doctor";
                myPageMeString = "";
                break;
            case R.id.btnGasStation:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "gas_station", "");

                myTypeSearch = "gas_station";
                myPageMeString = "";
                break;
            case R.id.btnGym:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "gym", "");

                myTypeSearch = "gym";
                myPageMeString = "";
                break;
            case R.id.btnJewelry:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "jewelry_store", "");

                myTypeSearch = "jewelry_store";
                myPageMeString = "";
                break;
            case R.id.btnPark:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "park|amusement_park|parking|rv_park", "");

                myTypeSearch = "park|amusement_park|parking|rv_park";
                myPageMeString = "";
                break;
            case R.id.btnRestaurant:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "food|restaurant|cafe|bakery", "");

                myTypeSearch = "food|restaurant|cafe|bakery";
                myPageMeString = "";
                break;
            case R.id.btnSchool:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                myPage = 1;
                getCheckBtnSearch(myPage, "school", "");

                myTypeSearch = "school";
                myPageMeString = "";
                break;
            case R.id.btnSpa:
                prefsPageMe.edit().clear().apply();
                prefsPageMy.edit().clear().apply();

                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                myPage = 1;
                getCheckBtnSearch(myPage, "spa", "");

                myTypeSearch = "spa";
                myPageMeString = "";
                break;
            case R.id.imageNext:
                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                getTypeQuery(myStringQueryPage, myStringQueryType, myStringQueryQuery);

                myPage++;

                getAllCheckPage(myPage);

                myPageMeString = myStringQueryPage;
                break;
            case R.id.imagePre:
                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                if (myPage == 2 || myPageMy == 2) {
                    pageTokenPre = "";
                } else {
                    pageTokenPre = prefsPre.getString("mystringquerypre", "");
                }

                getTypeQuery(pageTokenPre, myStringQueryType, myStringQueryQuery);

                myPage--;

                getAllCheckPage(myPage);

                myPageMeString = pageTokenPre;
                break;
            case R.id.imagePreFirst:
                startActivity(Objects.requireNonNull(getActivity()).getIntent());

                getTypeQuery("", myStringQueryType, myStringQueryQuery);

                myPage = 1;

                getAllCheckPage(myPage);

                myPageMeString = "";
                break;
        }
        editorTypeSearch.putString("mystringtypesearch", myTypeSearch).apply();
        editorPageMe.putString("mystringpageme", myPageMeString).apply();
        editorPageMy.putInt("mystringpagemy", myPage).apply();
    }

    private void getCheckBtnSearch(int page, String type, String query) {
        myStringPageMe = prefsPageMe.getString("mystringpageme", "");
        getTypeQuery(myStringPageMe, type, query);
        getAllCheckPage(page);
    }

    private void getAllCheckPage(int page) {
        getPage0(page);
        getPage1(page);
        getPageText();
    }

    private void getPage0(int page) {
        myPageMy = prefsPageMy.getInt("mystringpagemy", 1);
        if (page == 0 || myPageMy <= 0) {
            myPage = 1;
        } else {
            imagePre.setVisibility(View.VISIBLE);
        }
        editorPageMy.putInt("mystringpagemy", myPage).apply();
    }

    private void getPage1(int page) {
        myPageMy = prefsPageMy.getInt("mystringpagemy", 1);
        if (page == 1 || myPageMy == 1) {
            imagePre.setVisibility(View.GONE);
        } else {
            imagePre.setVisibility(View.VISIBLE);
        }

        if (page > 2 || myPageMy > 2) {
            imagePreFirst.setVisibility(View.VISIBLE);
        } else {
            imagePreFirst.setVisibility(View.GONE);
        }
    }

    private void getPageText() {
        myPageMy = prefsPageMy.getInt("mystringpagemy", 1);
        textPage.setText(String.valueOf(myPageMy));
    }

    private void getTypeQuery(String pageToken, String type, String query) {
        if (!isConnected(Objects.requireNonNull(getContext()))) {
            mMapList = mMapDBHelperSearch.getAllMaps();
            mAdapter = new PlaceCustomAdapterSearch(getActivity(), mMapList);
            mAdapter.setMapsCollections();
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
                                googleMapsApi.getStringGoogleMapsApi(location.getLatitude(), location.getLongitude(), myRadius, pageToken, myOpen, type, query, getString(R.string.api_key_search)), response -> {
                            try {
                                JSONObject mainObj = new JSONObject(response);
                                if (mainObj.has("next_page_token")) {
                                    imageNext.setVisibility(View.VISIBLE);
                                    hasPage = mainObj.getString("next_page_token");
                                } else {
                                    imageNext.setVisibility(View.GONE);
                                    hasPage = "";
                                }
                                editorPage.putString("myStringQueryPage", hasPage).apply();

                                myPageMy = prefsPageMy.getInt("mystringpagemy", 1);
                                if (myPageMy == 1) {
                                    editorPre.putString("mystringquerypre", hasPage).apply();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, error -> {

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

    private static class ItemClickSupport {

        private final RecyclerView mRecyclerView;
        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    // ask the RecyclerView for the viewHolder of this view.
                    // then use it to get the position for the adapter
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
            }
        };

        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
                return false;
            }
        };

        private RecyclerView.OnChildAttachStateChangeListener mAttachListener = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                // every time a new child view is attached add click listeners to it
                if (mOnItemClickListener != null) {
                    view.setOnClickListener(mOnClickListener);
                }
                if (mOnItemLongClickListener != null) {
                    view.setOnLongClickListener(mOnLongClickListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        };

        private ItemClickSupport(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            // the ID must be declared in XML, used to avoid
            // replacing the ItemClickSupport without removing
            // the old one from the RecyclerView
            mRecyclerView.setTag(R.id.item_click_support, this);
            mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
        }

        private static ItemClickSupport addTo(RecyclerView view) {
            // if there's already an ItemClickSupport attached
            // to this RecyclerView do not replace it, use it
            ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new ItemClickSupport(view);
            }
            return support;
        }

        private static ItemClickSupport removeFrom(RecyclerView view) {
            ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        private ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        private ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
            mOnItemLongClickListener = listener;
            return this;
        }

        private void detach(RecyclerView view) {
            view.removeOnChildAttachStateChangeListener(mAttachListener);
            view.setTag(R.id.item_click_support, null);
        }

        private interface OnItemClickListener {

            void onItemClicked(RecyclerView recyclerView, int position, View v);
        }

        private interface OnItemLongClickListener {

            boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
        }

    }

}
