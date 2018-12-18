package dat066.dat066_projekt.database;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "user_activity_table")
@TypeConverters(Converters.class)
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
    @ColumnInfo(name = "pace")
    private double pace;
    @NonNull
    @ColumnInfo(name = "calories")
    private double calories;
    @NonNull
    @ColumnInfo(name = "distance")
    private double distance;
    @NonNull
    @ColumnInfo(name = "time")
    private long time;
    @NonNull
    @ColumnInfo(name = "elevation")
    private ArrayList elevationArray;
    @NonNull
    @ColumnInfo(name = "speedArray")
    private ArrayList speedArray;
    @NonNull
    @ColumnInfo(name = "ListId")
    private int listId;


    public UserActivityEntity(int id, String date, double speed, double pace, double calories, double distance, long time,ArrayList speedArray, ArrayList elevationArray) {
        this.id = id;
        this.date = date;
        this.speed = speed;
        this.pace = pace;
        this.calories = calories;
        this.distance = distance;
        this.time = time;
        this.elevationArray = elevationArray;
        this.speedArray = speedArray;
        this.listId = date.hashCode();
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
    public double getPace() {
        return Math.round(pace);
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

    @NonNull
    public ArrayList getElevationArray(){ return elevationArray; }

    @NonNull
    public ArrayList getSpeedArray(){ return speedArray; }

    @NonNull
    public int getListId(){ return listId; }

    @NonNull
    public void setListId(int set){ this.listId = set; }
}
