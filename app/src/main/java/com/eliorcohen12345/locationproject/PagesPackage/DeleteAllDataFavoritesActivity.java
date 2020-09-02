package com.eliorcohen12345.locationproject.PagesPackage;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eliorcohen12345.locationproject.DataAppPackage.PlaceViewModelFavorites;
import com.eliorcohen12345.locationproject.OthersPackage.ConApp;
import com.eliorcohen12345.locationproject.R;

public class DeleteAllDataFavoritesActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnOK, btnCancel;
    private PlaceViewModelFavorites placeViewModelFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_all_data_favorites);

        initUI();
        initListeners();
    }

    private void initUI() {
        btnOK = findViewById(R.id.btnOK);
        btnCancel = findViewById(R.id.btnCancel);

        placeViewModelFavorites = new PlaceViewModelFavorites(ConApp.getApplication());
    }

    private void initListeners() {
        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                placeViewModelFavorites.deleteAll();

                Toast toast = Toast.makeText(DeleteAllDataFavoritesActivity.this, "All the data of favorites are deleted!", Toast.LENGTH_LONG);
                View view = toast.getView();
                view.getBackground().setColorFilter(getResources().getColor(R.color.colorLightBlue), PorterDuff.Mode.SRC_IN);
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(getResources().getColor(R.color.colorBrown));
                toast.show();

                Intent intentDeleteAllDataToMain = new Intent(DeleteAllDataFavoritesActivity.this, MainActivity.class);
                startActivity(intentDeleteAllDataToMain);
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
        }
    }

}
