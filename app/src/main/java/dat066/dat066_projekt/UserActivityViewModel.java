package dat066.dat066_projekt;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import dat066.dat066_projekt.database.UserActivityEntity;
import dat066.dat066_projekt.database.UserActivityRepository;

public class UserActivityViewModel extends AndroidViewModel {

    private UserActivityRepository mRepository;

    private LiveData<List<UserActivityEntity>> mAllUserActivites;

    public UserActivityViewModel(Application application) {
        super(application);
        mRepository = new UserActivityRepository(application);
        mAllUserActivites = mRepository.getAllUserActivities();
    }

    public LiveData<List<UserActivityEntity>> getAllUserActivites() {
        return mAllUserActivites;
    }

    public void insertActivity(UserActivityEntity userActivity) { mRepository.insertActivity(userActivity); }
}
