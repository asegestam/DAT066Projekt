package dat066.dat066_projekt;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.Date;

public class UserActivity {
    private double userSpeed;
    private double userDistanceMoved;
    private PolylineOptions route;
    private long activityTime;
    private double caloriesBurned;
    private Date dateTime;
    private LatLng firstLocation;

    UserActivity(double userSpeed, double userDistanceMoved, PolylineOptions route, long activityTime, double caloriesBurned, Date dateTime, LatLng firstLocation) {
        this.userSpeed = userSpeed;
        this.userDistanceMoved = userDistanceMoved;
        this.route = route;
        this.activityTime = activityTime;
        this.caloriesBurned = caloriesBurned;
        this.dateTime = dateTime;
        this.firstLocation = firstLocation;
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

