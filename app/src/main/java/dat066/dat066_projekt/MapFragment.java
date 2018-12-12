package dat066.dat066_projekt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import dat066.dat066_projekt.database.UserActivityEntity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.jjoe64.graphview.GraphView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    private SpeedDistanceCalculator speedDistanceCalculator;
    private ElevationUpdater elevationUpdater = ElevationUpdater.getInstance();
    private View view;
    long elapsedActivityTime = 0;
    long timeStopped = 0;
    private Location currentLocation;
    private Location lastLocation = null;
    private Location firstLocation = null;
    DecimalFormat numberFormat = new DecimalFormat("#.00");
    GraphView graph;
    Timer t;
    boolean activityStopped;
    boolean activityPaused;
    boolean followerModeEnabled;
    Snackbar saveSnackbar;
    ArrayList<LatLng> userMovement;
    ArrayList<ArrayList<LatLng>> listOfUserMovement;
    Polyline route;
    Polyline savedPolyline;
    double calories;
    private UserActivityViewModel activityViewModel;

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
        mapFragment.getMapAsync(this);
        initLocationCallback();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initGlobalVariables();
        initButtons();
        startLocationUpdates();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initGlobalVariables() {
        graph = (GraphView) view.findViewById(R.id.graph);
        speedDistanceCalculator = new SpeedDistanceCalculator(this);
        followerModeEnabled = true;
       //saveSnackbar = Snackbar.make(view.findViewById(R.id.myCoordinatorLayout), R.string.save_activity, Snackbar.LENGTH_INDEFINITE);
        userMovement = new ArrayList<>();
        listOfUserMovement = new ArrayList<>();
        route = null;
        savedPolyline = null;
        mLocationRequest = ((MainActivity)getActivity()).getLocationRequest();
        activityStopped = true;
        activityPaused = true;
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


    private void initLocationCallback() {
        mLocationCallback = new LocationCallback() {
            //här är location uppdateringar
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                currentLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "onLocationResult: location " + location.getLatitude()+ " " + location.getLongitude());
                if(lastLocation == null) {
                    lastLocation = location;
                }
                if(firstLocation == null) {
                    firstLocation = location;
                }
                if(!activityStopped && !activityPaused)  {
                    speedDistanceCalculator.handleLocationChange(location, lastLocation);
                    addLatLngToRoute(latLng);
                    updateCamera(latLng);
                    elevationUpdater.setLocation(location);
                    lastLocation = location;
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        ((MainActivity) getActivity()).changeActivityText();
        this.mMap = googleMap;
        if(checkPermissions()) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setPadding(0, 0, 0, 0);
        mMap.setOnMapClickListener(onMapClickListener);
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);;
        if(currentLocation !=null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18.0f);
            mMap.moveCamera(cameraUpdate);
        }
    }
    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
    }

    @SuppressLint("MissingPermission")
    public void stopLocationUpdates() {
        if(checkPermissions()) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
        lastLocation = null;
        firstLocation = null;
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
        reDrawRoute();
    }

    public void startActivity() {
        activityStopped = false;
        activityPaused = false;
        setButtonVisibility(8);
        setTextViewVisibility(0);
        (view.findViewById(R.id.stop_button)).setVisibility(View.VISIBLE);
        mMap.clear(); // clears the map of all polylines and markers
        resetValues(); //resets all previous activity values to start recording a new one
        Toast.makeText(getActivity(), "Activity started", Toast.LENGTH_SHORT).show();
       // saveSnackbar.dismiss();
        startTimer();
    }

    public void stopActivity() {
        activityStopped = true;
        activityPaused = true;
        setButtonVisibility(0);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.GONE);
        setTextViewVisibility(8);
        (view.findViewById(R.id.stop_button)).setVisibility(View.GONE);
        //saveSnackbar.setAction(R.string.save_string, saveListener);
       //saveSnackbar.show();
        Toast.makeText(getActivity(), "Activity stopped", Toast.LENGTH_SHORT).show();
        stopTimer();
        saveActivity();
        addMarkers();
    }

    /** Pauses the current active activity */
    public void pauseActivity() {
        (view.findViewById(R.id.imageButtonPause)).setVisibility(View.GONE);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.VISIBLE);
        activityPaused = true;
        stopTimer();
    }

    /** Resumes the paused activity */
    public void resumeActivity() {
        (view.findViewById(R.id.imageButtonPause)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.imageButtonResume)).setVisibility(View.GONE);
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activityPaused = false;
        addResumeMarkers();
        startTimer();
    }

    private void saveActivity() {
        Date currentTime = Calendar.getInstance().getTime();
        CaloriesBurned caloriesBurned = new CaloriesBurned(getContext());
        caloriesBurned.setTraining(((MainActivity) getActivity()).getType());
        calories = caloriesBurned.CalculateCalories(speedDistanceCalculator.getAverageSpeed(),elapsedActivityTime);
        Log.d(TAG, "stopActivity: KALORIER " + calories);
        saveUserMovement(userMovement);
        if(firstLocation != null) {
            LatLng firstLatLng = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
            UserActivityEntity userActivityEntity = new UserActivityEntity(0, currentTime.toString(), speedDistanceCalculator.getSpeed()
                    , calories, speedDistanceCalculator.getDistanceInMetres(), elapsedActivityTime);
            ViewModelProviders.of(getActivity()).get(UserActivityViewModel.class).insertActivity(userActivityEntity);
            Log.d(TAG, "saveActivity: insertActivity " + userActivityEntity.getDate());
            //userActivity.saveNote();
        }
    }

    private void saveUserMovement(ArrayList<LatLng> list) {
        listOfUserMovement.add(list);
        Log.d(TAG, "saveActivityRoutes: size av saved rutts " + listOfUserMovement.size());
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
            saveUserMovement(userMovement);
            Polyline polyline = mMap.addPolyline(new PolylineOptions().width(15).color(Color.BLUE).geodesic(true).addAll(userMovement));
            userMovement.clear();
            userMovement.add(latLng);
            lastLocation = null;
        }
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
                ((MainActivity)getActivity()).setActivityStopped(false);
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
            ((MainActivity)getActivity()).setActivityStopped(true);
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

    /** Graph plot stuff */
    @Override
    public void onResume() {
        Log.d(TAG, "onResume: mapfrag");
        if(currentLocation !=null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            updateCamera(latLng);
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: mapfrag");
        if(currentLocation !=null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            updateCamera(latLng);
        }
        super.onStart();
    }

    @Override
        public void onStop() {
        Log.d(TAG, "onStop: mapfrag");
        activityPaused = true;
        activityStopped = true;
        ((MainActivity)getActivity()).setActivityStopped(true);
        super.onStop();
    }
}
