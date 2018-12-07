package dat066.dat066_projekt;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class LocationUpdater implements LocationListener {

    private Location lastLocation = null;
    private Location firstLocation = null;
    private SpeedDistanceCalculator speedDistanceCalculator;
    private volatile double ele;

    public LocationUpdater(SpeedDistanceCalculator speedDistanceCalculator) {
        this.speedDistanceCalculator = speedDistanceCalculator;
    }
    @Override
    public void onLocationChanged(Location location) {
        if(lastLocation == null) {
            lastLocation = location;
        }
        if(firstLocation == null) {
            firstLocation = location;
        }
        speedDistanceCalculator.handleLocationChange(location, lastLocation);
        lastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*Räknar ut höjden*/
    public void getElevation(){
        if(lastLocation != null) {
            final double LONGITUDE = lastLocation.getLongitude();
            final double LATITUDE = lastLocation.getLatitude();
            final String url = "https://maps.googleapis.com/maps/api/elevation/json?locations=" + LATITUDE + "," + LONGITUDE + "&key=AIzaSyDVhNkpid7dwf_jsBQ02XQKJNW4vW-DhvA";

            new Thread() {
                public void run() {
                    HttpHandler sh = new HttpHandler();
                    double elevation = 0;
                    String jsonStr = sh.makeServiceCall(url);
                    // Log.e(TAG, "parsed: LAT: " + LATITUDE + " LONG: " + LONGITUDE);
                    // Log.e(TAG, "String: " + jsonStr);

                    if (jsonStr != null) {
                        try {

                            ele = getData(jsonStr, elevation);

                            //Log.e(TAG, jsonStr);
                        } catch (final JSONException e) {
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                        }
                    } else {
                        Log.e(TAG, "Couldn't get json from server.");
                    }
                }
            }.start();
        }
    }

    public double getData(String jsonStr, double elevation) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObj.getJSONArray("results");
        elevation = -1;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonTemp = jsonArray.getJSONObject(i);
            elevation = jsonTemp.getDouble("elevation");
        }
        return elevation;
    }

    public double getEle() {
        getElevation();
        return ele;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public LatLng getFirstLocation() {
        LatLng fl = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
        return fl;
    }

    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }
}
