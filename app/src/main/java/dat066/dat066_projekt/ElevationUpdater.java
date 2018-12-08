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

public class ElevationUpdater {


    private volatile double ele;
    private Location location;

    /*Räknar ut höjden*/
    public void getElevation(){
        if(location != null) {
            final double LONGITUDE = location.getLongitude();
            final double LATITUDE = location.getLatitude();
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

    private double getData(String jsonStr, double elevation) throws JSONException {
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

    public void setLocation(Location location) {
        this.location = location;
    }
}
