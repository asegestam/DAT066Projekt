package dat066.dat066_projekt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.DecimalFormat;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private Thread thread;
    private SpeedDistanceCalculator speedDistanceCalculator;
    private static final String TAG = "MapFragment";
    LocationManager locationManager;
    DecimalFormat numberFormat = new DecimalFormat("#.00");
    private boolean activityRunning;
    private View view;


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
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        speedDistanceCalculator = new SpeedDistanceCalculator();
        activityRunning = false;
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (activityRunning)
                                    updateTextViews();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        Button startButton = (Button) view.findViewById(R.id.start_button);
        Button stopButton = (Button) view.findViewById(R.id.stop_button);
        startButton.setOnClickListener(startButtonClickListener);
        stopButton.setOnClickListener(stopButtonClickListener);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Map-Fragment");
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void updateTextViews() {
        TextView distanceText = view.findViewById(R.id.distanceText);
        TextView speedText = view.findViewById(R.id.speedText);
        distanceText.setText("Distance " + numberFormat.format(speedDistanceCalculator.getDistanceInMetres()) + " m");
        //speedText.setText("Speed " + numberFormat.format((speedDistanceCalculator.getSpeed())*3.6)+ " km/h");
        speedText.setText("Pace " + numberFormat.format(speedDistanceCalculator.getSpeed()) + " m/s");
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
        (view.findViewById(R.id.button4)).setVisibility(visibility);
        (view.findViewById(R.id.button5)).setVisibility(visibility);
    }

    /**
     * Starts the location updates to start calculating speed, distance
     * Shows and hides the relevant buttons
     */
    public void startActivity() {
        thread.start();
        activityRunning = true;
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
        thread.interrupt();
        activityRunning = false;
        locationManager.removeUpdates(speedDistanceCalculator);
        setButtonVisibility(0);
        setTextViewVisibility(8);
        (view.findViewById(R.id.stop_button)).setVisibility(View.GONE);
        resetValues();
        Toast.makeText(getActivity(), "Activity stopped", Toast.LENGTH_SHORT).show();
    }

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
}
