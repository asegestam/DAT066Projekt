package dat066.dat066_projekt;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class SpeedDistanceCalculator implements LocationListener {

    private static Location lastLocation;
    private static double distanceInMetres;
    private double speed;
    private ArrayList<Double> avgSpeed = new ArrayList<>();
    private double averageSpeed;
    private volatile double ele;



    @Override
    public void onLocationChanged(Location location) {
        //Log.e(TAG, "onLocationChanged: " + location);
        if(lastLocation == null) {
            lastLocation = location;
            return;
        }
        long currentTime = location.getTime();
        long lastTime = lastLocation.getTime();
        long timeBetween = (currentTime - lastTime)/1000;
        double distance = location.distanceTo(lastLocation);
        distanceInMetres += distance;
        if(timeBetween > 0) {
            speed = distance/timeBetween;
            avgSpeed.add(speed);
        }
        if(!avgSpeed.isEmpty()){
            calcAverageSpeed();
        }
        //Log.d(TAG, "Distance mellan " + distance+ " m");          //Loggar avståndet mellan uppdateringar
        Log.d(TAG, "Distance " + distanceInMetres + " m");     //Loggar totala avståndet
        //Log.d(TAG, "Time " + timeBetween + " s");                 //Loggar tiden mellan uppdateringar
        Log.d(TAG, "Speed " + speed + " m/s");                //Loggar hastighet i m/s
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

        final double LONGITUDE = lastLocation.getLongitude();
        final double LATITUDE = lastLocation.getLatitude();
        final String url = "https://maps.googleapis.com/maps/api/elevation/json?locations=" + LATITUDE + "," + LONGITUDE + "&key=AIzaSyDVhNkpid7dwf_jsBQ02XQKJNW4vW-DhvA";

        new Thread() {
            public void run() {
                HttpHandler sh = new HttpHandler();
                double elevation = 0;
                String jsonStr = sh.makeServiceCall(url);
                Log.e(TAG, "parsed: LAT: " + LATITUDE + " LONG: " + LONGITUDE);
                Log.e(TAG, "String: " + jsonStr);

                if (jsonStr != null) {
                    try {

                        ele = getData(jsonStr,elevation);

                        Log.e(TAG, jsonStr);
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                }
            }
        }.start();
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

    /* Räknar ut medelhastigheten */
    private double calcAverageSpeed() {
        double sum = 0;
        for (Double value : avgSpeed) {
            if(value != null) sum += value;
        }
        averageSpeed = sum/avgSpeed.size();
        Log.d(TAG, "Medelhastighet " + averageSpeed  + " m/s"); //Loggar medelhastigheten de senaste 5000 metrarna
        if(avgSpeed.size() > 500) avgSpeed.clear();
        return averageSpeed;
    }

    public void resetValues() {
        try {
            distanceInMetres = 0;
            speed = 0;
            averageSpeed = 0;
            avgSpeed.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Location getLastLocation() {
        return lastLocation;
    }

    public static double getDistanceInMetres() {
        return distanceInMetres;
    }

    public double getSpeed() {
        return speed;
    }

    public ArrayList<Double> getAvgSpeed() {
        return avgSpeed;
    }

    public double getAverageSpeed() {
        return calcAverageSpeed();
}

}
