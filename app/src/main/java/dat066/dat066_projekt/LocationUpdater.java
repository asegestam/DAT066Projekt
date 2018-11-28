package dat066.dat066_projekt;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

public class LocationUpdater implements LocationListener {

    private Location lastLocation = null;
    private Location firstLocation = null;
    private SpeedDistanceCalculator speedDistanceCalculator;

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
