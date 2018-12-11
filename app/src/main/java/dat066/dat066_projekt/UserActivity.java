package dat066.dat066_projekt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;
public class UserActivity {
    public double userSpeed;
    public double userDistanceMoved;
    public long activityTime;
    public double caloriesBurned;
    public Date dateTime;
    public LatLng firstLocation;
    public ArrayList<Double> elevationData;
    public ArrayList<ArrayList<LatLng>> listOfRoutes;
    public DatabaseReference mDatabase;
    private ListView v;

    public UserActivity(){
        //for calls with dataSnapshot.getValue(UserActivity.class)
    }

    public UserActivity(double userSpeed, double userDistanceMoved,
                 ArrayList<ArrayList<LatLng>> listOfRoutes, long activityTime,
                 double caloriesBurned, Date dateTime,
                 LatLng firstLocation, ArrayList<Double> elevationData)
    {
        this.userSpeed = userSpeed;
        this.userDistanceMoved = userDistanceMoved;
        this.listOfRoutes = listOfRoutes;
        this.activityTime = activityTime;
        this.caloriesBurned = caloriesBurned;
        this.dateTime = dateTime;
        this.firstLocation = firstLocation;
        this.elevationData = elevationData;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void saveNote(){

        mDatabase.child("Activity").addListenerForSingleValueEvent(

                new ValueEventListener() {

                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String key = mDatabase.child("Activity").push().getKey();

                        Map<String, Object> data = new HashMap<>();
                        data.put("date", getDateTime().toString());
                        data.put("distance", String.valueOf(getUserDistanceMoved()));
                        data.put("velocity", String.valueOf(getUserSpeed()));
                        data.put("calories", String.valueOf(getCaloriesBurned()));
                        data.put("time",String.valueOf(getActivityTime()));


                        Map<String, Object> update = new HashMap<>();
                        update.put(key, data);

                        mDatabase.updateChildren(update);
                    }

                    @Override

                    public void onCancelled(DatabaseError databaseError) {

                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                        // [START_EXCLUDE]

                        //setEditingEnabled(true);

                        // [END_EXCLUDE]

                    }

                });
    }


    public double getUserSpeed() {
        return userSpeed;
    }

    public double getUserDistanceMoved() {
        return userDistanceMoved;
    }

    public ArrayList<ArrayList<LatLng>> getListOfRoutes() {
        return listOfRoutes;
    }

    public ArrayList<LatLng> getSpecificUserRoute(int index) {
        return this.listOfRoutes.get(index);
    }
    public int getListSize() {
        return listOfRoutes.size();
    }

    public long getActivityTime() {
        return activityTime;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public Date getDateTime() {
        return dateTime;
    }
    public LatLng getFirstLocation() {
        return firstLocation;
    }

    public ArrayList<Double> getElevationData() {
        return elevationData;
    }

    public UserActivity getInstance() {
        return this;
    }
}
