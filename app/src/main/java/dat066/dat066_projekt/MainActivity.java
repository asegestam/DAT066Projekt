package dat066.dat066_projekt;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import java.util.ArrayList;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity
        implements PopupMenu.OnMenuItemClickListener, ElevationUpdater.OnPlotDataListener {
    private static final String TAG = "MainActivity";
    private String type;
    private boolean activityStopped;
    private final int REQUEST_CHECK_SETTINGS = 0; // a unique identifier
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 1500; /* 2 sec */
    StatsFragment statsFragment;
    private LocationRequest mLocationRequest;
    NavController navController;
    MapFragment mapFragment;
    ElevationUpdater elevationUpdater;
    ElevationUpdater.OnPlotDataListener plotDataListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, drawer);
        activityStopped = true;
        createLocationRequest();
        elevationUpdater = ElevationUpdater.getInstance();
        plotDataListener = this;
        elevationUpdater.setOnPlotDataListener(this);
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
            }
        });
        Task<LocationSettingsResponse> locationSettingsResponseTask1 = task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });
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
    public void onDataGiven(ArrayList elevation) {
        StatsFragment statsFrag = (StatsFragment)
                getSupportFragmentManager().findFragmentById(R.id.stats_fragment);

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

    public String getType() {
        return type;
    }

    public void setActivityStopped(boolean activityStopped) {
        this.activityStopped = activityStopped;
    }

    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }

}
