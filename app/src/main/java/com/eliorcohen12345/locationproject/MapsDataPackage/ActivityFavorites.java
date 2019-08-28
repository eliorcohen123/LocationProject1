package com.eliorcohen12345.locationproject.MapsDataPackage;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.eliorcohen12345.locationproject.R;

// Activity of FragmentFavorites
public class ActivityFavorites extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        frg();
    }

    private void frg() {
        FragmentFavorites fragmentFavorites = new FragmentFavorites();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFavoritesContainer, fragmentFavorites);
        fragmentTransaction.commit();
    }

}
