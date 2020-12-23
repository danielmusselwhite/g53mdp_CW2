package com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import android.content.Context;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nottingham.psydm7.cw2_runtracker.MyUtilities;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RunningActivity extends AppCompatActivity {

    RunningMapsFragment mapsFragment;

    private ArrayList<LatLng> path = new ArrayList<LatLng>();

    int sportIndex;

    TextView textView_timeValue;
    TextView textView_distanceValue;
    TextView textView_averageSpeedValue;
    Button button_save;

    RunTrackerRoomDatabase db;
    SavedRunDAO savedRunDAO;

    long startTime;

    float totalDistance=0;

    private RunningService.MyBinder runningService = null;

    //region "thread for updating the textview for displaying running time and speed every second"
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
                            textView_timeValue.setText(MyUtilities.formatTimeNicely(getDuration()));
                            textView_averageSpeedValue.setText(MyUtilities.roundToDP(averageSpeed, 2)  +" km/h"); // update average speed when time increases as well as when distance increases
                        }
                    });


                }
            } catch (InterruptedException e) {
            }
        }
    };
    //endregion

    //region "Lifecycle methods"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("g53mdp","RunningActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        //region "retrieving the bundle passed to this activity"
        Bundle bundle = getIntent().getExtras();
        sportIndex = bundle.getInt("SportIndex");
        //endregion

        //region "initialising DAOs"
        db = RunTrackerRoomDatabase.getDatabase(getApplicationContext());
        savedRunDAO = db.savedRunDAO();
        //endregion

        //region "creating and binding service"
        // MY BOUND
        Log.d("g53mdp", "RunningActivity starting MyBoundService");

        Bundle startServiceBundle = new Bundle();
        startServiceBundle.putInt("SportIndex", sportIndex);
        Intent startServiceIntent = new Intent(this, RunningService.class);
        startServiceIntent.putExtras(startServiceBundle);

        this.startService(startServiceIntent);
        Log.d("g53mdp", "RunningActivity binding MyBoundService");
        this.bindService(new Intent(this, RunningService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        //endregion

        //getting start time
        startTime = System.currentTimeMillis();

        //region "setting up map, doing on a separate thread so it doesn't slow down main activity / freeze UI"
        new Thread(new Runnable() {

            public void run() {
                mapsFragment = (RunningMapsFragment) RunningActivity.this.getSupportFragmentManager().findFragmentById(R.id.fragment_runningMap);
            }
        }).start();

        //endregion

        //region "getting references to views we will update"
        textView_timeValue = (TextView) findViewById(R.id.textViewTimeValue);
        textView_distanceValue = (TextView) findViewById(R.id.textViewKilometersValue);
        textView_averageSpeedValue = (TextView) findViewById(R.id.textViewAverageSpeedValue);
        button_save = (Button) findViewById(R.id.running_button_save);
        //endregion

        button_save.setText("Save "+getResources().getStringArray(R.array.sports_array)[sportIndex]);

        // starting the thread that will update the text view for playback duration every second
        playBackTextViewThread.start();
    }

    @Override
    public void onDestroy() {
        Log.d("g53mdp", "RunningActivity onDestroy");
        if(runningService!=null)
            runningService.finishService();
        super.onDestroy();
    }

    //endregion

    //region "inter-activity communication"
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onButtonClick (View v) {

        // generically handling button presses via id
        switch (v.getId()) {

            case R.id.running_button_save: {

                Log.d("g53mdp", "Finished the current run!");

                runningService.finishService();
                runningService=null;

                RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
                    //region "values which will be saved in database"
                    long totalTime = getDuration();
                    float averageSpeed = calculateSpeed(totalDistance, totalTime);
                    Date date = Calendar.getInstance().getTime();
                    String sport = getResources().getStringArray(R.array.sports_array)[sportIndex];
                    DateFormat dateFormat = new SimpleDateFormat("'"+sport+" on 'MMMM, dd");
                    String name = dateFormat.format(date);
                    //endregion

                    Log.d("g53mdp","Saving run to savedRuns table, with values: name = "+name+";time = "+MyUtilities.formatTimeNicely(totalTime)+"; distance = "+totalDistance+"km; speed = "+averageSpeed+"km/h; path size= "+path.size()+"; sportIndex = "+sportIndex);

                    //region "updating savedRuns table"
                    SavedRun savedRun = new SavedRun(name, date, totalDistance, averageSpeed, totalTime, path,sportIndex);
                    savedRunDAO.insert(savedRun);
                    //endregion
                });

                finish();
                break;
            }

        }

    }

    //endregion

    //region "calculations for distance and speed"
    public float calculateSpeed(float distance, long time){
        if(totalDistance==0 || time==0)
            return 0;
        return Math.round(distance/time*3600000 * 1000f) / 1000f;  // multiplying by 3,600,000 to convert from millis to hours, then rounding to 3 decimal places
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

            float newTotalDistance = MyUtilities.roundToDP(distance,2);
            totalDistance = newTotalDistance;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mapsFragment!=null)
                        mapsFragment.updateMap(location, lastLocation);
                    path.add(new LatLng(location.getLatitude(), location.getLongitude()));
                    textView_distanceValue.setText(MyUtilities.roundToDP(totalDistance,2) + " km");
                    float averageSpeed = calculateSpeed(totalDistance, getDuration());
                    textView_averageSpeedValue.setText(MyUtilities.roundToDP(averageSpeed,2)  +" km/h"); // update average speed when location changes as well as when time increases
                }
            });


        }

    };

    //endregion
    //endregion

}