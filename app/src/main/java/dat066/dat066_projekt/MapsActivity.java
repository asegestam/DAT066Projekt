package dat066.dat066_projekt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.lang.Math;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;


    static Location lastLocation = null;
    static double distanceInMetres;
    double speed;
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<Double> avgSpeed = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        distanceInMetres = 0;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            @Override
            public void onLocationChanged(Location location) {
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

                TextView distanceText = findViewById(R.id.distanceText);
                distanceText.setText("Distance moved " + numberFormat.format(distanceInMetres) + " m");

                TextView speedText = findViewById(R.id.speedText);
                speedText.setText("Speed " + numberFormat.format(speed*3.6) + " km/h");

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
        };
        getLocationPermission();
    }
    /* Räknar ut medelhastigheten */
    private double calcAverageSpeed() {
        double sum = 0;
        for (Double value : avgSpeed) {
            if(value != null) sum += value;
        }
        double averageSpeed = sum/avgSpeed.size();
        Log.d(TAG, "Medelhastighet " + averageSpeed  + " m/s"); //Loggar medelhastigheten de senaste 5000 metrarna
        if(avgSpeed.size() > 500) avgSpeed.clear();
        return averageSpeed;
    }

    public void resetValues(View view) {
        try {
            distanceInMetres = 0;
            speed = 0;
            avgSpeed.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    /**Gets the necessary permissions from the user or asks for them*/
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        //Check the permissions fine_location and coarse_location from the user
        //Ask for the coarse_location permission because it was not already granted
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                //Every permission is granted, initialize the map, request location updates every 10m
                initMap();
                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            } else {
                //Ask for the fine_location permission because it was not already granted
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Checking the results of the permission requests");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {

                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //All permissions are granted so we can initialize the map
                    initMap();
                }
            }
        }
    }

    /**Initializes the map*/
    private void initMap() {
        Log.d(TAG, "initMap: initializing the map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(MapsActivity.this);
    }
}
