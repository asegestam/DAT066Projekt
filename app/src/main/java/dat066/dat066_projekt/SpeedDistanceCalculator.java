package dat066.dat066_projekt;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class SpeedDistanceCalculator implements LocationListener {

    private static Location lastLocation = null;
    private static double distanceInMetres;
    private double speed;
    private ArrayList<Double> avgSpeed = new ArrayList<>();
    private double averageSpeed;



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
        return averageSpeed;
    }

}
