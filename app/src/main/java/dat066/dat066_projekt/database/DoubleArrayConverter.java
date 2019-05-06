package dat066.dat066_projekt.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class DoubleArrayConverter implements Serializable {

    static Gson gson = new Gson();

    @TypeConverter
    public static ArrayList<Double> stringToDouble(String data) {
        if (data == null) {
            return new ArrayList<>();
        }

        Type listType = new TypeToken<ArrayList<Double>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String doubleToString(ArrayList<Double> someObjects) {
        return gson.toJson(someObjects);
    }
}

