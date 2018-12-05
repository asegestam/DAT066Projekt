package dat066.dat066_projekt;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;

public class UserActivity {
    private double userSpeed;
    private double userDistanceMoved;
    private long activityTime;
    private double caloriesBurned;
    private Date dateTime;
    private LatLng firstLocation;
    private ArrayList<Double> elevationData;
    private ArrayList<PolylineOptions> routes;

    UserActivity(double userSpeed, double userDistanceMoved,
                 ArrayList<PolylineOptions> routes, long activityTime,
                 double caloriesBurned, Date dateTime,
                 LatLng firstLocation, ArrayList<Double> elevationData)
    {
        this.userSpeed = userSpeed;
        this.userDistanceMoved = userDistanceMoved;
        this.routes = routes;
        this.activityTime = activityTime;
        this.caloriesBurned = caloriesBurned;
        this.dateTime = dateTime;
        this.firstLocation = firstLocation;
        this.elevationData = elevationData;
    }

    public double getUserSpeed() {
        return userSpeed;
    }

    public double getUserDistanceMoved() {
        return userDistanceMoved;
    }

    public ArrayList<PolylineOptions> getRoutes() {
        return routes;
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
}

