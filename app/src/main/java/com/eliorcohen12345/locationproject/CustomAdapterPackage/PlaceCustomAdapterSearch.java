package com.eliorcohen12345.locationproject.CustomAdapterPackage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.MapsDataPackage.AddPlaceFavorites;
import com.eliorcohen12345.locationproject.MapsDataPackage.FragmentMapSearch;
import com.eliorcohen12345.locationproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlaceCustomAdapterSearch extends RecyclerView.Adapter<PlaceCustomAdapterSearch.PlaceViewHolder> {

    class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView name1, address1, kmMe1, isOpen1;
        private ImageView image1;
        private RelativeLayout relativeLayout1;

        private PlaceViewHolder(View itemView) {
            super(itemView);
            name1 = itemView.findViewById(R.id.name1);
            address1 = itemView.findViewById(R.id.address1);
            kmMe1 = itemView.findViewById(R.id.kmMe1);
            isOpen1 = itemView.findViewById(R.id.isOpen1);
            image1 = itemView.findViewById(R.id.image1);
            relativeLayout1 = itemView.findViewById(R.id.relative1);

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
                        Intent intent = new Intent(mInflater.getContext(), AddPlaceFavorites.class);
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
    private double diagonalInches;
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
            final PlaceModel current = mPlacesSearchList.get(position);
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
                    holder.name1.setText(current.getName());
                    holder.address1.setText(current.getVicinity());
                    String distanceKm1;
                    String disMile;
                    if (val == 1000.0) {
                        if (distanceMe < 1) {
                            int dis = (int) (distanceMe * 1000);
                            distanceKm1 = "\n" + "Meters: " + String.valueOf(dis);
                            holder.kmMe1.setText(distanceKm1);
                        } else if (distanceMe >= 1) {
                            String disM = String.format("%.2f", distanceMe);
                            distanceKm1 = "\n" + "Km: " + String.valueOf(disM);
                            // Put the text in kmMe1
                            holder.kmMe1.setText(distanceKm1);
                        }
                    } else if (val == 1609.344) {
                        String distanceMile1 = String.format("%.2f", distanceMe);
                        disMile = "\n" + "Miles: " + String.valueOf(distanceMile1);
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
                        Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + current.getPhoto_reference() +
                                "&key=" + mInflater.getContext().getString(R.string.api_key_search)).into(holder.image1);
                    } catch (Exception e) {

                    }
                }
            }
            holder.relativeLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Tablet/Phone mode
                    DisplayMetrics metrics = new DisplayMetrics();
                    ((AppCompatActivity) mInflater.getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

                    float yInches = metrics.heightPixels / metrics.ydpi;
                    float xInches = metrics.widthPixels / metrics.xdpi;
                    diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);

                    FragmentMapSearch fragmentMapSearch = new FragmentMapSearch();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(mInflater.getContext().getString(R.string.map_search_key), current);
                    fragmentMapSearch.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) mInflater.getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (diagonalInches >= 6.5) {
                        fragmentTransaction.replace(R.id.fragmentLt, fragmentMapSearch);
                    } else {
                        fragmentTransaction.replace(R.id.fragmentContainer, fragmentMapSearch);
                    }
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            setFadeAnimation(holder.itemView);
        } else {
            // Covers the case of data not being ready yet.
            holder.name1.setText("No Places");
        }
    }

    public void setMaps(List<PlaceModel> gameFavorites) {
        mPlacesSearchList = gameFavorites;
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
                Collections.sort(mPlacesSearchList, new Comparator<PlaceModel>() {
                    public int compare(PlaceModel obj1, PlaceModel obj2) {
                        // ## Ascending order
//                return obj1.getDistance().compareToIgnoreCase(obj2.getDistance()); // To compare string values
                        return Double.compare(Math.sqrt(Math.pow(obj1.getLat() - location.getLatitude(), 2) + Math.pow(obj1.getLng() - location.getLongitude(), 2)),
                                Math.sqrt(Math.pow(obj2.getLat() - location.getLatitude(), 2) + Math.pow(obj2.getLng() - location.getLongitude(), 2))); // To compare integer values

                        // ## Descending order
                        // return obj2.getCompanyName().compareToIgnoreCase(obj1.getCompanyName()); // To compare string values
                        // return Integer.valueOf(obj2.getId()).compareTo(obj1.getId()); // To compare integer values
                    }
                });
            }
        }
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mPlacesSearchList has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPlacesSearchList != null)
            return mPlacesSearchList.size();
        else return 0;
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1500);
        view.startAnimation(anim);
    }

}
