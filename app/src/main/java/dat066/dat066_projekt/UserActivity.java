package dat066.dat066_projekt;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class UserActivity {
    private double userSpeed;
    private double userDistanceMoved;
    private PolylineOptions route;
    private long activityTime;
    private double caloriesBurned;
    private Date dateTime;
    private LatLng firstLocation;
    private DatabaseReference mDatabase;


    UserActivity(double userSpeed, double userDistanceMoved, PolylineOptions route, long activityTime, double caloriesBurned, Date dateTime, LatLng firstLocation) {
        this.userSpeed = userSpeed;
        this.userDistanceMoved = userDistanceMoved;
        this.route = route;
        this.activityTime = activityTime;
        this.caloriesBurned = caloriesBurned;
        this.dateTime = dateTime;
        this.firstLocation = firstLocation;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void saveNote(){

        mDatabase.child("Activity").addListenerForSingleValueEvent(

                new ValueEventListener() {

                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {


                            writeNewPost();

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

    private void writeNewPost() {

        // Create new post at /user-posts/$userid/$postid and at

        // /posts/$postid simultaneously
        String key = mDatabase.child("Activity").push().getKey();

        Map<String, Object> data = new HashMap<>();
        data.put("date", getDateTime().toString());
        data.put("distance", String.valueOf(getUserDistanceMoved()));
        data.put("velocity", String.valueOf(getUserSpeed()));
        data.put("calories", String.valueOf(getCaloriesBurned()));
        data.put("time",String.valueOf(getActivityTime()));
        DataSnapshot snapshot;
        mDatabase.

        Map<String, Object> update = new HashMap<>();
        update.put(key, data);

        mDatabase.updateChildren(update);

    }

    public double getUserSpeed() {
        return userSpeed;
    }

    public double getUserDistanceMoved() {
        return userDistanceMoved;
    }

    public PolylineOptions getRoute() {
        return route;
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
}

