package com.eliorcohen12345.locationproject.GeofencePackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofenceTransitionsJobIntentService.enqueueWork(context, intent);
    }

}
