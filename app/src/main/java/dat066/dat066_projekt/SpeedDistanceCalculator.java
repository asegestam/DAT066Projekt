package dat066.dat066_projekt;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static android.content.ContentValues.TAG;

public class SpeedDistanceCalculator {

    private static double distanceInMetres;
    private double speed;
    private ArrayList<Double> avgSpeedArray = new ArrayList<>();
    private double averageSpeed;
    private MapFragment map;
    int i ;

    SpeedDistanceCalculator(MapFragment map) {
        this.map = map;
        i = 0;
    }

    /** Handles the location change by calculating speed and distance, calling for fragment to re draw route and updating camera */
    public void handleLocationChange(Location currentLocation, Location lastLocation) {
        long currentTime = currentLocation.getTime();
        long lastTime = lastLocation.getTime();
        long timeBetween = (currentTime - lastTime)/1000;
        double distance = currentLocation.distanceTo(lastLocation);
        distanceInMetres += distance;
        Log.d(TAG, "Distans " + distanceInMetres  + " m");
        if(timeBetween > 0) { // to avoid dividing by 0
            speed = distance/timeBetween;
            avgSpeedArray.add(speed);
        }
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.addLatLngToRoute(latLng);
        map.updateTextViews(distanceInMetres, calcAverageSpeed(), System.currentTimeMillis());
        map.updateCamera(latLng);
    }

    /** Calculates average speed */
    private double calcAverageSpeed() {
        if(avgSpeedArray.isEmpty()) {
            return speed;
        }
        double sum = 0;
        for (Double value : avgSpeedArray) {
            if(value != null) sum += value;
        }
        averageSpeed = sum/avgSpeedArray.size();
        Log.d(TAG, "Medelhastighet " + averageSpeed  + " m/s"); //Loggar medelhastigheten de senaste 5000 metrarna
        if(avgSpeedArray.size() > 500) avgSpeedArray.clear();
        return averageSpeed;
    }

    /** Resets values, used when wanting to reset the recorded speed and distance */
    public void resetValues() {
        try {
            distanceInMetres = 0;
            speed = 0;
            averageSpeed = 0;
            avgSpeedArray.clear();
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
