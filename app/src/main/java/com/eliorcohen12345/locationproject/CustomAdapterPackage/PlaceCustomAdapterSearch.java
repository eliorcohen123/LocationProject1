package com.eliorcohen12345.locationproject.CustomAdapterPackage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eliorcohen12345.locationproject.ModelsPackage.PlaceModel;
import com.eliorcohen12345.locationproject.PagesPackage.AddPlaceFavoritesActivity;
import com.eliorcohen12345.locationproject.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaceCustomAdapterSearch extends RecyclerView.Adapter<PlaceCustomAdapterSearch.PlaceViewHolder> {

    class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView name1, address1, kmMe1, isOpen1;
        private LinearLayout linear1;

        private PlaceViewHolder(View itemView) {
            super(itemView);
            name1 = itemView.findViewById(R.id.name1);
            address1 = itemView.findViewById(R.id.address1);
            kmMe1 = itemView.findViewById(R.id.kmMe1);
            isOpen1 = itemView.findViewById(R.id.isOpen1);
            linear1 = itemView.findViewById(R.id.linear1);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem add_to_favorites = menu.add(Menu.NONE, 1, 1, "Add to favorites");
            MenuItem share = menu.add(Menu.NONE, 2, 2, "Share");

            add_to_favorites.setOnMenuItemClickListener(onChange);
            share.setOnMenuItemClickListener(onChange);
        }

        private final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PlaceModel current = mPlacesSearchList.get(getAdapterPosition());
                switch (item.getItemId()) {
                    case 1:
                        Intent intent = new Intent(mInflater.getContext(), AddPlaceFavoritesActivity.class);
                        intent.putExtra(mInflater.getContext().getString(R.string.map_add_from_internet), current);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mInflater.getContext().startActivity(sendIntent);
                        break;
                }
                return false;
            }
        };
    }

    private final LayoutInflater mInflater;
    private List<PlaceModel> mPlacesSearchList;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;

    public PlaceCustomAdapterSearch(Context context, ArrayList<PlaceModel> dataList) {
        mInflater = LayoutInflater.from(context);
        this.mPlacesSearchList = dataList;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.place_item_row_total, parent, false);
        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaceViewHolder holder, final int position) {
        if (mPlacesSearchList != null) {
            initLocation();
            final PlaceModel current = mPlacesSearchList.get(position);
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
                    holder.name1.setText(current.getName());
                    holder.address1.setText(current.getVicinity());
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
                    distanceMe = locationA.distanceTo(locationB) / val; // In Km
                    String distanceKm1;
                    String disMile;
                    if (val == 1000.0) {
                        if (distanceMe < 1) {
                            int dis = (int) (distanceMe * 1000);
                            distanceKm1 = "Meters: " + String.valueOf(dis);
                            holder.kmMe1.setText(distanceKm1);
                        } else if (distanceMe >= 1) {
                            String disM = String.format("%.2f", distanceMe);
                            distanceKm1 = "Km: " + String.valueOf(disM);
                            // Put the text in kmMe1
                            holder.kmMe1.setText(distanceKm1);
                        }
                    } else if (val == 1609.344) {
                        String distanceMile1 = String.format("%.2f", distanceMe);
                        disMile = "Miles: " + String.valueOf(distanceMile1);
                        // Put the text in kmMe1
                        holder.kmMe1.setText(disMile);
                    }
                    try {
                        if (String.valueOf(current.getOpening_hours()).equals("true")) {
                            holder.isOpen1.setText("Open");
                        } else if (String.valueOf(current.getOpening_hours()).equals("false")) {
                            holder.isOpen1.setText("Close");
                        } else {
                            holder.isOpen1.setText("No info");
                        }
                    } catch (Exception e) {

                    }

                    try {
                        Glide.with(mInflater.getContext()).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + current.getPhoto_reference() +
                                "&key=" + mInflater.getContext().getString(R.string.api_key_search)).into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NotNull Drawable resource, Transition<? super Drawable> transition) {
                                holder.linear1.setBackground(resource);
                            }
                        });
                    } catch (Exception e) {
                        holder.linear1.setBackgroundResource(R.drawable.no_image_available);
                    }
                }
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.name1.setText("No Places");
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
                Collections.sort(mPlacesSearchList, (obj1, obj2) -> {
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
        if (mPlacesSearchList != null)
            return mPlacesSearchList.size();
        else return 0;
    }

}
