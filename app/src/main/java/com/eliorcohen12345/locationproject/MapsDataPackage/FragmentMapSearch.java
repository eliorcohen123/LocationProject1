package com.eliorcohen12345.locationproject.MapsDataPackage;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.eliorcohen12345.locationproject.MainAndOtherPackage.ConApp;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eliorcohen12345.locationproject.AsyncTaskPackage.GetMapsAsyncTaskHistory;
import com.eliorcohen12345.locationproject.AsyncTaskPackage.GetMapsAsyncTaskSearch;
import com.eliorcohen12345.locationproject.CustomAdapterPackage.CustomInfoWindowGoogleMapSearch;
import com.eliorcohen12345.locationproject.DataAppPackage.MapDBHelperSearch;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

public class FragmentMapSearch extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private PlaceModel placeModelSearch;
    private Marker markerSearch, markerAllSearch[];
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private ImageView moovit, gett, waze, num1, num2, num3, num4, num5, btnOpenList;
    private GetMapsAsyncTaskSearch mGetMapsAsyncTaskSearch;
    private GetMapsAsyncTaskHistory mGetMapsAsyncTaskHistory;
    private static MapDBHelperSearch mMapDBHelperSearch;
    private static ArrayList<PlaceModel> mMapList;
    private List<Marker> markers = new ArrayList<Marker>();
    private RecyclerView mRecyclerView;  // RecyclerView of FragmentMapSearch
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout linearList;
    private boolean isClicked;
    private AlphaAnimation anim;
    private double diagonalInches;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_layout_search, container, false);

        initUI();
        initListeners();
        initLocation();
        mapShow();
        getData();

        return mView;
    }

    private void initUI() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            placeModelSearch = (PlaceModel) bundle.getSerializable(getString(R.string.map_search_key));
        }

        coordinatorLayout = mView.findViewById(R.id.myContent);

        num1 = mView.findViewById(R.id.imageMe1);
        num2 = mView.findViewById(R.id.imageMe2);
        num3 = mView.findViewById(R.id.imageMe3);
        num4 = mView.findViewById(R.id.imageMe4);
        num5 = mView.findViewById(R.id.imageMe5);
        btnOpenList = mView.findViewById(R.id.btnOpenList);

        moovit = mView.findViewById(R.id.imageViewMoovit);
        gett = mView.findViewById(R.id.imageViewGett);
        waze = mView.findViewById(R.id.imageViewWaze);

        linearList = mView.findViewById(R.id.listAll);

        linearList.setVisibility(View.GONE);

        mMapDBHelperSearch = new MapDBHelperSearch(getActivity());

        isClicked = true;
    }

    private void initListeners() {
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        btnOpenList.setOnClickListener(this);
    }

    private void initLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
    }

    private void mapShow() {
        Snackbar.make(coordinatorLayout, R.string.item_removed_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    // Respond to the click, such as by undoing the modification that caused
                    // this message to be displayed
                })
                .show();
    }

    private void getData() {
        try {
            if (!isConnected(getContext())) {
                mMapList = mMapDBHelperSearch.getAllMaps();
                // Put AsyncTask in the RecyclerView of MainActivity to execute the SQLiteHelper
                mRecyclerView = new RecyclerView(getContext());
                mGetMapsAsyncTaskHistory = new GetMapsAsyncTaskHistory(mRecyclerView);
                mGetMapsAsyncTaskHistory.execute(mMapDBHelperSearch);
                // Tablet/Phone mode
                DisplayMetrics metrics = new DisplayMetrics();
                ((WindowManager) ConApp.getmContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

                float yInches = metrics.heightPixels / metrics.ydpi;
                float xInches = metrics.widthPixels / metrics.xdpi;
                diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
                if (diagonalInches < 6.5) {
                    buildDialog(getContext()).show();
                }
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
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                        int myRadius = prefs.getInt("seek", 5000);
                        mMapList = mMapDBHelperSearch.getAllMaps();
                        mMapDBHelperSearch.deleteData();
                        String myQuery = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                                location.getLatitude() + "," + location.getLongitude() +
                                "&radius=" + myRadius + "&sensor=true&rankby=prominence&types=&keyword=&key=" +
                                getString(R.string.api_key_search);
                        mGetMapsAsyncTaskSearch = new GetMapsAsyncTaskSearch();
                        mGetMapsAsyncTaskSearch.execute(myQuery);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = view.findViewById(R.id.mapSearch);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            MapsInitializer.initialize(getContext());
            mGoogleMap = googleMap;
            addMarkerSearch();
            addCircleNearBy();
            addCircleSearch();
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            if (provider != null) {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 8));
                }
            }
        } catch (Exception e) {

        }
    }

    private void addMarkerSearch() {
        try {
            for (int i = 0; i <= mMapList.size(); i++) {
                if (placeModelSearch != null) {
                    markerSearch = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(placeModelSearch.getLat(), placeModelSearch.getLng())).title(placeModelSearch.getName()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    markers.add(markerSearch);
                }

                try {
                    markerAllSearch[i] = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mMapList.get(i).getLat(), mMapList.get(i).getLng())).title(mMapList.get(i).getName()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    if (!markerAllSearch[i].getTitle().equals(markerSearch.getTitle())) {
                        markers.add(markerAllSearch[i]);
                    }
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }

        mGoogleMap.setOnMarkerClickListener(marker -> {
            try {
                for (int finalI = 0; finalI <= mMapList.size(); finalI++) {
                    final int finalI1 = finalI;
                    if (marker.getTitle().equals(mMapList.get(finalI1).getName())) {
                        try {
                            getMoovit(mMapList.get(finalI1).getLat(), mMapList.get(finalI1).getLng(), mMapList.get(finalI1).getName(), location.getLatitude(), location.getLongitude());
                            getGetTaxi(mMapList.get(finalI1).getLat(), mMapList.get(finalI1).getLng());
                            getWaze(mMapList.get(finalI1).getLat(), mMapList.get(finalI1).getLng());

                            getNavigation(mMapList.get(finalI1).getLat(), mMapList.get(finalI1).getLng(), mMapList.get(finalI1).getName(), mMapList.get(finalI1).getVicinity(), mMapList.get(finalI1).getRating(), mMapList.get(finalI1).getUser_ratings_total(), marker);
                        } catch (Exception e) {

                        }
                        break;
                    } else if (marker.equals(markerSearch)) {
                        try {
                            getMoovit(placeModelSearch.getLat(), placeModelSearch.getLng(), placeModelSearch.getName(), location.getLatitude(), location.getLongitude());
                            getGetTaxi(placeModelSearch.getLat(), placeModelSearch.getLng());
                            getWaze(placeModelSearch.getLat(), placeModelSearch.getLng());

                            getNavigation(placeModelSearch.getLat(), placeModelSearch.getLng(), placeModelSearch.getName(), placeModelSearch.getVicinity(), placeModelSearch.getRating(), placeModelSearch.getUser_ratings_total(), marker);
                        } catch (Exception e) {

                        }
                    }
                }
            } catch (Exception e) {

            }
            return false;
        });
    }

    // Add circle of NearBy
    private void addCircleNearBy() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        if (provider != null) {
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                double myRadius = prefs.getInt("seek", 5000);
                SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(getContext());
                String result = prefs2.getString("myKm", "1000.0");
                assert result != null;
                double val = Double.parseDouble(result);
                if (val == 1000.0) {
                    double distanceKm = myRadius / val;
                    String radiusMeKm = String.format("%.2f", distanceKm);
                    if (myRadius >= 1000.0) {
                        TextView disNearBy = mView.findViewById(R.id.disNearBy);
                        disNearBy.setText(" = " + radiusMeKm + " KM R - Nearby");
                    } else {
                        double distanceMeters = distanceKm * 1000;
                        TextView disNearBy = mView.findViewById(R.id.disNearBy);
                        disNearBy.setText(" = " + (int) distanceMeters + " Meters R - Nearby");
                    }
                } else if (val == 1609.344) {
                    double distanceMile = myRadius / val;
                    String radiusMeMile = String.format("%.2f", distanceMile);
                    TextView disNearBy = mView.findViewById(R.id.disNearBy);
                    disNearBy.setText(" = " + radiusMeMile + " Miles R - Nearby");
                }
                mGoogleMap.addCircle(new CircleOptions()
                        .center(new LatLng(location.getLatitude(), location.getLongitude()))
                        .radius(myRadius)
                        .strokeColor(Color.rgb(153, 153, 102))
                        .fillColor(0x20FF0000)
                        .strokeWidth(3)
                );
            }
        }
    }

    // Add Circle of Search
    private void addCircleSearch() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        if (provider != null) {
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String result = prefs.getString("myKm", "1000.0");
                assert result != null;
                double val = Double.parseDouble(result);
                if (val == 1000.0) {
                    TextView disNearBy = mView.findViewById(R.id.disSearch);
                    disNearBy.setText(" = " + 50.00 + " KM R - Search");
                } else if (val == 1609.344) {
                    TextView disNearBy = mView.findViewById(R.id.disSearch);
                    disNearBy.setText(" = " + 31.06 + " Miles R - Search");
                }
                mGoogleMap.addCircle(new CircleOptions()
                        .center(new LatLng(location.getLatitude(), location.getLongitude()))
                        .radius(50000)
                        .strokeColor(Color.rgb(232, 232, 53))
                        .fillColor(0x200000FF)
                        .strokeWidth(3)
                );
            }
        }
    }

    // Check network
    private boolean isConnected(Context context) {
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

    private void getMoovit(final double des_lat, final double des_lng, final String name, final double orig_lat, final double orig_lng) {
        moovit.setOnClickListener(v -> {
            try {
                PackageManager pm = getActivity().getPackageManager();
                pm.getPackageInfo("com.tranzmate", PackageManager.GET_ACTIVITIES);
                String uri = "moovit://directions?dest_lat=" + des_lat + "&dest_lon=" + des_lng + "&dest_name=" + name + "&orig_lat=" + orig_lat + "&orig_lon=" + orig_lng + "&orig_name=Your current location&auto_run=true&partner_id=Lovely Favorites Places";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            } catch (PackageManager.NameNotFoundException e) {
                String url = "http://app.appsflyer.com/com.tranzmate?pid=DL&c=Lovely Favorites Places";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void getGetTaxi(final double des_lat, final double des_lng) {
        gett.setOnClickListener(v -> {
            if (isPackageInstalled(getContext(), "com.gettaxi.android")) {
                openLinkGetTaxi(getActivity(), "gett://order?pickup=my_location&dropoff_latitude=" + des_lat + "&dropoff_longitude=" + des_lng + "&product_id=0c1202f8-6c43-4330-9d8a-3b4fa66505fd");
            } else {
                openLinkGetTaxi(getActivity(), "https://play.google.com/store/apps/details?id=" + "com.gettaxi.android");
            }
        });
    }

    private static void openLinkGetTaxi(Activity activity, String link) {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playStoreIntent.setData(Uri.parse(link));
        activity.startActivity(playStoreIntent);
    }

    private static boolean isPackageInstalled(Context context, String packageId) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    private void getWaze(final double des_lat, final double des_lng) {
        waze.setOnClickListener(v -> {
            try {
                String url = "https://www.waze.com/ul?ll=" + des_lat + "%2C" + des_lng + "&navigate=yes&zoom=17";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                startActivity(intent);
            }
        });
    }

    private void getNavigation(double getLat, double getLng, String getName, String getVicinity, double getRating, int getTotalRating, Marker marker) {
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
            if (location != null) {
                PlaceModel info = new PlaceModel();
                double distanceMe;
                Location locationA = new Location("Point A");
                locationA.setLatitude(getLat);
                locationA.setLongitude(getLng);
                Location locationB = new Location("Point B");
                locationB.setLatitude(location.getLatitude());
                locationB.setLongitude(location.getLongitude());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String result = prefs.getString("myKm", "1000.0");
                double val = Double.parseDouble(result);
                distanceMe = locationA.distanceTo(locationB) / val;

                String distanceKm1;
                String disMile;
                if (val == 1000.0) {
                    if (distanceMe < 1) {
                        int dis = (int) (distanceMe * 1000);
                        distanceKm1 = "\n" + "Meters: " + String.valueOf(dis);
                        info.setName(getName);
                        info.setVicinity(getVicinity);
                        info.setRating(getRating);
                        info.setUser_ratings_total(getTotalRating);
                        info.setDistance(distanceKm1);
                    } else if (distanceMe >= 1) {
                        String disM = String.format("%.2f", distanceMe);
                        distanceKm1 = "\n" + "Km: " + String.valueOf(disM);
                        info.setName(getName);
                        info.setVicinity(getVicinity);
                        info.setRating(getRating);
                        info.setUser_ratings_total(getTotalRating);
                        info.setDistance(distanceKm1);
                    }
                } else if (val == 1609.344) {
                    String distanceMile1 = String.format("%.2f", distanceMe);
                    disMile = "\n" + "Miles: " + String.valueOf(distanceMile1);
                    info.setName(getName);
                    info.setVicinity(getVicinity);
                    info.setRating(getRating);
                    info.setUser_ratings_total(getTotalRating);
                    info.setDistance(disMile);
                }

                CustomInfoWindowGoogleMapSearch customInfoWindow = new CustomInfoWindowGoogleMapSearch(getActivity());
                mGoogleMap.setInfoWindowAdapter(customInfoWindow);

                marker.setTag(info);
                marker.showInfoWindow();
            }
        }

        Toast.makeText(getContext(), getName, Toast.LENGTH_LONG).show();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        if (provider != null) {
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String lat1 = String.valueOf(lat);
                String lng1 = String.valueOf(lng);

                //Define list to get all latLng for the route
                ArrayList<LatLng> path = new ArrayList<LatLng>();

                //Execute Directions API request
                GeoApiContext context = new GeoApiContext.Builder()
                        .apiKey(getString(R.string.api_key_search))
                        .build();
                DirectionsApiRequest req = DirectionsApi.getDirections(context, lat1 + ", " + lng1, getLat + ", " + getLng);
                try {
                    DirectionsResult res = req.await();

                    //Loop through legs and steps to get encoded polyLines of each step
                    if (res.routes != null && res.routes.length > 0) {
                        DirectionsRoute route = res.routes[0];

                        if (route.legs != null) {
                            for (int i = 0; i < route.legs.length; i++) {
                                DirectionsLeg leg = route.legs[i];
                                if (leg.steps != null) {
                                    for (int j = 0; j < leg.steps.length; j++) {
                                        DirectionsStep step = leg.steps[j];
                                        if (step.steps != null && step.steps.length > 0) {
                                            for (int k = 0; k < step.steps.length; k++) {
                                                DirectionsStep step1 = step.steps[k];
                                                EncodedPolyline points1 = step1.polyline;
                                                if (points1 != null) {
                                                    //Decode polyline and add points to list of route coordinates
                                                    List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                    for (com.google.maps.model.LatLng coord1 : coords1) {
                                                        path.add(new LatLng(coord1.lat, coord1.lng));
                                                    }
                                                }
                                            }
                                        } else {
                                            EncodedPolyline points = step.polyline;
                                            if (points != null) {
                                                //Decode polyline and add points to list of route coordinates
                                                List<com.google.maps.model.LatLng> coords = points.decodePath();
                                                for (com.google.maps.model.LatLng coord : coords) {
                                                    path.add(new LatLng(coord.lat, coord.lng));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                //Draw the polyline
                if (path.size() > 0) {
                    PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.rgb(255, 255, 204)).width(5);
                    mGoogleMap.addPolyline(opts);
                }
            }
        }
    }

    private void setFadeAnimationTrue(LinearLayout linearList) {
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1500);
        linearList.startAnimation(anim);
    }

    private void setFadeAnimationFalse(LinearLayout linearList) {
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1500);
        linearList.startAnimation(anim);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageMe1:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.imageMe2:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.imageMe3:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.imageMe4:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.imageMe5:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.btnOpenList:
                if (isClicked) {
                    linearList.setVisibility(View.VISIBLE);
                    setFadeAnimationTrue(linearList);
                    isClicked = false;
                } else {
                    linearList.setVisibility(View.GONE);
                    setFadeAnimationFalse(linearList);
                    isClicked = true;
                }
                break;
        }
    }

}
