package dat066.dat066_projekt;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class SpeedDistanceCalculator implements LocationListener {

    private Location lastLocation = null;
    private Location firstLocation = null;
    private boolean resumeLineDrawn;
    private static double distanceInMetres;
    private double speed;
    private ArrayList<Double> avgSpeed = new ArrayList<>();
    private double averageSpeed;
    private PolylineOptions routeOptions = new PolylineOptions().width(15).color(Color.BLUE).geodesic(true);
    private PolylineOptions resumeOptions = new PolylineOptions().width(20).color(Color.RED).geodesic(true);
    GoogleMap map;
    Polyline route;
    Polyline resumePolyline;
    MapFragment fragment;

    public SpeedDistanceCalculator(GoogleMap map, MapFragment fragment) {
        this.map = map;
        this.fragment = fragment;
    }

    /** On LocationChange calculate user speed, distance moved, adds LatLng objects to draw a Route */
    @Override
    public void onLocationChanged(Location location) {
        if(lastLocation == null) {
            lastLocation = location;
        }
        if(firstLocation == null) {
            firstLocation = location;
        }
        long currentTime = location.getTime();
        long lastTime = lastLocation.getTime();
        long timeBetween = (currentTime - lastTime)/1000;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        routeOptions.add(latLng);
        double distance = location.distanceTo(lastLocation);
        distanceInMetres += distance;
        if(timeBetween > 0) {
            speed = distance/timeBetween;
            avgSpeed.add(speed);
        }
        if(!avgSpeed.isEmpty()){
            calcAverageSpeed();
        }
        reDrawRoute();
        fragment.updateTextViews(distanceInMetres, calcAverageSpeed());
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

    /** Calculates average speed */
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

    /** Resets values */
    public void resetValues() {
        try {
            distanceInMetres = 0;
            speed = 0;
            averageSpeed = 0;
            avgSpeed.clear();
            routeOptions.getPoints().clear();
            firstLocation = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** Draws a Polyline based on user movement */
    private void reDrawRoute(){
        route = map.addPolyline(routeOptions);
    }

    /**
     * Draws a PolyLine when resuming activity from the point the user clicked pause to the point the user pressed resume
     */
    public void drawResumeLine(Location lastKnownLocation){
        List<LatLng> latLngs = routeOptions.getPoints();
        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        setLastLocation(null); //so the distance is not accounted for when resuming
        resumeOptions.add(latLngs.get(latLngs.size() - 1)).add(latLng);
        resumePolyline = map.addPolyline(resumeOptions);
        resumeOptions.getPoints().clear();
        routeOptions.getPoints().clear();
        routeOptions.add(latLng);
        fragment.requestLocationUpdates(); //enable Location updates like normal
    }

    public LatLng getFirstLocation() {
        LatLng fl = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
        return fl;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
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

    public PolylineOptions getRouteOptions() {
        return routeOptions;
    }
}
