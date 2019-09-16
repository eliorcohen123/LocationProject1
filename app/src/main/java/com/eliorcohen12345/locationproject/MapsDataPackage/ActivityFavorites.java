package com.eliorcohen12345.locationproject.MapsDataPackage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.eliorcohen12345.locationproject.AsyncTaskPackage.GetMapsAsyncTaskFavorites;
import com.eliorcohen12345.locationproject.DataAppPackage.MapDBHelperFavorites;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.GeofencePackage.Constants;
import com.eliorcohen12345.locationproject.GeofencePackage.GeofenceBroadcastReceiver;
import com.eliorcohen12345.locationproject.GeofencePackage.GeofenceErrorMessages;
import com.eliorcohen12345.locationproject.MainAndOtherPackage.ConApp;
import com.eliorcohen12345.locationproject.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Activity of FragmentFavorites
public class ActivityFavorites extends AppCompatActivity implements OnCompleteListener<Void>,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 33;
    private static final int REQUEST_CHECK_SETTINGS = 77;
    private static FusedLocationProviderClient mFusedLocationClient;
    private static LocationRequest mLocationRequest;
    private static LocationCallback mLocationCallback;
    private static final String TAG = "MyLocation";
    private GoogleApiClient mGoogleApiClient;
    private int myRadiusGeo;
    private SharedPreferences prefsSeekGeo;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private GetMapsAsyncTaskFavorites mGetMapsAsyncTaskFavorites;
    private MapDBHelperFavorites mMapDBHelperFavorites;
    private RecyclerView mRecyclerView;
    private ArrayList<PlaceModel> mMapListFavorites;

    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initUI();
        frg();
    }

    // onStart
    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            Log.i(TAG, "Inside onStart function; requesting permission when permission is not available");
            requestPermissions();
        } else {
            Log.i(TAG, "Inside onStart function; getting location when permission is already available");
            getLastLocation();
            performPendingGeofenceTask();
        }
    }

    // onResume
    @Override
    protected void onResume() {
        super.onResume();

        startLocationUpdates();
    }

    // onPause
    @Override
    protected void onPause() {
        super.onPause();

        startLocationUpdates();
//        stopLocationUpdates();
    }

    private void initUI() {
        getMyLocation();

        prefsSeekGeo = PreferenceManager.getDefaultSharedPreferences(this);

        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        populateGeofenceList();

        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }

    private void frg() {
        FragmentFavorites fragmentFavorites = new FragmentFavorites();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFavoritesContainer, fragmentFavorites);
        fragmentTransaction.commit();
    }

    private void getMyLocation() {
        checkLocationPermission();

        // Start all of check location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();

        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ActivityFavorites.this);
        if (ActivityCompat.checkSelfPermission(ActivityFavorites.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityFavorites.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityFavorites.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                Toast.makeText(MainActivity.this, "Please I need yor location to...", Toast.LENGTH_LONG).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityCompat.requestPermissions(ActivityFavorites.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }, 3000);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ActivityFavorites.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                // MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            getLocation();
        }

        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(ActivityFavorites.this);
        final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.toString();
        task.addOnSuccessListener(ActivityFavorites.this, locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            LocationSettingsStates locationSettingsStates = locationSettingsResponse.getLocationSettingsStates();
//                Toast.makeText(MainActivity.this, "Great Success" + locationSettingsStates, Toast.LENGTH_LONG).show();
        });

        task.addOnFailureListener(ActivityFavorites.this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(ActivityFavorites.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    // Resume all of check location
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
//                Toast.makeText(MainActivity.this, "REQUEST_CHECK_SETTINGS result" + requestCode, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
            }
        });
    }

    public static void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(900);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(ConApp.getmContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ConApp.getmContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
    }

//    private void stopLocationUpdates() {
//        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Confirmation")
                        .setMessage("To use the location services in the app confirm this message")
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(ActivityFavorites.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Return whether permissions is needed as boolean value.
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    //Request permission from user
    private void requestPermissions() {
        Log.i(TAG, "Inside requestPermissions function");
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        //Log an additional rationale to the user. This would happen if the user denied the
        //request previously, but didn't check the "Don't ask again" checkbox.
        // In case you want, you can also show snackbar. Here, we used Log just to clear the concept.
        if (shouldProvideRationale) {
            Log.i(TAG, "****Inside requestPermissions function when shouldProvideRationale = true");
            startLocationPermissionRequest();
        } else {
            Log.i(TAG, "****Inside requestPermissions function when shouldProvideRationale = false");
            startLocationPermissionRequest();
        }
    }

    //Start the permission request dialog
    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(ActivityFavorites.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    /**
     * This method should be called after location permission is granted. It gets the recently available location,
     * In some situations, when location, is not available, it may produce null result.
     * WE used SuppressWarnings annotation to avoid the missing permission warning. You can comment the annotation
     * and check the behaviour yourself.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        location = task.getResult();
                    } else {
                        Log.i(TAG, "Inside getLocation function. Error while getting location");
                        System.out.println(TAG + task.getException());
                    }
                });
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public void addGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        addGeofences();
    }

    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    public void removeGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
            requestPermissions();
            return;
        }
        removeGeofences();
    }

    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }
        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            FragmentMapFavorites.setButtonsEnabledState();
            int messageId = getGeofencesAdded() ? R.string.geofences_added : R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private void populateGeofenceList() {
        myRadiusGeo = prefsSeekGeo.getInt("seek_geo", 500);

        mMapDBHelperFavorites = new MapDBHelperFavorites(ConApp.getmContext());
        mMapListFavorites = mMapDBHelperFavorites.getAllMaps();
        mRecyclerView = new RecyclerView(ConApp.getmContext());
        mGetMapsAsyncTaskFavorites = new GetMapsAsyncTaskFavorites(mRecyclerView);
        mGetMapsAsyncTaskFavorites.execute(mMapDBHelperFavorites);

        HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();
        locationManager = (LocationManager) ConApp.getmContext().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(ConApp.getmContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(ConApp.getmContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
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
                BAY_AREA_LANDMARKS.put("Hi", new LatLng(location.getLatitude(), location.getLongitude()));
                for (int i = 0; i < mMapListFavorites.size(); i++) {
                    try {
                        BAY_AREA_LANDMARKS.put(mMapListFavorites.get(i).getName(), new LatLng(mMapListFavorites.get(i).getLat(), mMapListFavorites.get(i).getLng()));
                    } catch (Exception e) {

                    }
                }
            }
        }
        for (Map.Entry<String, LatLng> entry : BAY_AREA_LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            myRadiusGeo
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    public static boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(ConApp.getmContext()).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

}
