package eliorcohen.com.googlemapsapi;

public class GoogleMapsApi {

    private String myQuery;

    public String getStringGoogleMapsApi(double lat, double lng, int myRadius, String pageToken, String open, String type, String query, String key) {
        myQuery = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                lat + "," + lng +
                "&radius="
                + myRadius +
                "&sensor=true&rankby=prominence&pagetoken="
                + pageToken +
                open +
                "&types="
                + type +
                "&keyword="
                + query +
                "&key=" +
                key;
        return myQuery;
    }

}
