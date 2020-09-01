package com.eliorcohen12345.locationproject.OthersPackage.GeofencePackage;

public final class Constants {

    private Constants() {

    }

    private static String PACKAGE_NAME = "com.eliorcohen12345.locationproject";
    public static String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    private static long GEOFENCE_EXPIRATION_IN_HOURS = -1;
    public static long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

}
