package com.eliorcohen12345.locationproject.PagesPackage;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eliorcohen12345.locationproject.ViewModelsPackage.PlaceViewModelSearchDB;
import com.eliorcohen12345.locationproject.OthersPackage.ConApp;
import com.eliorcohen12345.locationproject.R;

public class DeleteAllDataHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnOK, btnCancel;
    private PlaceViewModelSearchDB placeViewModelSearchDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_all_data_history);

        initUI();
        initListeners();
    }

    private void initUI() {
        btnOK = findViewById(R.id.btnOK);
        btnCancel = findViewById(R.id.btnCancel);

        placeViewModelSearchDB = new PlaceViewModelSearchDB(ConApp.getApplication());
    }

    private void initListeners() {
        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                placeViewModelSearchDB.deleteAll();

                Toast toast = Toast.makeText(DeleteAllDataHistoryActivity.this, "All the data of history are deleted!", Toast.LENGTH_LONG);
                View view = toast.getView();
                view.getBackground().setColorFilter(getResources().getColor(R.color.colorLightBlue), PorterDuff.Mode.SRC_IN);
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(getResources().getColor(R.color.colorBrown));
                toast.show();

                Intent intentDeleteAllDataToMain = new Intent(DeleteAllDataHistoryActivity.this, MainActivity.class);
                startActivity(intentDeleteAllDataToMain);
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
        }
    }

}
