package com.eliorcohen12345.locationproject.PagesPackage;

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
import com.eliorcohen12345.locationproject.AsyncTasksPackage.GetMapsAsyncTaskSearch;
import com.eliorcohen12345.locationproject.CustomAdaptersPackage.CustomAdapterSearch;
import com.eliorcohen12345.locationproject.ModelsPackage.Results;
import com.eliorcohen12345.locationproject.ViewModelsPackage.PlaceViewModelSearchDB;
import com.eliorcohen12345.locationproject.OthersPackage.ConApp;
import com.eliorcohen12345.locationproject.OthersPackage.ItemDecoration;
import com.eliorcohen12345.locationproject.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import eliorcohen.com.googlemapsapi.GoogleMapsApi;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private static ArrayList<Results> mMapList;
    private static CustomAdapterSearch mAdapter;
    private static RecyclerView mRecyclerView;
    private static PlaceViewModelSearchDB placeViewModelSearchDB;
    private static ProgressDialog mProgressDialogInternet;
    private static SearchFragment mSearchFragment;
    private GetMapsAsyncTaskSearch mGetMapsAsyncTaskSearch;
    private View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static Location location;
    private static LocationManager locationManager;
    private Criteria criteria;
    private SharedPreferences prefsSeek, prefsOpen, prefsQuery, prefsPage, prefsPre, prefsType, prefsPageMe, prefsPageMy;
    private SharedPreferences.Editor editorQuery, editorPage, editorPre, editorType, editorPageMe, editorPageMy;
    private static ItemDecoration itemDecoration;
    private int myRadius, myPage, myPageMy;
    private ImageView imagePre, imageNext, imagePreFirst;
    private TextView textPage;
    private static String provider;
    private String hasPage, myStringPage, myStringQuery, pageTokenPre, myType, myTypeSearch, myOpen, myStringPageMe, myPageMeString, myQuery;
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
        getData(mMapList);
        getCheckBtnSearch(myPage, myType, myStringQuery);

        return mView;
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

        mSearchFragment = this;

        placeViewModelSearchDB = new PlaceViewModelSearchDB(ConApp.getApplication());
        mMapList = new ArrayList<>();
        googleMapsApi = new GoogleMapsApi();
        itemDecoration = null;

        myType = prefsType.getString("mystringtypesearch", "");
        myStringQuery = prefsQuery.getString("mystringquerysearch", "");
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
        prefsSeek = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefsOpen = PreferenceManager.getDefaultSharedPreferences(getContext());

        prefsQuery = requireContext().getSharedPreferences("mysettingsquery", Context.MODE_PRIVATE);
        prefsPage = requireContext().getSharedPreferences("mysettingspage", Context.MODE_PRIVATE);
        prefsPre = getContext().getSharedPreferences("mysettingspre", Context.MODE_PRIVATE);
        prefsType = getContext().getSharedPreferences("mysettingstype", Context.MODE_PRIVATE);
        prefsPageMe = getContext().getSharedPreferences("mysettingspageme", Context.MODE_PRIVATE);
        prefsPageMy = getContext().getSharedPreferences("mysettingspagemy", Context.MODE_PRIVATE);

        editorQuery = prefsQuery.edit();
        editorPage = prefsPage.edit();
        editorPre = prefsPre.edit();
        editorType = prefsType.edit();
        editorPageMe = prefsPageMe.edit();
        editorPageMy = prefsPageMy.edit();
    }

    private void refreshUI() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorOrange));  // Colors of the SwipeRefreshLayout of FragmentSearch
        // Refresh the MapDBHelper of app in RecyclerView of MainActivity
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Vibration for 0.1 second
            Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }

            requireActivity().finish();
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

    public static void getData(ArrayList<Results> list) {
        mMapList = list;
        if (!isConnected(mSearchFragment.requireContext())) {
            mMapList = placeViewModelSearchDB.getAllPlaces();
        }
        mAdapter = new CustomAdapterSearch(ConApp.getApplication(), mMapList);
        if (mAdapter.getItemCount() != 0) {
            handScrollDownImage.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            handScrollDownImage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mSearchFragment.getContext()));
        if (itemDecoration == null) {
            itemDecoration = new ItemDecoration(20);
            mRecyclerView.addItemDecoration(itemDecoration);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mAdapter.setMapsCollections();
        mRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            Results current = mMapList.get(position);

            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) mSearchFragment.getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            float yInches = metrics.heightPixels / metrics.ydpi;
            float xInches = metrics.widthPixels / metrics.xdpi;
            diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);

            MapSearchFragment mapSearchFragment = new MapSearchFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(mSearchFragment.getContext().getString(R.string.map_search_key), current);
            mapSearchFragment.setArguments(bundle);
            FragmentManager fragmentManager = ((AppCompatActivity) mSearchFragment.getContext()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (diagonalInches >= 6.5) {
                fragmentTransaction.replace(R.id.fragmentLt, mapSearchFragment);
            } else {
                fragmentTransaction.replace(R.id.fragmentContainer, mapSearchFragment);
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
            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
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

    // Sets off the menu of menu_main
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        // SearchView of FragmentSearch
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);

        // Change colors of the searchView upper panel
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Set styles for expanded state here
                if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                    Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Set styles for collapsed state here
                if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
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
                    getConfigureSearchNearby(newText);

                    stopShowingProgressDialog();
                    return true;
                }
            });
        }
    }

    // Options in the menu_main
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            case R.id.nearByMe:
                getConfigureSearchNearby("");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        myStringQuery = prefsQuery.getString("mystringquerysearch", "");
        myType = prefsType.getString("mystringtypesearch", "");
        myPageMy = prefsPageMy.getInt("mystringpagemy", 1);
        switch (v.getId()) {
            case R.id.btnBank:
                getConfigureBtn("bank");
                break;
            case R.id.btnBar:
                getConfigureBtn("bar|night_club");
                break;
            case R.id.btnBeauty:
                getConfigureBtn("beauty_salon|hair_care");
                break;
            case R.id.btnBooks:
                getConfigureBtn("book_store|library");
                break;
            case R.id.btnBusStation:
                getConfigureBtn("bus_station");
                break;
            case R.id.btnCars:
                getConfigureBtn("car_dealer|car_rental|car_repair|car_wash");
                break;
            case R.id.btnClothing:
                getConfigureBtn("clothing_store");
                break;
            case R.id.btnDoctor:
                getConfigureBtn("doctor");
                break;
            case R.id.btnGasStation:
                getConfigureBtn("gas_station");
                break;
            case R.id.btnGym:
                getConfigureBtn("gym");
                break;
            case R.id.btnJewelry:
                getConfigureBtn("jewelry_store");
                break;
            case R.id.btnPark:
                getConfigureBtn("park|amusement_park|parking|rv_park");
                break;
            case R.id.btnRestaurant:
                getConfigureBtn("food|restaurant|cafe|bakery");
                break;
            case R.id.btnSchool:
                getConfigureBtn("school");
                break;
            case R.id.btnSpa:
                getConfigureBtn("spa");
                break;
            case R.id.imageNext:
                myStringPage = prefsPage.getString("myStringPage", "");

                getTypeQuery(myStringPage, myType, myStringQuery);

                myPage++;

                getAllCheckPage(myPage);

                myPageMeString = myStringPage;
                break;
            case R.id.imagePre:
                if (myPage == 2 || myPageMy == 2) {
                    pageTokenPre = "";
                } else {
                    pageTokenPre = prefsPre.getString("mystringquerypre", "");
                }

                getTypeQuery(pageTokenPre, myType, myStringQuery);

                myPage--;

                getAllCheckPage(myPage);

                myPageMeString = pageTokenPre;
                break;
            case R.id.imagePreFirst:
                getTypeQuery("", myType, myStringQuery);

                myPage = 1;

                getAllCheckPage(myPage);

                myPageMeString = "";
                break;
        }

        editorType.putString("mystringtypesearch", myTypeSearch).apply();
        editorQuery.putString("mystringquerysearch", myQuery).apply();
        editorPageMe.putString("mystringpageme", myPageMeString).apply();
        editorPageMy.putInt("mystringpagemy", myPage).apply();
    }

    private void getConfigureSearchNearby(String query) {
        prefsPageMe.edit().clear().apply();
        prefsPageMy.edit().clear().apply();

        myPage = 1;
        getCheckBtnSearch(myPage, "", query);

        myPageMeString = "";

        editorType.putString("mystringtypesearch", "").apply();
        editorQuery.putString("mystringquerysearch", query).apply();
        editorPageMe.putString("mystringpageme", myPageMeString).apply();
        editorPageMy.putInt("mystringpagemy", myPage).apply();
    }

    private void getConfigureBtn(String type) {
        prefsPageMe.edit().clear().apply();
        prefsPageMy.edit().clear().apply();

        myPage = 1;
        getCheckBtnSearch(myPage, type, "");

        myTypeSearch = type;
        myPageMeString = "";
        myQuery = "";
    }

    private void getCheckBtnSearch(int page, String type, String query) {
        myStringPageMe = prefsPageMe.getString("mystringpageme", "");
        getTypeQuery(myStringPageMe, type, query);
        getAllCheckPage(page);
    }

    private void getAllCheckPage(int page) {
        getPage(page);
        getPageText();
    }

    private void getPage(int page) {
        myPageMy = prefsPageMy.getInt("mystringpagemy", 1);

        if (page <= 0 || myPageMy <= 0) {
            myPage = 1;
        } else {
            imagePre.setVisibility(View.VISIBLE);
        }

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

        editorPageMy.putInt("mystringpagemy", myPage).apply();
    }

    private void getPageText() {
        myPageMy = prefsPageMy.getInt("mystringpagemy", 1);
        textPage.setText(String.valueOf(myPageMy));
    }

    private void getTypeQuery(String pageToken, String type, String query) {
        if (!isConnected(requireContext())) {
            // Put AsyncTask in the RecyclerView of fragmentSearch to execute the SQLiteHelper
            mMapList = placeViewModelSearchDB.getAllPlaces();

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
                        placeViewModelSearchDB.deleteAll();
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
                                editorPage.putString("myStringPage", hasPage).apply();

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
        mProgressDialogInternet = ProgressDialog.show(mSearchFragment.getActivity(), "Loading...",
                "Please wait...", true);
        mProgressDialogInternet.show();
    }

    private static class ItemClickSupport {

        private final RecyclerView mRecyclerView;
        private OnItemClickListener mOnItemClickListener;
        private static OnItemLongClickListener mOnItemLongClickListener;

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
            public void onChildViewAttachedToWindow(@NotNull View view) {
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

        private ItemClickSupport(RecyclerView recyclerView, OnItemLongClickListener mOnItemLongClickListener) {
            mRecyclerView = recyclerView;
            ItemClickSupport.mOnItemLongClickListener = mOnItemLongClickListener;
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
                support = new ItemClickSupport(view, mOnItemLongClickListener);
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

        private void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
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
