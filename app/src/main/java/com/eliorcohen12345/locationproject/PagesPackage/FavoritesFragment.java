package com.eliorcohen12345.locationproject.PagesPackage;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eliorcohen12345.locationproject.DataAppPackage.PlaceViewModelFavorites;
import com.eliorcohen12345.locationproject.OthersPackage.ConApp;
import com.eliorcohen12345.locationproject.OthersPackage.ItemDecoration;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eliorcohen12345.locationproject.CustomAdaptersPackage.PlaceCustomAdapterFavorites;
import com.eliorcohen12345.locationproject.ModelsPackage.PlaceModel;
import com.eliorcohen12345.locationproject.R;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<PlaceModel> mMapList;  // ArrayList of PlaceModel
    private PlaceCustomAdapterFavorites mAdapter;  // CustomAdapter of FragmentFavorites
    private RecyclerView mRecyclerView;  // RecyclerView of FragmentFavorites
    private PlaceViewModelFavorites placeViewModelFavorites;
    private View mView;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ItemDecoration itemDecoration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_favorites_layout, container, false);

        initUI();
        drawerLayout();
        getData();

        return mView;
    }

    private void initUI() {
        mRecyclerView = mView.findViewById(R.id.places_list_favorites);  // ID of the RecyclerView of FragmentFavorites

        drawer = mView.findViewById(R.id.drawer_layout);
        navigationView = mView.findViewById(R.id.nav_view);
        toolbar = mView.findViewById(R.id.toolbar);

        placeViewModelFavorites = new PlaceViewModelFavorites(ConApp.getApplication());  // Put the SQLiteHelper in FragmentFavorites
        itemDecoration = null;

        mMapList = new ArrayList<>();
    }

    private void getData() {
        mMapList.clear();
        mMapList = placeViewModelFavorites.getAllPlaces();  // Put the getAllMaps of SQLiteHelper in the ArrayList of FragmentFavorites
        mAdapter = new PlaceCustomAdapterFavorites(getContext(), mMapList);  // Comparing the ArrayList of FragmentFavorites to the CustomAdapter
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
    }

    private void drawerLayout() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        mView.findViewById(R.id.myButton).setOnClickListener(v -> {
            // open right drawer
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else
                drawer.openDrawer(GravityCompat.END);
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.intentMainActivity) {
            Intent intentBackMainActivity = new Intent(getContext(), MainActivity.class);
            startActivity(intentBackMainActivity);
        } else if (id == R.id.deleteAllDataFavorites) {
            Intent intentDeleteAllData = new Intent(getContext(), DeleteAllDataFavoritesActivity.class);
            startActivity(intentDeleteAllData);
        }

        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

}
