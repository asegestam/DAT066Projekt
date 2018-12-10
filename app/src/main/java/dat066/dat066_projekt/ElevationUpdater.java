package dat066.dat066_projekt;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

class ElevationUpdater {

    private static ElevationUpdater elevationUpdater_instance = null;
    private volatile double ele;
    private Location location;
    OnPlotDataListener mCallback;
    ArrayList<Double> elevationArray;
    private boolean activityStopped;

    public ElevationUpdater() {
        this.elevationArray = new ArrayList<>();
    }

    public static ElevationUpdater getInstance() {
        if(elevationUpdater_instance == null) {
            elevationUpdater_instance = new ElevationUpdater();
        }
        return elevationUpdater_instance;
    }

    public static void setElevationUpdater_instance(ElevationUpdater elevationUpdater_instance) {
        ElevationUpdater.elevationUpdater_instance = elevationUpdater_instance;
    }

    /*Räknar ut höjden*/
    public void getElevationData(){
        if(location != null) {
            final double LONGITUDE = location.getLongitude();
            final double LATITUDE = location.getLatitude();
            final String url = "https://maps.googleapis.com/maps/api/ele/json?locations=" + LATITUDE + "," + LONGITUDE + "&key=AIzaSyDVhNkpid7dwf_jsBQ02XQKJNW4vW-DhvA";

            new Thread() {
                public void run() {
                    HttpHandler sh = new HttpHandler();
                    double elevation = 0;
                    String jsonStr = sh.makeServiceCall(url);
                    Log.e(TAG, "parsed: LAT: " + LATITUDE + " LONG: " + LONGITUDE);
                    Log.e(TAG, "String: " + jsonStr);

                    if (jsonStr != null) {
                        try {

                            ele = getData(jsonStr, elevation);

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
    }

    private double getData(String jsonStr, double elevation) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonObj.getJSONArray("results");
        elevation = -1;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonTemp = jsonArray.getJSONObject(i);
            elevation = jsonTemp.getDouble("ele");
        }
        return elevation;
    }

    public double getEle() {
        getElevationData();
        return ele;
    }

    /** Graph plot stuff */

    public void plotGraph() {
        if(activityStopped) {
            if(getEle() > 0) {
                elevationArray.add(getEle());
            }
            Log.d(TAG, "plotGraph: elevationarray size " + elevationArray.size());
          mCallback.onDataGiven(elevationArray);
        }else{
            if(getEle() != 0.0) {
                elevationArray.add(getEle());
                Log.e(TAG, "Added: " + getEle() + " in MapFragment!");
            }
        }
    }

    public void setOnPlotDataListener(Activity activity){
        mCallback = (OnPlotDataListener) activity;
    }

    public interface OnPlotDataListener{
        public void onDataGiven(ArrayList elevation);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<Double> getElevationArray() {
        return elevationArray;
    }

    public void setActivityStopped(boolean activityStopped) {
        this.activityStopped = activityStopped;
    }
}
