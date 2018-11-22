package dat066.dat066_projekt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Polyline;

import android.support.v4.content.ContextCompat;
import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private SpeedDistanceCalculator speedDistanceCalculator;
    private static final String TAG = "MapFragment";
    LocationManager locationManager;
    DecimalFormat numberFormat = new DecimalFormat("#.00");
    private Polyline route;
    private View view;
    private boolean activityStopped;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        getLocationPermission();
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        speedDistanceCalculator = new SpeedDistanceCalculator();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!activityStopped) {
                        Thread.sleep(1000);
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextViews();
                                route = mMap.addPolyline(speedDistanceCalculator.getRouteOptions());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        Button startButton = (Button) view.findViewById(R.id.start_button);
        Button stopButton = (Button) view.findViewById(R.id.stop_button);
        Button activityButton = (Button) view.findViewById(R.id.activity_button);

        startButton.setOnClickListener(startButtonClickListener);
        stopButton.setOnClickListener(stopButtonClickListener);
        activityButton.setOnClickListener(activityButtonOnClickListener);
        thread.start();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("");
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
        this.mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setPadding(0,0,0,0);
        mMap.setMyLocationEnabled(true);
    }

    /**Gets the necessary permissions from the user or asks for them*/
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Every permission is granted, initialize the map
                initMap();
            } else {
                //Ask for the fine_location permission because it was not already granted
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {

                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    initMap();
                }
            }
        }
    }

    /**Initializes the map*/
    private void initMap() {
        Log.d(TAG, "initMap: initializing the map");
        mapFragment.getMapAsync(this);
    }

    /** Updates the TextViews to the current speed and distance */
    private void updateTextViews() {
        if(!activityStopped) {
            TextView distanceText = view.findViewById(R.id.distanceText);
            TextView speedText = view.findViewById(R.id.speedText);
            distanceText.setText("Distance " + numberFormat.format(speedDistanceCalculator.getDistanceInMetres()) + " m");
            //speedText.setText("Speed " + numberFormat.format((speedDistanceCalculator.getSpeed())*3.6)+ " km/h");
            speedText.setText("Pace " + numberFormat.format(speedDistanceCalculator.getSpeed()) + " m/s");
        }
    }

    public void resetValues() {
        speedDistanceCalculator.resetValues();
    }

    /** Request permission if not given and then requests location updates */
    private void requestLocationUpdates() {
        //Every permission is granted, initialize the map, request location updates every 10m
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, speedDistanceCalculator);
    }

    /**
     * Changes the visibility of buttons depending on what code is given
     * VISIBLE = 0, INVISIBLE = 4, GONE = 8
     */
    private void setButtonVisibility(int visibility){
        (view.findViewById(R.id.start_button)).setVisibility(visibility);
        (view.findViewById(R.id.activity_button)).setVisibility(visibility);
        (view.findViewById(R.id.button5)).setVisibility(visibility);
    }

    /**
     * Starts the location updates to start calculating speed, distance
     * Shows and hides the relevant buttons
     */
    public void startActivity() {
        activityStopped = false;
        requestLocationUpdates();
        setButtonVisibility(8);
        setTextViewVisibility(0);
        (view.findViewById(R.id.stop_button)).setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "Activity started", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stops location updates
     * Shows and hides the relevant buttons
     * Resets the distance and speed calculated during the activity
     */
    public void stopActivity() {
        activityStopped = true;
        locationManager.removeUpdates(speedDistanceCalculator);
        setButtonVisibility(0);
        setTextViewVisibility(8);
        (view.findViewById(R.id.stop_button)).setVisibility(View.GONE);
        resetValues();
        Toast.makeText(getActivity(), "Activity stopped", Toast.LENGTH_SHORT).show();
    }
    /** Sets textView visibility */
    private void setTextViewVisibility(int visibility) {
        TextView distanceText = view.findViewById(R.id.distanceText);
        TextView speedText = view.findViewById(R.id.speedText);
        if(distanceText.getVisibility() == visibility && speedText.getVisibility() == visibility) {
            return;
        }
        else {
            distanceText.setVisibility(visibility);
            speedText.setVisibility(visibility);
        }
    }
    /** Sets the onClickListeners to all relevant buttons */
    private View.OnClickListener startButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            startActivity();
        }
    };

    private View.OnClickListener stopButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            stopActivity();
        }
    };

    private View.OnClickListener activityButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ((MainActivity)getActivity()).showPopup();
        }
    };
}
