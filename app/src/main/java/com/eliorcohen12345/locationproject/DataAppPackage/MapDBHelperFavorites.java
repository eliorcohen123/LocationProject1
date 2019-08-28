package com.eliorcohen12345.locationproject.DataAppPackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MapDBHelperFavorites extends SQLiteOpenHelper {

    private static final String MAP_TABLE_NAME = "FAVORITES";
    private static final String MAP_ID = "ID";
    private static final String MAP_NAME = "NAME";
    private static final String MAP_ADDRESS = "ADDRESS";
    private static final String MAP_LAT = "LAT";
    private static final String MAP_LNG = "LNG";
    private static final String MAP_PHOTOS = "PHOTOS";

    public MapDBHelperFavorites(Context context) {
        super(context, MAP_TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + MAP_TABLE_NAME + "(" +
                MAP_ID + " INTEGER PRIMARY KEY, " +
                MAP_NAME + " TEXT, " +
                MAP_ADDRESS + " TEXT, " +
                MAP_LAT + " REAL, " +
                MAP_LNG + " REAL, " +
                MAP_PHOTOS + " TEXT " + ")";
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLiteException ex) {
            Log.e("SQLiteException", ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MAP_TABLE_NAME);
        onCreate(db);
    }

    //Add info items
    public void addMapFav(String name, String address, Double lat, Double lng, String photo) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MAP_NAME, name);
        contentValues.put(MAP_ADDRESS, address);
        contentValues.put(MAP_LAT, lat);
        contentValues.put(MAP_LNG, lng);
        contentValues.put(MAP_PHOTOS, photo);

        long id = db.insertOrThrow(MAP_TABLE_NAME, null, contentValues);
        try {
            Log.d("MapDBHelperFavorites", "insert new place with id: " + id +
                    ", name: " + name);
        } catch (SQLiteException ex) {
            Log.e("MapDBHelperFavorites", ex.getMessage());
        } finally {
            db.close();
        }
    }

    //Edit info items
    public void updateMap(String name, String address, Double lat, Double lng, String photo, String id) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MAP_NAME, name);
        values.put(MAP_ADDRESS, address);
        values.put(MAP_LAT, lat);
        values.put(MAP_LNG, lng);
        values.put(MAP_PHOTOS, photo);

        int rowNumber3 = db.update(MAP_TABLE_NAME, values, MAP_ID + " = ?", new String[]{String.valueOf(id)});
        try {
            Log.d("MapDBHelperFavorites", "update new map with id: " + rowNumber3 +
                    ", Name: " + name);
        } catch (SQLiteException ex) {
            Log.e("MapDBHelperFavorites", ex.getMessage());
            throw ex;
        } finally {
            db.close();
        }
    }

    // Delete info items
    public void deleteMap(PlaceModel placeModel) {

        SQLiteDatabase db = getWritableDatabase();

        String[] ids = new String[1];
        ids[0] = placeModel.getId() + "";
        try {
            db.delete(MAP_TABLE_NAME, MAP_ID + " =? ", ids);
        } catch (SQLiteException e) {
            Log.e("MapDBHelperFavorites", e.getMessage());
        } finally {
            db.close();
        }
    }

    // Delete all the data of the Favorites
    public void deleteData() {

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("delete from " + MAP_TABLE_NAME);
        } catch (SQLiteException e) {
            Log.e("MapDBHelperFavorites", e.getMessage());
        } finally {
            db.close();
        }
    }

    // Get all info items
    public ArrayList<PlaceModel> getAllMaps() {

        ArrayList<PlaceModel> placeModels = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(MAP_TABLE_NAME, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int colID = cursor.getColumnIndex(MAP_ID);
            int id = cursor.getInt(colID);
            String name = cursor.getString(1);
            String address = cursor.getString(2);
            double lat = cursor.getDouble(3);
            double lng = cursor.getDouble(4);
            String photo = cursor.getString(5);
            Location location = new Location();
            location.setLat(lat);
            location.setLng(lng);
            Geometry geometry = new Geometry();
            geometry.setLocation(location);
            Photos photos = new Photos();
            photos.setPhoto_reference(photo);
            List<Photos> photosList = new ArrayList<Photos>();
            photosList.add(photos);
            PlaceModel placeModel = new PlaceModel(name, address, geometry, photosList);
            placeModel.setId(String.valueOf(id));
            placeModels.add(placeModel);
        }
        return placeModels;
    }

}
