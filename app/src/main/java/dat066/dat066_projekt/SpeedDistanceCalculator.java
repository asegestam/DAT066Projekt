package dat066.dat066_projekt;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import static android.content.ContentValues.TAG;

public class SpeedDistanceCalculator {

    private static SpeedDistanceCalculator INSTANCE = null;
    private double distanceInMetres;
    private double speed;
    private double highestSpeed = 0;
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
            if(speed > highestSpeed) {
                highestSpeed = speed;
            }
        }
    }

    /** Calculates average speed */
    private double calcAverageSpeed() {
        long time = (lastRecordedLocation.getTime() - firstRecordedLocation.getTime())/1000;
        double avgspeed = (firstRecordedLocation.distanceTo(lastRecordedLocation))/time;
        Log.d(TAG, "calcAverageSpeed: medelhastighet " + avgspeed*3.6 + " km/h");
        return avgspeed*3.6;
    }

    private double calcAveragePace() {
        if(calcAverageSpeed() <= 0) return 0;
        else return 60/calcAverageSpeed();
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
    public double getDistanceInMetres() {
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
    public double getAveragePace() {
        return calcAveragePace();
    }

    public double getHighestSpeed() {
        return highestSpeed;
    }
}
