package dat066.dat066_projekt;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationUpdatesService extends Service {
    private static final String TAG = LocationUpdatesService.class.getSimpleName();
    private static final String PACKAGE_NAME = "dat066.dat066_projekt";
    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "TrainRChannel";

    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    static final String CURRENT_LOCATION = PACKAGE_NAME + ".location";
    static final String LAST_KNOWN_LOCATION = PACKAGE_NAME + ".lastknownlocation";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS /5;
    private final IBinder mBinder = new LocalBinder();
    private static final int NOTIFICATION_ID = 12345678;
    private NotificationManager mNotificationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location lastLocation = null;
    private ArrayList<Location> listOfLocations;
    private Location lastKnownLocation;

    /**
     * The current location.
     */
    private Location mLocation;

    public LocationUpdatesService() {
    }

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        listOfLocations = new ArrayList<>();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if(serviceIsRunningInForeground(getApplicationContext())) {
                    Log.i(TAG, "onLocationResult: adding locaiton to list " + location.getLatitude() + " " + location.getLongitude());
                    listOfLocations.add(location);
                }else {
                    Log.i(TAG, "onLocationResult: " + location.getLatitude() + " " + location.getLongitude());
                    broadCastLocation(location);
                }

            }
        };

        createLocationRequest();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void broadCastLocation(Location location) {
        mLocation = location;
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(CURRENT_LOCATION, location);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i(TAG, "broadCastLocation: broadcasting " + latLng );
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


    private void broadCastLastKnownLocation(Location location) {
        mLocation = location;
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(LAST_KNOWN_LOCATION, location);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i(TAG, "broadCastLocation: broadcasting " + latLng );
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service Started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when MainActivity comes to the foreground
        stopForeground(true);
        if(listOfLocations != null) {
            for(Location location : listOfLocations) {
                broadCastLocation(location);
            }
            listOfLocations.clear();
        }
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        if(listOfLocations != null) {
            for(Location location : listOfLocations) {
                broadCastLocation(location);
            }
            listOfLocations.clear();
        }
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ContextCompat.startForegroundService(this, new Intent(this, LocationUpdatesService.class));
        startForeground(1, getNotification());
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    public void requestLocationUpdates() {
        Log.d(TAG, "Requesting location updates");
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    public void removeLocationUpdates() {
        Log.d(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);

        CharSequence text = getLocationText(mLocation);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Action cancelAction = new NotificationCompat.Action.Builder(R.drawable.ic_cancel, getString(R.string.remove_location_updates), servicePendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID )
                .addAction(cancelAction)
                .setContentTitle("TrainR")
                .setContentText("TrainR is running in the background")
                .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
                .setTicker(text);
        return builder.build();
    }


    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                lastKnownLocation = task.getResult();
                                broadCastLastKnownLocation(lastKnownLocation);
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }

                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    public class LocalBinder extends Binder {
        LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    private void notifyNotification() {
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
