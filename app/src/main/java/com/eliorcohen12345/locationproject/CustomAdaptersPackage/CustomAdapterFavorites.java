package com.eliorcohen12345.locationproject.CustomAdaptersPackage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eliorcohen12345.locationproject.ModelsPackage.Results;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceViewModelFavorites;
import com.eliorcohen12345.locationproject.OthersPackage.ConApp;
import com.eliorcohen12345.locationproject.PagesPackage.DeletePlaceActivity;
import com.eliorcohen12345.locationproject.PagesPackage.EditPlaceActivity;
import com.eliorcohen12345.locationproject.PagesPackage.MapFavoritesFragment;
import com.eliorcohen12345.locationproject.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class CustomAdapterFavorites extends RecyclerView.Adapter<CustomAdapterFavorites.PlaceViewHolder> {

    class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView name3, address3, kmMe3;
        private LinearLayout linear3;

        private PlaceViewHolder(View itemView) {
            super(itemView);

            name3 = itemView.findViewById(R.id.name1);
            address3 = itemView.findViewById(R.id.address1);
            kmMe3 = itemView.findViewById(R.id.kmMe1);
            linear3 = itemView.findViewById(R.id.linear1);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem edit = menu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem share = menu.add(Menu.NONE, 2, 2, "Share");
            MenuItem delete = menu.add(Menu.NONE, 3, 3, "Delete");

            edit.setOnMenuItemClickListener(onChange);
            share.setOnMenuItemClickListener(onChange);
            delete.setOnMenuItemClickListener(onChange);
        }

        private final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Results current = mPlacesFavoritesList.get(getAdapterPosition());
                switch (item.getItemId()) {
                    case 1:
                        Intent intent = new Intent(mInflater.getContext(), EditPlaceActivity.class);
                        intent.putExtra(mInflater.getContext().getString(R.string.map_id), current.getPlace_id());
                        intent.putExtra(mInflater.getContext().getString(R.string.map_edit), current);
                        mInflater.getContext().startActivity(intent);
                        break;
                    case 2:
                        String name = current.getName();
                        String address = current.getVicinity();
                        double lat = current.getLat();
                        double lng = current.getLng();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\nAddress: " + address + "\nLatitude: " + lat + "\nLongitude: " + lng);
                        sendIntent.setType("text/plain");
                        mInflater.getContext().startActivity(sendIntent);
                        break;
                    case 3:
                        placeViewModelFavorites = new PlaceViewModelFavorites(ConApp.getApplication());
                        placeViewModelFavorites.deletePlace(current);

                        Intent intentDeleteData = new Intent(mInflater.getContext(), DeletePlaceActivity.class);
                        mInflater.getContext().startActivity(intentDeleteData);
                        break;
                }
                return false;
            }
        };
    }

    private final LayoutInflater mInflater;
    private ArrayList<Results> mPlacesFavoritesList;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private PlaceViewModelFavorites placeViewModelFavorites;

    public CustomAdapterFavorites(Context context, ArrayList<Results> dataList) {
        mInflater = LayoutInflater.from(context);
        this.mPlacesFavoritesList = dataList;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.apater_place, parent, false);
        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaceViewHolder holder, final int position) {
        if (mPlacesFavoritesList != null) {
            initLocation();
            final Results current = mPlacesFavoritesList.get(position);
            locationManager = (LocationManager) mInflater.getContext().getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(mInflater.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.checkSelfPermission(mInflater.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
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
                    holder.name3.setText(current.getName());
                    holder.address3.setText(current.getVicinity());
                    double distanceMe;
                    Location locationA = new Location("Point A");
                    locationA.setLatitude(current.getLat());
                    locationA.setLongitude(current.getLng());
                    Location locationB = new Location("Point B");
                    locationB.setLatitude(location.getLatitude());
                    locationB.setLongitude(location.getLongitude());
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mInflater.getContext());
                    String result = prefs.getString("myKm", "1000.0");
                    assert result != null;
                    double val = Double.parseDouble(result);
                    distanceMe = locationA.distanceTo(locationB) / val;   // in km
                    String distanceKm1;
                    String disMile;
                    if (val == 1000.0) {
                        if (distanceMe < 1) {
                            int dis = (int) (distanceMe * 1000);
                            distanceKm1 = "Meters: " + dis;
                            holder.kmMe3.setText(distanceKm1);
                        } else if (distanceMe >= 1) {
                            String disM = String.format("%.2f", distanceMe);
                            distanceKm1 = "Km: " + disM;
                            // Put the text in kmMe3
                            holder.kmMe3.setText(distanceKm1);
                        }
                    } else if (val == 1609.344) {
                        String distanceMile1 = String.format("%.2f", distanceMe);
                        disMile = "Miles: " + distanceMile1;
                        // Put the text in kmMe3
                        holder.kmMe3.setText(disMile);
                    }

                    try {
                        Glide.with(mInflater.getContext()).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + current.getPhoto_reference() +
                                "&key=" + mInflater.getContext().getString(R.string.api_key_search)).into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NotNull Drawable resource, Transition<? super Drawable> transition) {
                                holder.linear3.setBackground(resource);
                            }
                        });
                    } catch (Exception e) {
                        holder.linear3.setBackgroundResource(R.drawable.no_image_available);
                    }
                }
            }

            holder.linear3.setOnClickListener(v -> {
                MapFavoritesFragment mapFavoritesFragment = new MapFavoritesFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(mInflater.getContext().getString(R.string.map_favorites_key), current);
                mapFavoritesFragment.setArguments(bundle);
                FragmentManager fragmentManager = ((AppCompatActivity) mInflater.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragmentFavoritesContainer, mapFavoritesFragment).addToBackStack(null).commit();
            });
        } else {
            // Covers the case of data not being ready yet.
            holder.name3.setText("No Places");
        }
    }

    public void setMapsCollections() {
        initLocation();
        if (ActivityCompat.checkSelfPermission(mInflater.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(mInflater.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
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
                Collections.sort(mPlacesFavoritesList, (obj1, obj2) -> {
                    // ## Ascending order
//                return obj1.getDistance().compareToIgnoreCase(obj2.getDistance()); // To compare string values
                    return Double.compare(Math.sqrt(Math.pow(obj1.getLat() - location.getLatitude(), 2) + Math.pow(obj1.getLng() - location.getLongitude(), 2)),
                            Math.sqrt(Math.pow(obj2.getLat() - location.getLatitude(), 2) + Math.pow(obj2.getLng() - location.getLongitude(), 2))); // To compare integer values

                    // ## Descending order
                    // return obj2.getCompanyName().compareToIgnoreCase(obj1.getCompanyName()); // To compare string values
                    // return Integer.valueOf(obj2.getId()).compareTo(obj1.getId()); // To compare integer values
                });
            }
        }
        notifyDataSetChanged();
    }

    private void initLocation() {
        locationManager = (LocationManager) mInflater.getContext().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
    }

    // getItemCount() is called many times, and when it is first called,
    // mPlacesSearchList has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPlacesFavoritesList != null)
            return mPlacesFavoritesList.size();
        else return 0;
    }

}
