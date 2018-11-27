package dat066.dat066_projekt;

import com.google.android.gms.maps.model.PolylineOptions;

public class UserActivity {
    private double userSpeed;
    private double userDistanceMoved;
    private PolylineOptions route;
    private int activityTime;
    private double caloriesBurned;

    public UserActivity(double userSpeed, double userDistanceMoved, PolylineOptions route, int activityTime, double caloriesBurned) {
        this.userSpeed = userSpeed;
        this.userDistanceMoved = userDistanceMoved;
        this.route = route;
        this.activityTime = activityTime;
        this.caloriesBurned = caloriesBurned;
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

    public int getActivityTime() {
        return activityTime;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }
}
