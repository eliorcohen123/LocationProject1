package com.eliorcohen12345.locationproject.PagesPackage;

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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.eliorcohen12345.locationproject.DataAppPackage.PlaceViewModelFavorites;
import com.eliorcohen12345.locationproject.OthersPackage.ConApp;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eliorcohen12345.locationproject.CustomAdaptersPackage.CustomInfoWindowGoogleMapFavorites;
import com.eliorcohen12345.locationproject.ModelsPackage.Results;
import com.eliorcohen12345.locationproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Objects;

public class MapFavoritesFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private Results resultsFavorites;
    private Marker markerFavorites, markerAllFavorites[];
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private ImageView moovit, gett, waze, num1, num2, num3, num4, num5, btnOpenList;
    private TextView textGeo;
    private PlaceViewModelFavorites placeViewModelFavorites;
    private ArrayList<Results> mMapList = new ArrayList<>();  // ArrayList of PlaceModel
    private List<Marker> markers;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout linearList;
    private boolean isClicked;
    private AlphaAnimation anim;
    private SharedPreferences prefsSeekGeo;
    private int myRadiusGeo;
    private static LinearLayout linearLayoutYes, linearLayoutNo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_layout_favorites, container, false);

        initUI();
        initListeners();
        initLocation();
        setButtonsEnabledState();
        mapShow();
        getData();

        return mView;
    }

    private void initUI() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            resultsFavorites = (Results) bundle.getSerializable(getString(R.string.map_favorites_key));
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

        textGeo = mView.findViewById(R.id.textGeo);

        linearLayoutYes = mView.findViewById(R.id.linYes);
        linearLayoutNo = mView.findViewById(R.id.linNo);

        linearList.setVisibility(View.GONE);

        markers = new ArrayList<Marker>();
        placeViewModelFavorites = new PlaceViewModelFavorites(ConApp.getApplication());
        prefsSeekGeo = PreferenceManager.getDefaultSharedPreferences(getContext());
        myRadiusGeo = prefsSeekGeo.getInt("seek_geo", 500);
        textGeo.setText("Geofence? (" + myRadiusGeo + "m)");

        isClicked = true;

        setHasOptionsMenu(true);
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
        locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
    }

    private void mapShow() {
        Snackbar.make(coordinatorLayout, R.string.item_removed_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Respond to the click, such as by undoing the modification that caused
                        // this message to be displayed
                    }
                })
                .show();
    }

    private void getData() {
        try {
            mMapList = placeViewModelFavorites.getAllPlaces();  // Put the getAllMaps of SQLiteHelper in the ArrayList of FragmentFavorites
        } catch (Exception e) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = view.findViewById(R.id.mapFavorites);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            MapsInitializer.initialize(Objects.requireNonNull(getContext()));
            mGoogleMap = googleMap;
            addMarkerFavorites();
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
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
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 8));
                }
            }
        } catch (Exception e) {

        }
    }

    // Add marker
    private void addMarkerFavorites() {
        try {
            for (int i = 0; i <= mMapList.size(); i++) {
                if (resultsFavorites != null) {
                    markerFavorites = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(resultsFavorites.getLat(), resultsFavorites.getLng())).title(resultsFavorites.getName()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    markers.add(markerFavorites);
                }

                try {
                    markerAllFavorites[i] = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mMapList.get(i).getLat(), mMapList.get(i).getLng())).title(mMapList.get(i).getName()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    if (!markerAllFavorites[i].getTitle().equals(markerFavorites.getTitle())) {
                        markers.add(markerAllFavorites[i]);
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

                            getNavigation(mMapList.get(finalI1).getLat(), mMapList.get(finalI1).getLng(), mMapList.get(finalI1).getName(), mMapList.get(finalI1).getVicinity(), marker);
                        } catch (Exception e) {

                        }
                        break;
                    } else if (marker.equals(markerFavorites)) {
                        try {
                            getMoovit(resultsFavorites.getLat(), resultsFavorites.getLng(), resultsFavorites.getName(), location.getLatitude(), location.getLongitude());
                            getGetTaxi(resultsFavorites.getLat(), resultsFavorites.getLng());
                            getWaze(resultsFavorites.getLat(), resultsFavorites.getLng());

                            getNavigation(resultsFavorites.getLat(), resultsFavorites.getLng(), resultsFavorites.getName(), resultsFavorites.getVicinity(), marker);
                        } catch (Exception e) {

                        }
                    }
                }
            } catch (Exception e) {

            }
            return false;
        });
    }

    // Sets off the menu of menu_favorites
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Options in the menu_favorites
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backMapFavorites:
                Objects.requireNonNull(getActivity()).onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMoovit(final double des_lat, final double des_lng, final String name, final double orig_lat, final double orig_lng) {
        moovit.setOnClickListener(v -> {
            try {
                PackageManager pm = Objects.requireNonNull(getActivity()).getPackageManager();
                pm.getPackageInfo("com.tranzmate", PackageManager.GET_ACTIVITIES);
                String uri = "moovit://directions?dest_lat=" + des_lat + "&dest_lon=" + des_lng + "&dest_name=" + name + "&orig_lat=" + orig_lat + "&orig_lon=" + orig_lng + "&orig_name=Your current location&auto_run=true&partner_id=Lovely Favorite Places";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            } catch (PackageManager.NameNotFoundException e) {
                String url = "http://app.appsflyer.com/com.tranzmate?pid=DL&c=Lovely Favorite Places";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void getGetTaxi(final double des_lat, final double des_lng) {
        gett.setOnClickListener(v -> {
            if (isPackageInstalled(Objects.requireNonNull(getContext()))) {
                openLinkGetTaxi(Objects.requireNonNull(getActivity()), "gett://order?pickup=my_location&dropoff_latitude=" + des_lat + "&dropoff_longitude=" + des_lng + "&product_id=0c1202f8-6c43-4330-9d8a-3b4fa66505fd");
            } else {
                openLinkGetTaxi(Objects.requireNonNull(getActivity()), "https://play.google.com/store/apps/details?id=" + "com.gettaxi.android");
            }
        });
    }

    private static void openLinkGetTaxi(Activity activity, String link) {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playStoreIntent.setData(Uri.parse(link));
        activity.startActivity(playStoreIntent);
    }

    private static boolean isPackageInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.gettaxi.android", PackageManager.GET_ACTIVITIES);
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

    private void getNavigation(double getLat, double getLng, String getName, String getVicinity, Marker marker) {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Results info = new Results();
                double distanceMe;
                Location locationA = new Location("Point A");
                locationA.setLatitude(getLat);
                locationA.setLongitude(getLng);
                Location locationB = new Location("Point B");
                locationB.setLatitude(location.getLatitude());
                locationB.setLongitude(location.getLongitude());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String result = prefs.getString("myKm", "1000.0");
                assert result != null;
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
                        info.setDistance(distanceKm1);
                    } else if (distanceMe >= 1) {
                        String disM = String.format("%.2f", distanceMe);
                        distanceKm1 = "\n" + "Km: " + String.valueOf(disM);
                        info.setName(getName);
                        info.setVicinity(getVicinity);
                        info.setDistance(distanceKm1);
                    }
                } else if (val == 1609.344) {
                    String distanceMile1 = String.format("%.2f", distanceMe);
                    disMile = "\n" + "Miles: " + String.valueOf(distanceMile1);
                    info.setName(getName);
                    info.setVicinity(getVicinity);
                    info.setDistance(disMile);
                }

                CustomInfoWindowGoogleMapFavorites customInfoWindow = new CustomInfoWindowGoogleMapFavorites(getActivity());
                mGoogleMap.setInfoWindowAdapter(customInfoWindow);

                marker.setTag(info);
                marker.showInfoWindow();

                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String lat1 = String.valueOf(lat);
                String lng1 = String.valueOf(lng);

                //Define list to get all latLng for the route
                ArrayList<LatLng> path = new ArrayList<LatLng>();
                path.clear();

                //Execute Directions API request
                GeoApiContext context = new GeoApiContext.Builder()
                        .apiKey(getString(R.string.api_key_geo))
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
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                //Draw the polyline
                if (path.size() > 0) {
                    PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.rgb(153, 153, 102)).width(5);
                    mGoogleMap.addPolyline(opts);
                }
            }
        }

        Toast.makeText(getContext(), getName, Toast.LENGTH_LONG).show();
    }

    public static void setButtonsEnabledState() {
        if (FavoritesActivity.getGeofencesAdded()) {
            linearLayoutYes.setVisibility(View.GONE);
            linearLayoutNo.setVisibility(View.VISIBLE);
        } else {
            linearLayoutYes.setVisibility(View.VISIBLE);
            linearLayoutNo.setVisibility(View.GONE);
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
