package dat066.dat066_projekt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.security.PrivateKey;
import java.text.DecimalFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    private Thread thread;
    private SpeedDistanceCalculator speedDistanceCalculator;

    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;



    LocationManager locationManager;
    DecimalFormat numberFormat = new DecimalFormat("#.00");
    private boolean activityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        speedDistanceCalculator = new SpeedDistanceCalculator();
        activityRunning = false;

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(activityRunning)
                                updateTextViews();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
        getLocationPermission();
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

    private void updateTextViews() {
        TextView distanceText = findViewById(R.id.distanceText);
        TextView speedText = findViewById(R.id.speedText);
        distanceText.setText("Distance " + numberFormat.format(speedDistanceCalculator.getDistanceInMetres())+ " m");
        //speedText.setText("Speed " + numberFormat.format((speedDistanceCalculator.getSpeed())*3.6)+ " km/h");
        speedText.setText("Pace " + numberFormat.format(speedDistanceCalculator.getSpeed())+ " m/s");
    }

    public void resetValues() {
        speedDistanceCalculator.resetValues();
    }

    /** Request permission if not given and then requests location updates */
    private void requestLocationUpdates() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        //Check the permissions fine_location and coarse_location from the user
        //Ask for the coarse_location permission because it was not already granted
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                //Every permission is granted, initialize the map, request location updates every 10m
                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 5, speedDistanceCalculator);
            } else {
                //Ask for the fine_location permission because it was not already granted
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Changes the visibility of buttons depending on what code is given
     * VISIBLE = 0, INVISIBLE = 4, GONE = 8
     */
    private void setButtonVisibility(int visibility){
        (findViewById(R.id.start_button)).setVisibility(visibility);
        (findViewById(R.id.button4)).setVisibility(visibility);
        (findViewById(R.id.button5)).setVisibility(visibility);
    }

    /**
     * Starts the location updates to start calculating speed, distance
     * Shows and hides the relevant buttons
     */
    public void startActivity(View view) {
        activityRunning = true;
        requestLocationUpdates();
        setButtonVisibility(8);
        setTextViewVisibility(0);
        (findViewById(R.id.stop_button)).setVisibility(View.VISIBLE);
        Toast.makeText(this, "Activity started", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stops location updates
     * Shows and hides the relevant buttons
     * Resets the distance and speed calculated during the activity
     */
    public void stopActivity(View view) {
        activityRunning = false;
        locationManager.removeUpdates(speedDistanceCalculator);
        setButtonVisibility(0);
        setTextViewVisibility(8);
        (findViewById(R.id.stop_button)).setVisibility(View.GONE);
        resetValues();
        Toast.makeText(this, "Activity stopped", Toast.LENGTH_SHORT).show();
    }

    private void setTextViewVisibility(int visibility) {
        TextView distanceText = findViewById(R.id.distanceText);
        TextView speedText = findViewById(R.id.speedText);
        if(distanceText.getVisibility() == visibility && speedText.getVisibility() == visibility) {
            return;
        }
        else {
            distanceText.setVisibility(visibility);
            speedText.setVisibility(visibility);
        }

    }
}
