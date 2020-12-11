package com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.content.Context;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nottingham.psydm7.cw2_runtracker.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RunningActivity extends AppCompatActivity {

    RunningMapsFragment mapsFragment;

    public final static int REQUEST_CODE_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    TextView tvTime;
    TextView tvKilos;
    TextView tvAverageSpeed;

    long startTime;

    float totalDistance=0;

    private RunningService.MyBinder runningService = null;

    //region "thread for updating the textview for displaying running time every 1 second"
    //updating the text view every second whilst playing a song
    private Thread playBackTextViewThread = new Thread(){

        private boolean paused = true;

        @Override
        public void run() {


            try {
                while (!isInterrupted()) {

                    Thread.sleep(1000);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float averageSpeed = calculateSpeed(totalDistance, getDuration());
                            tvTime.setText(formatTimeNicely(getDuration()));
                            tvAverageSpeed.setText(averageSpeed  +" km/h");
                        }
                    });


                }
            } catch (InterruptedException e) {
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("g53mdp","RunningActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        //region "creating and binding service"
        // MY BOUND
        Log.d("g53mdp", "RunningActivity starting MyBoundService");
        this.startService(new Intent(this, RunningService.class));
        Log.d("g53mdp", "RunningActivity binding MyBoundService");
        this.bindService(new Intent(this, RunningService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        //endregion

        //getting start time
        startTime = System.currentTimeMillis();

        //region "setting up map"
        mapsFragment = (RunningMapsFragment) RunningActivity.this.getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        //endregion

        //region "sorting out stuff for text views"
        tvTime = (TextView) findViewById(R.id.textViewTimeValue);
        tvKilos = (TextView) findViewById(R.id.textViewKilometersValue);
        tvAverageSpeed = (TextView) findViewById(R.id.textViewAverageSpeedValue);
        //endregion

        // starting the thread that will update the text view for playback duration every second
        playBackTextViewThread.start();
    }

    //region "inter-activity communication"
    public void onButtonClick (View v) {

        // generically handling button presses via id
        switch (v.getId()) {

            case R.id.button_stop: {

                Log.d("g53mdp", "Saving the current run!");


                runningService.finishService();

                long totalTime = getDuration();
                String stringTime = formatTimeNicely(totalTime);
                float averageSpeed = calculateSpeed(totalDistance, totalTime);

                ArrayList<LatLng> path = mapsFragment.getPath();

                Log.d("g53mdp","Finished run with: time = "+stringTime+"; distance = "+totalDistance+"km; speed = "+averageSpeed+"km/h; path = "+path.size());

                // TODO - code for storing this run into database

                // ID, Name (default to "Run: <ID>"), DateTime, distance time, speed, path

                finish();
                break;
            }

        }

    }

    //endregion

    //region "calculations for distance and speed"
    public float convertDistanceTo3Dp(float distance){
        return Math.round(distance * 1000f) / 1000f; //rounding displayed value to 3 decimal places
    }
    public float calculateSpeed(float distance, long time){
        if(totalDistance==0 || time==0)
            return 0;
        return Math.round(distance/time*3600000 * 1000f) / 1000f;  // multiplying by 3,600,000 to convert from millis to hours, then rounding to 3 decimal places
    }
    //endregion



    //region "formatting time nice"
    private String formatTimeNicely(long timeMillis){
        String stringHours = "";
        String stringMinutes = "";
        String stringSeconds = "";

        long hours= TimeUnit.MILLISECONDS.toHours(timeMillis); // converting milliseconds into hours
        //if the duration is greater than an hour...
        if(TimeUnit.MILLISECONDS.toHours(timeMillis)>0){
            //.. add the extra section for hours
            stringHours = String.format("%02d", hours);
        }

        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis); // converting milliseconds into minutes
        long actualMinutes =  totalMinutes - TimeUnit.HOURS.toMinutes(hours); // actual minutes = total minutes - the number of hours into minutes
        stringMinutes = String.format("%02d", actualMinutes)+":";

        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis); // converting milliseconds into seconds
        long actualSeconds = totalSeconds - TimeUnit.MINUTES.toSeconds(totalMinutes); // actual seconds = totalseconds - number of seconds in the minutes
        stringSeconds = String.format("%02d", actualSeconds);

        // used if statements so for example if a song is 3m42s long it will display 03:42 instead of 00:03:42
        String getDuration = stringHours +  stringMinutes +  stringSeconds;

        return getDuration;

    }

    private long getDuration(){
        return System.currentTimeMillis() - startTime;
    }
    //endregion

    //region "service and callback logic"
    //region "handling service connection"
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("g53mdp", "RunningActivity onServiceConnected");
            runningService = (RunningService.MyBinder) service;
            runningService.registerCallback(callback);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("g53mdp", "RunningActivity onServiceDisconnected");
            runningService.unregisterCallback(callback);
            runningService = null;
        }
    };
    //endregion

    //region "doing callbacks"
    IRunningCallback callback = new IRunningCallback() {

        @Override
        public void locationChangeEvent(Location location, Location lastLocation, float distance) {
            Log.d("g53mdp", "RunningActivity locationChangeEvent");

            float newTotalDistance = convertDistanceTo3Dp(distance);
            totalDistance = newTotalDistance;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mapsFragment.updateMap(location, lastLocation);
                    tvKilos.setText(totalDistance + " km");
                }
            });


        }

    };

    //endregion
    //endregion


//
//
//
}