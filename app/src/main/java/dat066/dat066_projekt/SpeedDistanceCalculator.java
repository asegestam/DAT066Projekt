package dat066.dat066_projekt;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import static android.content.ContentValues.TAG;

public class SpeedDistanceCalculator {

    private static SpeedDistanceCalculator INSTANCE = null;
    private static double distanceInMetres;
    private double speed;
    private ArrayList<Double> avgSpeedArray = new ArrayList<>();
    private double averageSpeed;
    private Location firstRecordedLocation = null;
    private Location lastRecordedLocation = null;

    private SpeedDistanceCalculator() {
    }

    public static SpeedDistanceCalculator getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SpeedDistanceCalculator();
        }
        return INSTANCE;
    }

    /** Handles the location change by calculating speed and distance, calling for fragment to re draw route and updating camera */
    public void handleLocationChange(Location currentLocation, Location lastLocation) {
        if(firstRecordedLocation == null) {
            firstRecordedLocation = currentLocation;
        }
        lastRecordedLocation = currentLocation;
        long currentTime = currentLocation.getTime();
        long lastTime = lastLocation.getTime();
        long timeBetween = (currentTime - lastTime)/1000;
        double distance = currentLocation.distanceTo(lastLocation);
        distanceInMetres += distance;
        Log.i(TAG, "Distans " + distanceInMetres  + " m");
        if(timeBetween > 0) { // to avoid dividing by 0
            speed = distance/timeBetween;
        }
    }

    /** Calculates average speed */
    private double calcAverageSpeed() {
        long time = (lastRecordedLocation.getTime() - firstRecordedLocation.getTime())/1000;
        double avgspeed = (firstRecordedLocation.distanceTo(lastRecordedLocation))/time;
        Log.d(TAG, "calcAverageSpeed: medelhastighet " + avgspeed + " m/s");
        return avgspeed;
    }

    /** Resets values, used when wanting to reset the recorded speed and distance */
    public void resetValues() {
        try {
            distanceInMetres = 0;
            speed = 0;
            averageSpeed = 0;
            avgSpeedArray.clear();
            firstRecordedLocation = null;
            lastRecordedLocation = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static double getDistanceInMetres() {
        return distanceInMetres;
    }

    public double getSpeed() {
        return speed;
    }

    public ArrayList<Double> getAvgSpeed() {
        return avgSpeedArray;
    }

    public double getAverageSpeed() {
        return calcAverageSpeed();
    }
}
