package dat066.dat066_projekt;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener, MapFragment.OnPlotDataListener {
    private static final String TAG = "MainActivity";
    private String type;
    private boolean activityStopped;
    private final int REQUEST_CHECK_SETTINGS = 0; // a unique identifier
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 1500; /* 2 sec */
    StatsFragment statsFragment;
    MapFragment mapFragment;
    ProfileFragment profileFragment;
    UserActivityList userActivityList;
    SettingsFragment settingsFragment;
    GoalsFragment goalsFragment;
    ArrayList<Fragment> fragments;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        activityStopped = true;
        createLocationRequest();
        if(savedInstanceState == null) {
            mapFragment = new MapFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.content_main, mapFragment, "map").commit();
        } else {
            mapFragment = (MapFragment)getSupportFragmentManager().findFragmentByTag("map");
        }
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        final Task<LocationSettingsResponse> locationSettingsResponseTask = task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                mapFragment.startLocationUpdates();
            }
        });
        Task<LocationSettingsResponse> locationSettingsResponseTask1 = task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(!activityStopped) {
            Log.d(TAG, "onBackPressed: ALERT");
            showAlertDialog().show();
        }
        else if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        setFragment(id);

        return true;
    }

    public void setFragment(int id){
        switch(id) {
            case R.id.activity_option:
                if(mapFragment == null) mapFragment = new MapFragment();
                switchFragment(mapFragment);
                break;

            case R.id.profile_option:
                if(profileFragment == null) profileFragment = new ProfileFragment();
                switchFragment(profileFragment);
                break;

            case R.id.settings_option:
                if(settingsFragment == null) settingsFragment = new SettingsFragment();
                switchFragment(settingsFragment);
                 break;

            case R.id.stats_option:
                if(statsFragment == null) statsFragment = new StatsFragment();
                switchFragment(statsFragment);
                break;

            case R.id.saved_activities_option:
                if(userActivityList == null) userActivityList = new UserActivityList();
                switchFragment(userActivityList);
                break;

            case R.id.goal_option:
                if(goalsFragment == null) goalsFragment = new GoalsFragment();
                switchFragment(goalsFragment);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    public void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment).addToBackStack(null).commit();
    }

    public void showPopup() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.activity_button));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.run:
                type = item.getTitle().toString();
                item.setChecked(true);
                changeActivityText();

                break;
            case R.id.bike:
                type = item.getTitle().toString();
                item.setChecked(true);
                changeActivityText();
                break;

        }
        return true;
    }

    public void changeActivityText(){
        if(getType() != null) {
            Button button = (Button) findViewById(R.id.activity_button);
            button.setText("Type of Activity (" + getType() + ")");
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment){
        if(fragment instanceof MapFragment){
            MapFragment mapFragment = (MapFragment) fragment;
            mapFragment.setOnPlotDataListener(this);
        }
    }

    @Override
    public void onDataGiven(ArrayList elevation) {
        StatsFragment statsFrag = (StatsFragment)
                getSupportFragmentManager().findFragmentById(R.id.statistics);

        if (statsFrag != null) {
            // If article frag is available, we're in two-pane layout...
            // Call a method in the ArticleFragment to update its content
            statsFrag.setPlotData(elevation);
            Log.e(TAG, "sent; "+ elevation + "to StatsFragmemt");
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...
            Log.e(TAG,"Null Fragment!");
            // Create fragment and give it an argument for the selected article
            statsFragment = new StatsFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("elevation", elevation);
            Log.e(TAG,"Put elevation in bundle: "+elevation);
            statsFragment.setArguments(args);
        }
    }
    public Dialog showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_activity)
                .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MapFragment mapf = (MapFragment)getSupportFragmentManager().findFragmentByTag("map");
                        mapf.discardActivity();
                        activityStopped = true;
                        Log.d(TAG, "onClick: discard activity");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                    }
                })
                .setTitle("Warning!")
                .setIcon(R.drawable.ic_warning);
        return builder.create();
    }

    public String getType() {
        return type;
    }

    public boolean isActivityStopped() {
        return activityStopped;
    }

    public void setActivityStopped(boolean activityStopped) {
        this.activityStopped = activityStopped;
    }

    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }
}
