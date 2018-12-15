package dat066.dat066_projekt;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class LocationViewModel extends AndroidViewModel {
    String TAG = "LocationViewModel";
    //Locations
    private MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private MutableLiveData<Location> lastLocationLiveData = new MutableLiveData<>();
    private MutableLiveData<Location> firstLocation = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Location>> listOfLocations = new MutableLiveData<>();



    public LocationViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Location> getLocation() {
        if (locationLiveData == null) {
            locationLiveData = new MutableLiveData<Location>();
        }
        return locationLiveData;
    }

    MutableLiveData<Location> getLastLocation() {
        if (lastLocationLiveData == null) {
            lastLocationLiveData = new MutableLiveData<Location>();
        }
        return lastLocationLiveData;
    }


    MutableLiveData<Location> getFirstLocation() {
        if (firstLocation == null) {
            firstLocation = new MutableLiveData<Location>();
        }
        return firstLocation;
    }

    public MutableLiveData<ArrayList<Location>> getListOfLocations() {
        return listOfLocations;
    }
}
