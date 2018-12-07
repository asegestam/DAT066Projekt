package dat066.dat066_projekt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.jjoe64.graphview.GraphView;
import android.support.v4.content.ContextCompat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.Date;
import java.util.List;
import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private SpeedDistanceCalculator speedDistanceCalculator;
    private View view;
    long elapsedActivityTime = 0;
    long timeStopped = 0;
    LocationManager locationManager;
    Location location = null;
    LocationUpdater locationUpdater;
    DecimalFormat numberFormat = new DecimalFormat("#.00");
    GraphView graph;
    OnPlotDataListener mCallback;
    Timer t;
    boolean activityStopped;
    boolean followerModeEnabled;
    Snackbar saveSnackbar;
    ArrayList<Double> elevationArray;
    ArrayList<LatLng> userMovement;
    ArrayList<ArrayList<LatLng>> listOfUserMovement;
    Polyline route;
    Polyline savedPolyline;
    Button startButton;
    ImageButton stopButton;
    Button activityButton;
    ImageButton pauseButton;
    ImageButton resumeButton;


    @SuppressLint("MissingPermission")
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
        initGlobalVariables();
        initButtons();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    private void initGlobalVariables() {
        graph = (GraphView) view.findViewById(R.id.graph);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        speedDistanceCalculator = new SpeedDistanceCalculator(this);
        locationUpdater = new LocationUpdater(speedDistanceCalculator);
        followerModeEnabled = true;
        saveSnackbar = Snackbar.make(view.findViewById(R.id.myCoordinatorLayout), R.string.save_activity, Snackbar.LENGTH_INDEFINITE);
        userMovement = new ArrayList<>();
        listOfUserMovement = new ArrayList<>();
        route = null;
        savedPolyline = null;
    }

    private void initButtons() {
        Button startButton = view.findViewById(R.id.start_button);
        ImageButton stopButton =  view.findViewById(R.id.stop_button);
        Button activityButton = view.findViewById(R.id.activity_button);
        ImageButton pauseButton =  view.findViewById(R.id.imageButtonPause);
        ImageButton resumeButton = view.findViewById(R.id.imageButtonResume);
        pauseButton.setOnClickListener(pauseButtonClickListener);
        startButton.setOnClickListener(startButtonClickListener);
        resumeButton.setOnClickListener(resumeButtonClickListener);
        stopButton.setOnClickListener(stopButtonClickListener);
        activityButton.setOnClickListener(activityButtonOnClickListener);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        ((MainActivity) getActivity()).changeActivityText();
        this.mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        elevationArray = new ArrayList<>();
        location = locationUpdater.getLastLocation();
        mMap.setPadding(0, 0, 0, 0);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(onMapClickListener);
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(userLocation !=null){
            LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            updateCamera(latLng);
        }
    }

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

    private void requestLocationUpdates() {
        //Every permission is granted, initialize the map, request location updates every 5m
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationUpdater);
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing the map");
        mapFragment.getMapAsync(this);
    }

    public void updateCamera(LatLng latLng) {
        if(followerModeEnabled) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18.0f);
            mMap.animateCamera(cameraUpdate);
        }
    }

    public void updateTextViews(Double distance, Double speed, long time) {
        TextView distanceText = view.findViewById(R.id.distanceText);
        TextView speedText = view.findViewById(R.id.speedText);
        distanceText.setText(numberFormat.format(distance) + " m");
        speedText.setText(numberFormat.format(speed) + " m/s");
    }

    private void resetValues() {
        elapsedActivityTime = 0;
        userMovement.clear();
        speedDistanceCalculator.resetValues();
        route = null;
        listOfUserMovement.clear();
        locationUpdater.setFirstLocation(null);
        locationUpdater.setLastLocation(null);
    }

    public void reDrawRoute(){
        if(route != null) {
            route.setPoints(userMovement);
        }
        else {
            route = mMap.addPolyline(new PolylineOptions().width(15).color(Color.BLUE).geodesic(true).addAll(userMovement));
        }
    }
    public void addLatLngToRoute(LatLng latLng) {
        userMovement.add(latLng);
        Log.d(TAG, "addLatLngToRoute: ACTUAL USERMOVMENT SIZE " + userMovement.size());
        reDrawRoute();
    }
    public void startActivity() {
        activityStopped = false;
        t = new Timer();
        TimerTask tTask = new TimerTask() {
            @Override
            public void run() {
                plotGraph();
            }
        };
        setButtonVisibility(8);
        setTextViewVisibility(0);
        (view.findViewById(R.id.stop_button)).setVisibility(View.VISIBLE);
        mMap.clear(); // clears the map of all polylines and markers
        resetValues(); //resets all previous activity values to start recording a new one
        Toast.makeText(getActivity(), "Activity started", Toast.LENGTH_SHORT).show();
        saveSnackbar.dismiss();
        startTimer();
        requestLocationUpdates();
        t.schedule(tTask, 1000,5000);
    }

    public void stopActivity() {
        locationManager.removeUpdates(locationUpdater);
        activityStopped = true;
        t.cancel();
        t.purge();
        setButtonVisibility(0);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.GONE);
        setTextViewVisibility(8);
        (view.findViewById(R.id.stop_button)).setVisibility(View.GONE);
        //saveSnackbar.setAction(R.string.save_string, saveListener);
       // saveSnackbar.show();
        Toast.makeText(getActivity(), "Activity stopped", Toast.LENGTH_SHORT).show();
        stopTimer();
        saveActivity();
        addMarkers();
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
        addResumeMarkers(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        startTimer();
    }

    private void saveActivity() {
        Date currentTime = Calendar.getInstance().getTime();
        CaloriesBurned caloriesBurned = new CaloriesBurned(getContext());
        caloriesBurned.setTraining(((MainActivity) getActivity()).getType());
        double d = caloriesBurned.CalculateCalories(speedDistanceCalculator.getAverageSpeed(),elapsedActivityTime);
        Log.d(TAG, "stopActivity: KALORIER" + d);
        saveUserMovement(userMovement);
        if(userMovement != null) {
            UserActivity userActivity = new UserActivity(speedDistanceCalculator.getAverageSpeed(), speedDistanceCalculator.getDistanceInMetres(), listOfUserMovement,
                    elapsedActivityTime / 1000, d, currentTime, locationUpdater.getFirstLocation(), elevationArray);
            ((MainActivity) getActivity()).saveUserActivity(userActivity);
        }
    }


    private void saveUserMovement(ArrayList<LatLng> list) {
        listOfUserMovement.add(list);
        Log.d(TAG, "saveActivityRoutes: size av saved rutts " + listOfUserMovement.size());
    }

    /** Adds two markers, one at the start location, one at the end location */
    private void addMarkers() {
        if(userMovement.size() != 0) {
            mMap.addMarker(new MarkerOptions().title("Activity Ended Here").
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(userMovement.get(userMovement.size() - 1)));
            mMap.addMarker(new MarkerOptions().title("Activity Started Here").
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(locationUpdater.getFirstLocation()));
        }
    }
    private void addResumeMarkers(Location lastKnownLocation){
        if(!userMovement.isEmpty()) {
            LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            Marker pasueMarker = mMap.addMarker(new MarkerOptions().
                    position(userMovement.get(userMovement.size() - 1)).
                    title("Activity paused here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            Marker unpauseMarker = mMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("Activity unpaused here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            saveUserMovement(userMovement);
            Polyline polyline = mMap.addPolyline(new PolylineOptions().width(15).color(Color.BLUE).geodesic(true).addAll(userMovement));
            userMovement.clear();
            userMovement.add(latLng);
            locationUpdater.setLastLocation(null);
        }
        requestLocationUpdates(); //enable Location updates like normal
    }
    /**
     * Changes the visibility of buttons depending on what code is given
     * VISIBLE = 0, INVISIBLE = 4, GONE = 8
     */
    private void setButtonVisibility(int visibility) {
        (view.findViewById(R.id.start_button)).setVisibility(visibility);
        (view.findViewById(R.id.activity_button)).setVisibility(visibility);
        int antiVisibility;
        if (visibility == 8) {
            antiVisibility = 0;
        } else {
            antiVisibility = 8;
        }
        (view.findViewById(R.id.imageButtonPause)).setVisibility(antiVisibility);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(visibility);
    }

    private void setTextViewVisibility(int visibility) {
        TextView distanceText = view.findViewById(R.id.distanceText);
        TextView speedText = view.findViewById(R.id.speedText);
        TextView distanceWord = view.findViewById(R.id.distanceWord);
        TextView speedWord = view.findViewById(R.id.speedWord);
        View rectangleView = view.findViewById(R.id.rectangle);
        Chronometer time = view.findViewById(R.id.timeChronometer);
        if(distanceText.getVisibility() == visibility && speedText.getVisibility() == visibility) {
            return;
        } else {
            distanceText.setVisibility(visibility);
            speedText.setVisibility(visibility);
            distanceWord.setVisibility(visibility);
            speedWord.setVisibility(visibility);
            rectangleView.setVisibility(visibility);
            time.setVisibility(visibility);
        }
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
            if(((MainActivity) getActivity()).getType() == null)
            {
                Toast.makeText(getActivity(), "Please select Type of Activity", Toast.LENGTH_SHORT).show();
            }
            else {
                Chronometer time = view.findViewById(R.id.timeChronometer);
                timeStopped = 0;
                time.setBase(SystemClock.elapsedRealtime());
                startActivity();
            }
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
            if(locationUpdater.getFirstLocation() != null &&  locationUpdater.getLastLocation() != null) {
                plotGraph();
            }
        }
    };
    private View.OnClickListener activityButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ((MainActivity) getActivity()).showPopup();
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
            return false;
        }
    };

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //saveActivity();
        }
    };

    /** Graph plot stuff */

    public void plotGraph() {
        if(this.activityStopped) {
            if(locationUpdater.getEle() > 0)
                elevationArray.add(locationUpdater.getEle());
            mCallback.onDataGiven(elevationArray);
        }else{
            if(locationUpdater.getEle() != 0.0) {
                elevationArray.add(locationUpdater.getEle());
                Log.e(TAG, "Added: " + locationUpdater.getEle() + " in MapFragment!");
            }
        }
    }

    public void setOnPlotDataListener(Activity activity){
        mCallback = (OnPlotDataListener) activity;
    }

    public interface OnPlotDataListener{
        public void onDataGiven(ArrayList elevation);
    }
}
