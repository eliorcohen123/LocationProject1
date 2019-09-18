package com.eliorcohen12345.locationproject.CustomAdapterPackage;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.eliorcohen12345.locationproject.DataAppPackage.PlaceModel;
import com.eliorcohen12345.locationproject.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Objects;

public class CustomInfoWindowGoogleMapFavorites implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMapFavorites(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.place_custom_infowindow_favorites, null);

        TextView name = view.findViewById(R.id.nameInfo);

//        ImageView img = view.findViewById(R.id.pic);

        TextView address = view.findViewById(R.id.addressInfo);
        TextView distance = view.findViewById(R.id.distanceInfo);

        name.setText(marker.getTitle());

        PlaceModel infoWindowData = (PlaceModel) marker.getTag();

//        int imageId = context.getResources().getIdentifier(infoWindowData.getPhoto_reference(),
//                "drawable", context.getPackageName());
//        img.setImageResource(imageId);

        try {
            address.setText(Objects.requireNonNull(infoWindowData).getVicinity());
            distance.setText(infoWindowData.getDistance());
        } catch (Exception e) {

        }

        return view;
    }

}
