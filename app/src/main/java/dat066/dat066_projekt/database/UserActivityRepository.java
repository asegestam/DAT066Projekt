package dat066.dat066_projekt.database;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import dat066.dat066_projekt.MapFragment;

public class UserActivityRepository {
    private static String TAG = "Repository";
    private UserActivityDao mUserActivityDao;
    private LiveData<List<UserActivityEntity>> allUserActivities;

    public UserActivityRepository(Application application) {
        UserActivityDatabase db = UserActivityDatabase.getDatabase(application);
        mUserActivityDao = db.userActivityDao();
        allUserActivities = mUserActivityDao.getAllActivities();
    }

    public LiveData<List<UserActivityEntity>> getAllUserActivities() {
        return allUserActivities;
    }

    public void insertActivity(UserActivityEntity userActivity) {
        new insertAsyncTask(mUserActivityDao).execute(userActivity);
    }

    public UserActivityEntity getActivity(int ID) {
        return mUserActivityDao.getActivity(ID);
    }

    public void deleteActiviy(UserActivityEntity userActivity) {
        new deleteAsyncTask(mUserActivityDao).execute(userActivity);
    }

    public void deleteAllActivities() {
        Log.d(TAG, "deleteAllActivities: hejejj");
        new deleteAllAsyncTask(mUserActivityDao).execute();
    }

    public LiveData<Integer> getTableSize() {
        return mUserActivityDao.getTableSize();
    }

    private static class insertAsyncTask extends AsyncTask<UserActivityEntity, Void, Void> {

        private UserActivityDao mAsyncTaskDao;

        insertAsyncTask(UserActivityDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserActivityEntity... params) {
            mAsyncTaskDao.insertActivity(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<UserActivityEntity, Void, Void> {

        private UserActivityDao mAsyncTaskDao;

        deleteAsyncTask(UserActivityDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserActivityEntity... params) {
            mAsyncTaskDao.deleteActivity(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserActivityDao mAsyncTaskDao;

        deleteAllAsyncTask(UserActivityDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAllActivities();
            Log.d(TAG, "doInBackground: deleted table");
            return null;
        }
    }
}
