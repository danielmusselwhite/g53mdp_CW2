package com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.nottingham.psydm7.cw2_runtracker.R;

public class RunningService extends Service {

    private NotificationManager notificationManager;
    private final String CHANNEL_ID = "101";
    private static int FOREGROUND_ID=1338;

    RemoteCallbackList<MyBinder> remoteCallbackList = new RemoteCallbackList<MyBinder>();
    private final IBinder binder = new MyBinder(); // reference to the interface the activity will be able to make use of

    private static LocationManager locationManager;
    private static MyLocationListener locationListener;
    float totalDistance=0;

    public RunningService() {
    }


    public void doCallbacks(Location location, Location lastLocation, float distance) {
        final int n = remoteCallbackList.beginBroadcast();
        for (int i=0; i<n; i++) {
            remoteCallbackList.getBroadcastItem(i).callback.locationChangeEvent(location, lastLocation, distance);
        }
        remoteCallbackList.finishBroadcast();
    }

    // region "binder"
    public class MyBinder extends Binder implements IInterface {
        IRunningCallback callback;

        public MyBinder()  {
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        public void registerCallback(IRunningCallback callback) {

            this.callback = callback;
            remoteCallbackList.register(MyBinder.this);
        }

        public void unregisterCallback(IRunningCallback callback) {
            remoteCallbackList.unregister(MyBinder.this);
        }

        public void finishService(){
            locationManager.removeUpdates(locationListener);
            locationManager = null;
            locationListener = null;
            stopForeground(true);
            totalDistance=0;
            RunningService.this.stopSelf();
        }
    }
    //endregion

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //region "lifecycle methods"
    @Override
    public void onCreate() {
        Log.d("g53mdp","RunningService onCreate");
        super.onCreate();

        //region "setting up the notification manager"
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setSound(null, null);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        //endregion

        startForeground(FOREGROUND_ID, buildForegroundNotification());

        //region "setting up location manager and listener"
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        // if this doesn't have access to location permission, return as you cannot do anything
        if (ActivityCompat.checkSelfPermission(RunningService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RunningService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
        //endregion
    }

    @Override
    public void onDestroy(){
        Log.d("g53mdp","RunningService onDestroy");
        //stopForeground(true);
        super.onDestroy();
    }
    //endregion

    // used to build the foreground notifacation necessetated to make this a foreground service
    private Notification buildForegroundNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(RunningService.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("You are currently on a run!")
                .setContentText("Your progress is being tracked!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);

        return mBuilder.build();
    }

    //region "MyLocationListener class to update the map"
    public class MyLocationListener implements LocationListener {

        Location lastLocation = null;

        @Override
        public void onLocationChanged(Location location) {
            Log.d("g53mdp", "New lat long is: "+ location.getLatitude() + ", " + location.getLongitude());

            // if this doesn't have access to location permissions, request access then return
            if (ActivityCompat.checkSelfPermission(RunningService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RunningService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if(lastLocation!=null){
                // if it gets this far then we do have permission to access location so use that to calculate distance
                float distance = location.distanceTo(lastLocation)/1000; //dividing by 1000 to convert from m to km
                totalDistance+=distance;
                Log.d("g53mdp", "Distance since last location is "+distance);
                Log.d("g53mdp", "Total distance is "+totalDistance);
            }

            //region "updating everything"
            doCallbacks(location, lastLocation, totalDistance);
            lastLocation=location;
            //endregion
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // information about the signal, i.e. number of satellites
            Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
        }
        @Override
        public void onProviderEnabled(String provider) {
            // the user enabled (for example) the GPS
            Log.d("g53mdp", "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // the user disabled (for example) the GPS
            Log.d("g53mdp", "onProviderDisabled: " + provider);
        }

    }
    //endregion
}