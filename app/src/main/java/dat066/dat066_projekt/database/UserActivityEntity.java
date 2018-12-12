package dat066.dat066_projekt.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_activity_table")
public class UserActivityEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    int id;
    @NonNull
    @ColumnInfo(name = "date")
    private String date;
    @NonNull
    @ColumnInfo(name = "speed")
    private double speed;
    @NonNull
    @ColumnInfo(name = "calories")
    private double calories;
    @NonNull
    @ColumnInfo(name = "distance")
    private double distance;
    @NonNull
    @ColumnInfo(name = "time")
    private long time;


    public UserActivityEntity(int id, String date, double speed, double calories, double distance, long time) {
        this.id = id;
        this.date = date;
        this.speed = speed;
        this.calories = calories;
        this.distance = distance;
        this.time = time;
    }

    @NonNull
    public int getId() {
        return id;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public double getSpeed() {
        return Math.round(speed*100.0)/100.0;
    }

    @NonNull
    public double getCalories() {
        return ((int) calories);
    }

    @NonNull
    public double getDistance() {
        return Math.round(distance);
    }

    @NonNull
    public long getTime() {
        return time;
    }
}
