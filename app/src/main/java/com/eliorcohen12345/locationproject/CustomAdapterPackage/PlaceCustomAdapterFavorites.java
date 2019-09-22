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

import com.eliorcohen12345.locationproject.DataAppPackage.MapDBHelperFavorites;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.MapsDataPackage.DeletePlace;
import com.eliorcohen12345.locationproject.MapsDataPackage.EditPlace;
import com.eliorcohen12345.locationproject.MapsDataPackage.FragmentMapFavorites;
import com.eliorcohen12345.locationproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceCustomAdapterFavorites extends RecyclerView.Adapter<PlaceCustomAdapterFavorites.PlaceViewHolder> {

    class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView name3, address3, kmMe3;
        private ImageView image3;
        private RelativeLayout relativeLayout3;

        private PlaceViewHolder(View itemView) {
            super(itemView);
            name3 = itemView.findViewById(R.id.name1);
            address3 = itemView.findViewById(R.id.address1);
            kmMe3 = itemView.findViewById(R.id.kmMe1);
            image3 = itemView.findViewById(R.id.image1);
            relativeLayout3 = itemView.findViewById(R.id.relative1);

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
                PlaceModel current = mPlacesFavoritesList.get(getAdapterPosition());
                switch (item.getItemId()) {
                    case 1:
                        Intent intent = new Intent(mInflater.getContext(), EditPlace.class);
                        intent.putExtra(mInflater.getContext().getString(R.string.map_id), current.getId());
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
                        mMapDBHelperFavorites = new MapDBHelperFavorites(mInflater.getContext());
                        mMapDBHelperFavorites.deleteMap(current);

                        Intent intentDeleteData = new Intent(mInflater.getContext(), DeletePlace.class);
                        mInflater.getContext().startActivity(intentDeleteData);
                        break;
                }
                return false;
            }
        };
    }

    private final LayoutInflater mInflater;
    private ArrayList<PlaceModel> mPlacesFavoritesList;
    private Location location;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private MapDBHelperFavorites mMapDBHelperFavorites;

    public PlaceCustomAdapterFavorites(Context context, ArrayList<PlaceModel> dataList) {
        mInflater = LayoutInflater.from(context);
        this.mPlacesFavoritesList = dataList;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.place_item_row_total, parent, false);
        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaceViewHolder holder, final int position) {
        if (mPlacesFavoritesList != null) {
            final PlaceModel current = mPlacesFavoritesList.get(position);
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
                    holder.name3.setText(current.getName());
                    holder.address3.setText(current.getVicinity());
                    String distanceKm1;
                    String disMile;
                    if (val == 1000.0) {
                        if (distanceMe < 1) {
                            int dis = (int) (distanceMe * 1000);
                            distanceKm1 = "\n" + "Meters: " + String.valueOf(dis);
                            holder.kmMe3.setText(distanceKm1);
                        } else if (distanceMe >= 1) {
                            String disM = String.format("%.2f", distanceMe);
                            distanceKm1 = "\n" + "Km: " + String.valueOf(disM);
                            // Put the text in kmMe3
                            holder.kmMe3.setText(distanceKm1);
                        }
                    } else if (val == 1609.344) {
                        String distanceMile1 = String.format("%.2f", distanceMe);
                        disMile = "\n" + "Miles: " + String.valueOf(distanceMile1);
                        // Put the text in kmMe3
                        holder.kmMe3.setText(disMile);
                    }
                    try {
                        Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + current.getPhoto_reference() +
                                "&key=" + mInflater.getContext().getString(R.string.api_key_search)).into(holder.image3);
                    } catch (Exception e) {

                    }
                }
            }
            holder.relativeLayout3.setOnClickListener(v -> {
                FragmentMapFavorites fragmentMapFavorites = new FragmentMapFavorites();
                Bundle bundle = new Bundle();
                bundle.putSerializable(mInflater.getContext().getString(R.string.map_favorites_key), current);
                fragmentMapFavorites.setArguments(bundle);
                FragmentManager fragmentManager = ((AppCompatActivity) mInflater.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragmentFavoritesContainer, fragmentMapFavorites).addToBackStack(null).commit();
            });

            setFadeAnimation(holder.itemView);
        } else {
            // Covers the case of data not being ready yet.
            holder.name3.setText("No Places");
        }
    }

    // getItemCount() is called many times, and when it is first called,
    // mPlacesSearchList has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPlacesFavoritesList != null)
            return mPlacesFavoritesList.size();
        else return 0;
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1500);
        view.startAnimation(anim);
    }

}
