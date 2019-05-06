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

    private LiveData<Integer> tableSize;

    public UserActivityViewModel(Application application) {
        super(application);
        mRepository = new UserActivityRepository(application);
        mAllUserActivites = mRepository.getAllUserActivities();

    }

    public LiveData<List<UserActivityEntity>> getAllUserActivites() {
        return mAllUserActivites;
    }

    public UserActivityEntity getActivity(int ID) { return mRepository.getActivity(ID);}

    public void insertActivity(UserActivityEntity userActivity) { mRepository.insertActivity(userActivity); }

    public void deleteAllActivities() {
        mRepository.deleteAllActivities();
    }

    public LiveData<Integer> getTableSize() {
      return mRepository.getTableSize();
    }
}
