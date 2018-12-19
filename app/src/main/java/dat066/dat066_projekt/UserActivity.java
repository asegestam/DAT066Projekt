package dat066.dat066_projekt;

import android.view.View;

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
import com.google.firebase.database.ValueEventListener;

public class UserActivity {
    private double userSpeed;
    private double userDistanceMoved;
    private long activityTime;
    private double caloriesBurned;
    private Date dateTime;
    private LatLng firstLocation;
    private ArrayList<Double> elevationData;
    private ArrayList<ArrayList<LatLng>> listOfRoutes;

    UserActivity(double userSpeed, double userDistanceMoved,
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
    }

    public void saveNote(){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference();

        Map<String, Object> data = new HashMap<>();
        data.put("date", getDateTime().toString());
        data.put("distance", String.valueOf(getUserDistanceMoved()));
        data.put("velocity", String.valueOf(getUserSpeed()));
        data.put("calories", String.valueOf(getCaloriesBurned()));
        data.put("time",String.valueOf(getActivityTime()));

        ref.setValue(data, null);
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
