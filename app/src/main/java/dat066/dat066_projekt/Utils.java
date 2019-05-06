package dat066.dat066_projekt;

import android.content.Context;
import android.preference.PreferenceManager;

public class Utils {

    static final String ACTIVITY_STOPPED = "activity_stopped";


    static boolean activityStopped(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(ACTIVITY_STOPPED, false);
    }

    static void setActivityStopped(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(ACTIVITY_STOPPED, requestingLocationUpdates)
                .apply();
    }
}
