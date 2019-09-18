package com.eliorcohen12345.locationproject.MapsDataPackage;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliorcohen12345.locationproject.DataAppPackage.MapDBHelperFavorites;
import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AddPlaceFavorites extends AppCompatActivity implements View.OnClickListener {

    private PlaceModel item;
    private MapDBHelperFavorites mapDBHelperFavorites;  // The SQLiteHelper of the app
    private EditText name, address, lat, lng, photo;
    private TextView textViewOK, textViewShow;
    private Button btnBack;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_place_me);

        initUI();
        initListeners();
        getData();
    }

    private void initUI() {
        // GetSerializable for the texts
        item = (PlaceModel) Objects.requireNonNull(getIntent().getExtras()).getSerializable(getString(R.string.map_add_from_internet));

        name = findViewById(R.id.editTextName);  // ID of the name
        address = findViewById(R.id.editTextAddress);  // ID of the address
        lat = findViewById(R.id.editTextLat);  // ID of the lat
        lng = findViewById(R.id.editTextLng);  // ID of the lng
        photo = findViewById(R.id.editTextPhoto);  // ID of the photo

        textViewOK = findViewById(R.id.textViewOK);
        textViewShow = findViewById(R.id.textViewShow);

        btnBack = findViewById(R.id.btnBack);

        imageView = findViewById(R.id.imageViewMe);

        mapDBHelperFavorites = new MapDBHelperFavorites(this);
    }

    private void initListeners() {
        textViewOK.setOnClickListener(this);
        textViewShow.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void getData() {
        name.setText(item.getName());  // GetSerializable of name
        address.setText(item.getVicinity());  // GetSerializable of address
        lat.setText(String.valueOf(item.getLat()));  // GetSerializable of lat
        lng.setText(String.valueOf(item.getLng()));  // GetSerializable of lng
        try {
            photo.setText(item.getPhoto_reference());  // GetSerializable of photo
        } catch (Exception e) {
            photo.setText("");  // GetSerializable of photo
        }

        try {
            String picture = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + item.getPhoto_reference() +
                    "&key=" +
                    getString(R.string.api_key_search);
            Picasso.get().load(picture).into(imageView);
            imageView.setVisibility(View.INVISIBLE); //Set the ImageView Invisible
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewOK:
                String name1 = name.getText().toString();  // GetText of the name
                String address1 = address.getText().toString();  // GetText of the address
                String lat1 = lat.getText().toString();  // GetText of the lat
                String lng1 = lng.getText().toString();  // GetText of the lng
                String photo1 = photo.getText().toString();  // GetText of the photo
                double lat2 = Double.parseDouble(lat1);
                double lng2 = Double.parseDouble(lng1);

                // The texts in the SQLiteHelper
                mapDBHelperFavorites.addMapFav(name1, address1, lat2, lng2, photo1);

                // Pass from AddMapFromInternet to ActivityFavorites
                Intent intentAddInternetToMain = new Intent(AddPlaceFavorites.this, ActivityFavorites.class);
                startActivity(intentAddInternetToMain);
                break;
            case R.id.textViewShow:
                try {
                    photo.setVisibility(View.INVISIBLE);  // Canceling the show of URL
                    imageView.setVisibility(View.VISIBLE);  // Show the ImageView
                } catch (Exception e) {
                    photo.setVisibility(View.INVISIBLE);  // Canceling the show of URL
                }
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

}
