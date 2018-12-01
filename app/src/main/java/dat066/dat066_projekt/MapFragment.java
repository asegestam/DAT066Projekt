package dat066.dat066_projekt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import android.support.v4.content.ContextCompat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private SpeedDistanceCalculator speedDistanceCalculator;
    LocationManager locationManager;
    DecimalFormat numberFormat = new DecimalFormat("#.00");
    private View view;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    long elapsedActivityTime = 0;
    long timeStopped = 0;
    LocationUpdater locationUpdater;
    private PolylineOptions routeOptions = new PolylineOptions().width(15).color(Color.BLUE).geodesic(true);
    private PolylineOptions resumeOptions = new PolylineOptions().width(20).color(Color.RED).geodesic(true);
    Polyline route;
    Polyline resumePolyline;
    private boolean followerModeEnabled;
    Snackbar saveSnackbar;
    private ArrayList<UserActivity> userActivities = new ArrayList<>();

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
        speedDistanceCalculator = new SpeedDistanceCalculator(this);
        locationUpdater = new LocationUpdater(speedDistanceCalculator);
        followerModeEnabled = true;
        saveSnackbar = Snackbar.make(view.findViewById(R.id.myCoordinatorLayout), R.string.save_activity, Snackbar.LENGTH_INDEFINITE);

        Button startButton = (Button) view.findViewById(R.id.start_button);
        ImageButton stopButton = (ImageButton) view.findViewById(R.id.stop_button);
        Button activityButton = (Button) view.findViewById(R.id.activity_button);
        ImageButton pauseButton = (ImageButton) view.findViewById(R.id.imageButtonPause);
        ImageButton resumeButton = (ImageButton) view.findViewById(R.id.imageButtonResume);
        pauseButton.setOnClickListener(pauseButtonClickListener);
        startButton.setOnClickListener(startButtonClickListener);
        resumeButton.setOnClickListener(resumeButtonClickListener);
        stopButton.setOnClickListener(stopButtonClickListener);
        activityButton.setOnClickListener(activityButtonOnClickListener);

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
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setPadding(0, 0, 0, 0);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(onMapClickListener);
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
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

    /** Zooms in at the given LatLng with a zoom of 18 */
    public void updateCamera(LatLng latLng) {
        if(followerModeEnabled) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
            mMap.animateCamera(cameraUpdate);
        }
    }

    /** Updates the TextViews to the current speed and distance */
    public void updateTextViews(Double distance, Double speed, long time) {
        TextView distanceText = view.findViewById(R.id.distanceText);
        TextView speedText = view.findViewById(R.id.speedText);
        distanceText.setText(numberFormat.format(distance) + " m");
        speedText.setText(numberFormat.format(speed) + " m/s");
    }

    private void resetValues() {
        elapsedActivityTime = 0;
        routeOptions.getPoints().clear();
        speedDistanceCalculator.resetValues();
        locationUpdater.setFirstLocation(null);
    }

    /** Request permission if not given and then requests location updates */
    private void requestLocationUpdates() {
        //Every permission is granted, initialize the map, request location updates every 5m
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationUpdater);
    }

    /** Draws a Polyline based on user movement */
    public void reDrawRoute(){
        route = mMap.addPolyline(routeOptions);
    }

    /**
     * Draws a PolyLine when resuming activity from the point the user clicked pause to the point the user pressed resume
     */
    private void drawResumeLine(Location lastKnownLocation){
        List<LatLng> latLngs = routeOptions.getPoints();
        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        locationUpdater.setLastLocation(null); //so the distance is not accounted for when resuming
        resumeOptions.add(latLngs.get(latLngs.size() - 1)).add(latLng);
        resumePolyline = mMap.addPolyline(resumeOptions);
        resumeOptions.getPoints().clear();
        routeOptions.getPoints().clear();
        routeOptions.add(latLng);
        requestLocationUpdates(); //enable Location updates like normal
    }

    public void addLatLngToRoute(LatLng latLng) {
        routeOptions.add(latLng);
    }

    /**
     * Starts the location updates to start calculating speed, distance
     * Shows and hides the relevant buttons, starts chronometer timer
     */
    public void startActivity() {
        setButtonVisibility(8);
        setTextViewVisibility(0);
        (view.findViewById(R.id.stop_button)).setVisibility(View.VISIBLE);
        mMap.clear(); // clears the map of all polylines and markers
        resetValues(); //resets all previous activity values to start recording a new one
        Toast.makeText(getActivity(), "Activity started", Toast.LENGTH_SHORT).show();
        requestLocationUpdates();
        saveSnackbar.dismiss();
        startTimer();
    }

    /**
     * Stops location updates
     * Shows and hides the relevant buttons
     * Resets the distance and speed calculated during the activity
     * Stops chronometer timer
     */
    public void stopActivity() {
        locationManager.removeUpdates(locationUpdater);
        setButtonVisibility(0);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.GONE);
        setTextViewVisibility(8);
        (view.findViewById(R.id.stop_button)).setVisibility(View.GONE);
        addMarkers();
        saveSnackbar.setAction(R.string.save_string, saveListener);
        saveSnackbar.show();
        Toast.makeText(getActivity(), "Activity stopped", Toast.LENGTH_SHORT).show();
        stopTimer();
    }

    /** Pauses the current active activity */
    public void pauseActivity() {
        (view.findViewById(R.id.imageButtonPause)).setVisibility(View.GONE);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.VISIBLE);
        locationManager.removeUpdates(locationUpdater);
        stopTimer();
    }

    /** Resumes the paused activity */
    public void resumeActivity() {
        (view.findViewById(R.id.imageButtonPause)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.GONE);
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        drawResumeLine(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        startTimer();
    }

    /** Adds two markers, one at the start location, one at the end location */
    private void addMarkers() {
        if(routeOptions.getPoints().size() != 0) {
            List<LatLng> latLngs = routeOptions.getPoints();
            mMap.addMarker(new MarkerOptions().title("Activity Ended Here").position(latLngs.get(latLngs.size() - 1)));
            mMap.addMarker(new MarkerOptions().title("Activity Started Here").position(locationUpdater.getFirstLocation()));
        }
    }
    /**
     * Changes the visibility of buttons depending on what code is given
     * VISIBLE = 0, INVISIBLE = 4, GONE = 8
     */
    private void setButtonVisibility(int visibility) {
        (view.findViewById(R.id.start_button)).setVisibility(visibility);
        (view.findViewById(R.id.activity_button)).setVisibility(visibility);
        (view.findViewById(R.id.button5)).setVisibility(visibility);
        int antiVisibility;
        if (visibility == 8) {
            antiVisibility = 0;
        } else {
            antiVisibility = 8;
        }
        (view.findViewById(R.id.imageButtonPause)).setVisibility(antiVisibility);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(visibility);
    }


    /** Sets textView visibility */
    private void setTextViewVisibility(int visibility) {
        TextView distanceText = view.findViewById(R.id.distanceText);
        TextView speedText = view.findViewById(R.id.speedText);
        TextView distanceWord = view.findViewById(R.id.distanceWord);
        TextView speedWord = view.findViewById(R.id.speedWord);
        View rectangleView = view.findViewById(R.id.rectangle);
        Chronometer time = view.findViewById(R.id.timeChronometer);
        if(distanceText.getVisibility() == visibility && speedText.getVisibility() == visibility) {
            return;
        }
        else {
            distanceText.setVisibility(visibility);
            speedText.setVisibility(visibility);
            distanceWord.setVisibility(visibility);
            speedWord.setVisibility(visibility);
            rectangleView.setVisibility(visibility);
            time.setVisibility(visibility);
        }
    }
    /** Saves the speed, distance, route, elapsed time of a activity in a instance of UserActivity */
    private void saveActivity() {
        Date currentTime = Calendar.getInstance().getTime();
        UserActivity userActivity = new UserActivity(speedDistanceCalculator.getAverageSpeed(), speedDistanceCalculator.getDistanceInMetres(), routeOptions,
                elapsedActivityTime/1000, 0.0, currentTime,locationUpdater.getFirstLocation());
        userActivities.add(userActivity);
        Toast.makeText(getActivity(), "Activity saved", Toast.LENGTH_SHORT).show();
        String logString = "\n" + userActivity.getUserSpeed() + " m/s\n" + userActivity.getUserDistanceMoved() + " m/s\n" + "size " + userActivity.getRoute().getPoints().size() + "\n" + userActivity.getActivityTime() + " s\n" + userActivity.getDateTime();
        Log.d(TAG, "userActivity: " + logString + "\n" + "number of saved activities " + userActivities.size());
    }

    private void startTimer() {
        Chronometer time = view.findViewById(R.id.timeChronometer);
        time.setBase(SystemClock.elapsedRealtime() + timeStopped);
        time.start();
    }

    private void stopTimer(){
        Chronometer time = view.findViewById(R.id.timeChronometer);
        timeStopped = time.getBase() - SystemClock.elapsedRealtime();
        time.stop();
    }

    /** Sets the onClickListeners to all relevant buttons */
    private View.OnClickListener startButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Chronometer time = view.findViewById(R.id.timeChronometer);
            timeStopped = 0;
            time.setBase(SystemClock.elapsedRealtime());
            startActivity();
        }
    };
    private View.OnClickListener pauseButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            pauseActivity();
        }
    };
    private View.OnClickListener resumeButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) { resumeActivity();
        }
    };
    private View.OnClickListener stopButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Chronometer time = view.findViewById(R.id.timeChronometer);
            elapsedActivityTime = SystemClock.elapsedRealtime() - time.getBase();
            Log.d(TAG, "chronometerElaspedTime " + elapsedActivityTime/1000);
            stopActivity();
        }
    };
    private View.OnClickListener activityButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ((MainActivity)getActivity()).showPopup();
        }
    };
    private GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            followerModeEnabled = false;
        }
    };

    private GoogleMap.OnMyLocationButtonClickListener  onMyLocationButtonClickListener = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            followerModeEnabled = true;
            return true;
        }
    };

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveActivity();
        }
    };

}
