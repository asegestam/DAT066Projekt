package dat066.dat066_projekt;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.GraphView;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import dat066.dat066_projekt.database.UserActivityEntity;
import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private SpeedDistanceCalculator speedDistanceCalculator;
    private ElevationUpdater elevationUpdater = ElevationUpdater.getInstance();
    private View view;
    private long elapsedActivityTime = 0;
    private long timeStopped = 0;
    private Location currentLocation = null;
    private Location lastLocation = null;
    private Location firstLocation = null;
    private Location initLocation = null;
    Chronometer time;
    private DecimalFormat numberFormat = new DecimalFormat("#.00");
    GraphView graph;
    public int id = 0;
    boolean activityStopped;
    boolean activityPaused;
    boolean followerModeEnabled;
    Snackbar saveSnackbar;
    ArrayList<ArrayList<LatLng>> listOfUserMovement;
    Polyline route;
    Polyline savedPolyline;
    double calories;
    private UserActivityViewModel activityViewModel;
    private ArrayList<LatLng> userMovement = new ArrayList<>();
    private ArrayList<Location> userLocations = new ArrayList<>();
    private ArrayList speedArray = new ArrayList<>();
    private ArrayList timeArray = new ArrayList<>();
    TextView distanceText;
    TextView speedText;
    private LocationViewModel locationViewModel;
    private SharedPreferences sharedPreferences;
    CaloriesBurned caloriesBurned;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        caloriesBurned = new CaloriesBurned(getContext());
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initGlobalVariables();
        initButtons();
        locationViewModel = ViewModelProviders.of(getActivity()).get(LocationViewModel.class);
        locationViewModel.getLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(TAG, "onChanged: location updated");
                if(location != null){
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    currentLocation = location;
                    if(firstLocation == null) {
                        firstLocation = location;
                        moveCamera(latLng);
                    }
                    if(lastLocation == null) {
                        lastLocation = location;
                    }
                    if(!activityStopped && !activityPaused) {
                        userMovement.add(latLng);
                        reDrawRoute();
                        updateCamera(latLng);
                        timeArray.add(SystemClock.elapsedRealtime() - time.getBase());
                        speedDistanceCalculator.handleLocationChange(location, lastLocation);
                        speedArray.add(speedDistanceCalculator.getSpeed());
                        updateTextViews();
                        elevationUpdater.setLocation(currentLocation);
                        plotGraph();
                        lastLocation = location;
                    }
                }
            }
        });
        locationViewModel.getListOfLocations().observe(this, new Observer<ArrayList<Location>>() {
            @Override
            public void onChanged(ArrayList<Location> locations) {
                if (!locations.isEmpty()) {
                    lastLocation = locations.get(0);
                    ArrayList<LatLng> latLngs = new ArrayList<>();
                    for (Location location : locations) {
                       speedDistanceCalculator.handleLocationChange(location, lastLocation);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (!userMovement.contains(latLng)) {
                            latLngs.add(latLng);
                        }
                        lastLocation = location;
                    }
                    Log.i(TAG, "onChanged: list of locations size " + latLngs.size());
                    userMovement.addAll(latLngs);
                    lastLocation = null;
                    currentLocation = null;
                    latLngs.clear();
                }
            }
        });

        locationViewModel.getLastKnownLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                moveCamera(latLng);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initGlobalVariables() {
        speedDistanceCalculator = SpeedDistanceCalculator.getInstance();
        followerModeEnabled = true;
       //saveSnackbar = Snackbar.make(view.findViewById(R.id.myCoordinatorLayout), R.string.save_activity, Snackbar.LENGTH_INDEFINITE);
        route = null;
        distanceText = view.findViewById(R.id.distanceText);
        speedText = view.findViewById(R.id.speedText);
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


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: map rdy");
        ((MainActivity) getActivity()).changeActivityText();
        this.mMap = googleMap;

        if(checkPermissions()) {
            mMap.setMyLocationEnabled(true);

        }
        mMap.setPadding(0, 0, 0, 0);
        mMap.setOnMapClickListener(onMapClickListener);
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void updateTextViews() {
        TextView distanceText = view.findViewById(R.id.distanceText);
        TextView speedText = view.findViewById(R.id.speedText);
        double distance = speedDistanceCalculator.getDistanceInMetres();
        if(distance > 1000) {
            distance = distance/1000;
            TextView distanceM = view.findViewById(R.id.distanceM);
            TextView distanceKm = view.findViewById(R.id.distanceKm);
            distanceM.setVisibility(View.GONE);
            distanceKm.setVisibility(View.VISIBLE);
        }
        distanceText.setText(numberFormat.format(distance));
        speedText.setText(numberFormat.format(speedDistanceCalculator.getAveragePace()));
    }

    private void updateCamera(LatLng latLng) {
        if(followerModeEnabled) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18.0f);
            mMap.animateCamera(cameraUpdate);
        }
    }

    private void moveCamera(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18.0f);
        mMap.moveCamera(cameraUpdate);
    }

    private void resetValues() {
        elapsedActivityTime = 0;
        userMovement.clear();
        elevationUpdater.getElevationArray().clear();
        speedDistanceCalculator.resetValues();
        route = null;
        lastLocation = null;
        firstLocation = null;
        userLocations.clear();
        userMovement.clear();
        locationViewModel.getLastLocation().setValue(null);
        locationViewModel.getFirstLocation().setValue(null);
        locationViewModel.getLocation().setValue(null);
    }

    public void reDrawRoute(){
        if(route != null) {
            route.setPoints(userMovement);
        }
        else {
            route = mMap.addPolyline(new PolylineOptions().width(15).color(getActivity().getColor(R.color.colorAccent)).geodesic(true).addAll(userMovement));
        }

    }

    private void addLatLngToRoute(LatLng latLng) {
        Log.d(TAG, "addLatLngToRoute: updating route");
        userMovement.add(latLng);
        reDrawRoute();
    }

    private void startActivity() {
        activityStopped = false;
        ((MainActivity)getActivity()).requestLocationUpdates();
        initActivityUI();
        mMap.clear(); // clears the map of all polylines and markers
        resetValues(); //resets all previous activity values to start recording a new one
        Toast.makeText(getActivity(), "Activity started", Toast.LENGTH_SHORT).show();
        followerModeEnabled = true;
        startTimer();
    }

    private double totalCalories(String training, Location[] locations, Double[] elevations, int counter){
        if(counter < locations.length - 1)
            return caloriesBurned.CalculateCalories((((MainActivity) getActivity()).getType()),
                    calcTime(locations[counter], locations[counter + 1]),
                    calcSpeed(locations[counter], locations[counter + 1]),
                    calcElevationAngel(locations[counter], locations[counter + 1],elevations[counter], elevations[counter + 1]))
                    + totalCalories(training, locations, elevations, counter + 1);
        else
        return 0;
    }

    private double calcTime(Location loc1, Location loc2){
        return (double)((loc2.getTime() - loc1.getTime())/1000);
    }

    private double calcSpeed(Location loc1, Location loc2){
        double distance = (double)loc1.distanceTo(loc2);
        double speed = distance/calcTime(loc1, loc2);
        return speed*3.6;
    }

    private double calcElevationAngel(Location loc1, Location loc2, double ele1, double ele2){
        double height = ele2 - ele1;
        double base = (double)loc1.distanceTo(loc2);

        Log.d(TAG, "calcElevationAngel: " + (Math.toDegrees(Math.atan2(height, base)))/100);
        return (Math.toDegrees(Math.atan2(height, base))/100);
    }

    private void stopActivity() {
        activityStopped = true;
        ((MainActivity)getActivity()).removeLocationUpdates();
        initIdleUI();
        Toast.makeText(getActivity(), "Activity stopped", Toast.LENGTH_SHORT).show();
        stopTimer();
        saveActivity();
        addMarkers();
        plotGraph();
        resetValues();
    }

    /** Pauses the current active activity */
    private void pauseActivity() {
        (view.findViewById(R.id.imageButtonPause)).setVisibility(View.GONE);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.VISIBLE);
        activityPaused = true;
        stopTimer();
    }

    /** Resumes the paused activity */
    private void resumeActivity() {
        (view.findViewById(R.id.imageButtonPause)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.GONE);
        activityPaused = false;
        addResumeMarkers();
        followerModeEnabled = true;
        startTimer();
    }

    private void saveActivity() {
        Date myDate = Calendar.getInstance().getTime();
        String currentTime = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(myDate);
        //double calories = caloriesBurned.CalculateCalories(speedDistanceCalculator.getAverageSpeed(), elapsedActivityTime);
        Log.i(TAG, "saveActivity: SIZE" + userLocations.toString());
        double calories = totalCalories(((MainActivity) getActivity()).getType(),
                            userLocations.toArray(new Location[userLocations.size()]),
                            elevationUpdater.getElevationArray().toArray(new Double[elevationUpdater.getElevationArray().size()]),
                            0);
        Log.d(TAG, "stopActivity: KALORIER " + calories);
        if(firstLocation != null) {
            LatLng firstLatLng = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
            UserActivityEntity userActivityEntity = new UserActivityEntity(0,
                    currentTime,
                    speedDistanceCalculator.getHighestSpeed(),
                    speedDistanceCalculator.getAveragePace(),
                    calories,
                    speedDistanceCalculator.getDistanceInMetres(),
                    elapsedActivityTime,
                    speedArray,
                    elevationUpdater.getElevationArray(),
                    timeArray);
            ViewModelProviders.of(getActivity()).get(UserActivityViewModel.class).insertActivity(userActivityEntity);
            Log.d(TAG, "saveActivity: insertActivity " + userActivityEntity.getDate());
        }
    }

    public void plotGraph() {
        double elevation = elevationUpdater.getEle();

        if(elevation > 0){
            elevationUpdater.elevationArray.add(elevation);
            userLocations.add(currentLocation);

        }else if(elevation != 0.0) {
                elevationUpdater.elevationArray.add(elevation);
                userLocations.add(currentLocation);
            }
    }

    /** Adds two markers, one at the start location, one at the end location */

    private void addMarkers() {
        if(userMovement.size() != 0) {
            LatLng firstLatLng = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().title("Activity Ended Here").
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())));
            mMap.addMarker(new MarkerOptions().title("Activity Started Here").
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(firstLatLng));
        }
    }

    private void addResumeMarkers(){
        if(!userMovement.isEmpty()) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Marker pasueMarker = mMap.addMarker(new MarkerOptions().
                    position(userMovement.get(userMovement.size() - 1)).
                    title("Activity paused here").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_grey_pin)));
            Marker unpauseMarker = mMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("Activity unpaused here").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_grey_pin)));
            //saveUserMovement(userMovement);
            Polyline polyline = mMap.addPolyline(new PolylineOptions().width(15).color(getActivity().getColor(R.color.colorAccent)).geodesic(true).addAll(userMovement));
            userMovement.clear();
            userMovement.add(latLng);
            lastLocation = null;
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

    private void initIdleUI() {
        //buttons
        view.findViewById(R.id.start_button).setVisibility(View.VISIBLE);
        view.findViewById(R.id.activity_button).setVisibility(View.VISIBLE);
        view.findViewById(R.id.imageButtonPause).setVisibility(View.GONE);
        view.findViewById(R.id.stop_button).setVisibility(View.GONE);
        view.findViewById(R.id.imageButtonResume).setVisibility(View.GONE);
        //textview
        view.findViewById(R.id.distanceText).setVisibility(View.GONE);
        view.findViewById(R.id.speedText).setVisibility(View.GONE);
        view.findViewById(R.id.distanceM).setVisibility(View.GONE);
        view.findViewById(R.id.speedWord).setVisibility(View.GONE);
        view.findViewById(R.id.rectangle).setVisibility(View.GONE);
        view.findViewById(R.id.timeChronometer).setVisibility(View.GONE);
    }

    private void initActivityUI() {
        view.findViewById(R.id.start_button).setVisibility(View.GONE);
        view.findViewById(R.id.activity_button).setVisibility(View.GONE);

        view.findViewById(R.id.distanceText).setVisibility(View.VISIBLE);
        view.findViewById(R.id.speedText).setVisibility(View.VISIBLE);
        view.findViewById(R.id.distanceM).setVisibility(View.VISIBLE);
        view.findViewById(R.id.speedWord).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rectangle).setVisibility(View.VISIBLE);
        view.findViewById(R.id.timeChronometer).setVisibility(View.VISIBLE);
        view.findViewById(R.id.stop_button).setVisibility(View.VISIBLE);

        if(!activityPaused) {
            Log.d(TAG, "initActivityUI: activity not paused");
            view.findViewById(R.id.imageButtonPause).setVisibility(View.VISIBLE);
            view.findViewById(R.id.imageButtonResume).setVisibility(View.GONE);

        } else {
            Log.d(TAG, "initActivityUI: activity paused");

            view.findViewById(R.id.imageButtonPause).setVisibility(View.GONE);
            view.findViewById(R.id.imageButtonResume).setVisibility(View.VISIBLE);
        }
    }
    private void initSavedUI() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("ui_state", Context.MODE_PRIVATE);
        activityStopped = sharedPref.getBoolean("activitystopped", true);
        activityPaused = sharedPref.getBoolean("activitypaused", true);
        //activity
        if(!activityStopped) {
            initActivityUI();
            Chronometer chronometer = view.findViewById(R.id.timeChronometer);
            long savedTime = sharedPref.getLong("chronometer_base", 0);
            Log.d(TAG, "initSavedUI: time " + savedTime/1000);
            //locationViewModel.ge(Double.longBitsToDouble(sharedPref.getLong("distance_moved", 0)));
          //  locationViewModel.setAverageSpeed(Double.longBitsToDouble(sharedPref.getLong("average_speed", 0)));
            chronometer.setBase(savedTime);
            chronometer.start();
        }
    }
/*
    private void saveUiState() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("ui_state", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Chronometer chronometer = view.findViewById(R.id.timeChronometer);
        double distance = locationViewModel.getDistanceInMetres().getValue();
        double avgSpeed = locationViewModel.getAverageSpeed().getValue();
        Log.d(TAG, "saveUiState: distance saved " + distance);
        Log.d(TAG, "saveUiState: speed saved " + avgSpeed);
        Log.d(TAG, "saveUiState: activitystopped " + activityStopped);
        editor.putBoolean("activitystopped", activityStopped);
        editor.putBoolean("activitypaused", activityPaused);
        editor.putLong("chronometer_base", chronometer.getBase());
        editor.putLong("current_time", SystemClock.elapsedRealtime());
        editor.putLong("distance_moved" , Double.doubleToRawLongBits(distance));
        editor.putLong("average_speed" , Double.doubleToRawLongBits(avgSpeed));
        editor.apply();
    }
    */


    /** Sets the onClickListeners to all relevant buttons */
    private View.OnClickListener startButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(((MainActivity) getActivity()).getType() == null)
            {
                Toast.makeText(getActivity(), "Please select Type of Activity", Toast.LENGTH_SHORT).show();
            }
            else {
                time = view.findViewById(R.id.timeChronometer);
                timeStopped = 0;
                time.setBase(SystemClock.elapsedRealtime());
                elevationUpdater.setActivityStopped(false);
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
            elevationUpdater.setActivityStopped(true);
            stopActivity();
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

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: mapfrag");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: mapfrag");
        super.onResume();
        ((MainActivity)getActivity()).getLastKnownLocation();
       // initSavedUI();

    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: mapfrag");
        super.onStop();
       // saveUiState();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: mapfrag destoryed");
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("ui_state", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        super.onDestroy();
    }
}
