package dat066.dat066_projekt.database;

import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;


public class Converters implements Serializable {
    static Gson gson = new Gson();
    @TypeConverter
    public static ArrayList arrayToDouble(String data) {
        if (data == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<ArrayList>() {}.getType();
        return gson.fromJson(data, listType);
    }
    @TypeConverter
    public static String latListToString(ArrayList someObjects) {
        return gson.toJson(someObjects);
    }
}
