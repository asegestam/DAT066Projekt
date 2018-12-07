package dat066.dat066_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener, MapFragment.OnPlotDataListener {

    private static final String TAG = "MainActivity";
    private String type;
    private boolean activityStopped;
    StatsFragment newFragment;
    ArrayList<UserActivity> savedUserActivities;
    MapFragment mapFragment;
    ProfileFragment profileFragment;
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
        /*When the application starts we want the "Home" fragment to be initilized*/
        profileFragment = new ProfileFragment();
        newFragment = new StatsFragment();
        setFragment(R.id.activity_option);
        savedUserActivities = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        Fragment fragment = null;

        switch(id) {
            case R.id.activity_option:
                if(mapFragment == null) {
                    mapFragment = new MapFragment();
                }
                switchFragment(mapFragment);
                break;

            case R.id.profile_option:
                if(profileFragment == null) {
                    profileFragment = new ProfileFragment();
                }
                switchFragment(profileFragment);
                break;

            case R.id.settings_option:
                fragment = new SettingsFragment();
                //switchFragment(fragment);
                 break;

            case R.id.stats_option:
                fragment = newFragment;
                switchFragment(newFragment);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
    public void switchFragment(Fragment fragment) {
        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            // switch to mapfragment
            if(fragment instanceof MapFragment) {
                if(!fragment.isAdded()){
                    fragmentManager.beginTransaction().add(R.id.content_main, fragment).hide(profileFragment).hide(newFragment).show(fragment).commit();
                    this.setTitle("Map");
                }
                else {
                    fragmentManager.beginTransaction().hide(profileFragment).hide(newFragment).show(fragment).commit();
                    this.setTitle("Map");
                }
            }
            // switch to profilefragment
            else if(fragment instanceof ProfileFragment){
                if(!fragment.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.content_main, fragment).hide(mapFragment).hide(newFragment).show(fragment).commit();
                }
                fragmentManager.beginTransaction().hide(mapFragment).hide(newFragment).show(fragment).commit();
            }
            // switch to statsfragment
            else if (fragment instanceof StatsFragment) {
                if(!fragment.isAdded()) {
                    fragmentManager.beginTransaction().add(R.id.content_main, fragment).hide(profileFragment).hide(mapFragment).show(fragment).commit();
                }
                fragmentManager.beginTransaction().hide(profileFragment).hide(mapFragment).show(fragment).commit();
            }
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
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
            newFragment = new StatsFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("elevation", elevation);
            Log.e(TAG,"Put elevation in bundle: "+elevation);
            newFragment.setArguments(args);

           /* FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.content_main, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
            */
        }
    }
    public void saveUserActivity(UserActivity userActivity) {
        savedUserActivities.add(userActivity);
        Log.d(TAG, "saveUserActivity: antal sparade aktiviteter " + savedUserActivities.size());
        String logString = "\n" + userActivity.getUserSpeed() + " m/s\n" + userActivity.getUserDistanceMoved() + " m\n" + "size " + userActivity.getListOfRoutes().size() +
                "\n" + userActivity.getActivityTime() + " s\n" + userActivity.getDateTime() + "\n";
        Log.d(TAG, "saveUserActivity: Main Activity" + logString + "kalorier " + userActivity.getCaloriesBurned());
    }

    public ArrayList<UserActivity> getSavedUserActivities() {
        return savedUserActivities;
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
}
