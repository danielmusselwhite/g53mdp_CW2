package com.nottingham.psydm7.cw2_runtracker.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.SavedRunsActivity;
import com.nottingham.psydm7.cw2_runtracker.MyUtilities;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity.RunningActivity;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    Spinner spinner_sport;
    Button button_start;

    RunTrackerRoomDatabase db;
    SavedRunDAO savedRunDAO;

    //region "views for updating all the statistics"
    TextView textView_mostRecentExercise;
    TextView textView_wellDone;
    TextView textView_beatenFarthestRunDistance;
    TextView textView_beatenQuickestRunTime;
    TextView textView_beatenFastestRunSpeed;
    TextView textView_todaysStats;
    TextView textView_todaysTotals;
    TextView textView_todaysTotalTime;
    TextView textView_todaysTotalDistance;
    TextView textView_todaysRunningStats;
    TextView textView_todaysRunningTime;
    TextView textView_todaysRunningDistance;
    TextView textView_weeksStats;
    TextView textView_weeksTotals;
    TextView textView_weeksTotalTime;
    TextView textView_weeksTotalDistance;
    TextView textView_weeksRunningStats;
    TextView textView_weeksRunningTime;
    TextView textView_weeksRunningDistance;
    TextView textView_records;
    TextView textView_runningRecords;
    TextView textView_farthestRunDistance;
    TextView textView_quickestRunTime;
    TextView textView_fastestRunSpeed;
    //endregion


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("g53mdp","MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region "getting references to the different views"
        spinner_sport = (Spinner) findViewById(R.id.main_spinner_sport);
        button_start = (Button) findViewById(R.id.main_button_startExercise);

        //region "views for updating all the statistics"
        textView_mostRecentExercise = (TextView) findViewById(R.id.main_textView_mostRecentExercise);
        textView_wellDone = (TextView) findViewById(R.id.main_textView_wellDone);
        textView_beatenFarthestRunDistance = (TextView) findViewById(R.id.main_textView_beatenFarthestRun);
        textView_beatenQuickestRunTime = (TextView) findViewById(R.id.main_textView_beatenBestRunningTime);
        textView_beatenFastestRunSpeed = (TextView) findViewById(R.id.main_textView_beatenFastestRunningSpeed);
        textView_todaysStats = (TextView) findViewById(R.id.main_textView_todaysStats);
        textView_todaysTotals = (TextView) findViewById(R.id.main_textView_todaysTotals);
        textView_todaysTotalTime = (TextView) findViewById(R.id.main_textView_todaysTotalTime);
        textView_todaysTotalDistance = (TextView) findViewById(R.id.main_textView_todaysTotalDistance);
        textView_todaysRunningStats = (TextView) findViewById(R.id.main_textView_todaysRunningStats);
        textView_todaysRunningTime = (TextView) findViewById(R.id.main_textView_todaysTotalTimeRunning);
        textView_todaysRunningDistance = (TextView) findViewById(R.id.main_textView_todaysTotalDistanceRunning);
        textView_weeksStats = (TextView) findViewById(R.id.main_textView_weeksStats);
        textView_weeksTotals = (TextView) findViewById(R.id.main_textView_weeksTotals);
        textView_weeksTotalTime = (TextView) findViewById(R.id.main_textView_weeksTotalTime);
        textView_weeksTotalDistance = (TextView) findViewById(R.id.main_textView_weeksTotalDistance);
        textView_weeksRunningStats = (TextView) findViewById(R.id.main_textView_weeksRunningStats);
        textView_weeksRunningTime = (TextView) findViewById(R.id.main_textView_weeksTotalTimeRunning);
        textView_weeksRunningDistance = (TextView) findViewById(R.id.main_textView_weeksTotalDistanceRunning);
        textView_records = (TextView) findViewById(R.id.main_textView_records);
        textView_runningRecords = (TextView) findViewById(R.id.main_textView_runningRecords);
        textView_farthestRunDistance = (TextView) findViewById(R.id.main_textView_farthestRun);
        textView_quickestRunTime = (TextView) findViewById(R.id.main_textView_bestRunningTime);
        textView_fastestRunSpeed = (TextView) findViewById(R.id.main_textView_fastestRunningSpeed);
        //endregion

        //endregion

        //region "populating the spinner for sorting"
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sports_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_sport.setAdapter(spinnerAdapter);
        //endregion

        //region "creating listener for the sport spinner to update the start buttons text"
        spinner_sport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                button_start.setText("Start tracking my "+getResources().getStringArray(R.array.sports_array)[pos]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
        //endregion

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(getApplicationContext());
        savedRunDAO = db.savedRunDAO();
        //endregion

        //region "getting the start of today and of last week"
        LocalDate todayLocalDate = LocalDate.now();
        Date todayDate = Date.from(todayLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate weekStartLocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY ));
        Date weekStartDate = Date.from(weekStartLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        //endregion

        //region "accessing database to display useful information to the user"
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {

            //TODO - convert to live data and then add listeners for the live data

            //region "getting the different stats we want to display"
            //region "most recent exercise"
            Integer sportIndexOfMostRecentExercise = savedRunDAO.getSportIndexOfMostRecentExercise();
            Date dateOfMostRecentExercise = savedRunDAO.getDateOfMostRecentExercise();
            //endregion

            //region "record beaten checks"
            Float beatenFarthestRunDistance = savedRunDAO.checkIfBeatenSportsRecordDistanceToday(todayDate.getTime(),0);
            Long beatenShortestRunTime = savedRunDAO.checkIfBeatenSportsRecordTimeToday(todayDate.getTime(),0);
            Float beatenFastestRunSpeed = savedRunDAO.checkIfBeatenSportsRecordSpeedToday(todayDate.getTime(),0);

            //endregion

            //region "todays stats"
            Float totalDistanceToday = savedRunDAO.getTotalDistanceWithinTimePeriod(todayDate.getTime());
            Long totalTimeToday = savedRunDAO.getTotalTimeWithinTimePeriod(todayDate.getTime());

            Float runningDistanceToday = savedRunDAO.getSportsTotalDistanceWithinTimePeriod(todayDate.getTime(),0);
            Long runningTimeToday = savedRunDAO.getSportsTotalTimeWithinTimePeriod(todayDate.getTime(),0);
            //endregion

            //region "weeks stats"
            Float totalDistanceWeek = savedRunDAO.getTotalDistanceWithinTimePeriod(weekStartDate.getTime());
            Long totalTimeWeek = savedRunDAO.getTotalTimeWithinTimePeriod(weekStartDate.getTime());

            Float runningDistanceWeek = savedRunDAO.getSportsTotalDistanceWithinTimePeriod(weekStartDate.getTime(),0);
            Long runningTimeWeek = savedRunDAO.getSportsTotalTimeWithinTimePeriod(weekStartDate.getTime(),0);
            //endregion

            //region "records"
            Float farthestRunDistance = savedRunDAO.getSportsRecordDistance(0);
            Long quickestRunTime = savedRunDAO.getSportsRecordTime(0);
            Float fastestRunSpeed = savedRunDAO.getSportsRecordSpeed(0);
            //endregion
            //endregion

            //region "handling which views should be shown and updating the text views to these values"
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //region "most recent exercise"
                    // if we have at least one record stored this will not be null as there will be a most recent exercise
                    if(sportIndexOfMostRecentExercise != null){
                        DateFormat dateFormat = new SimpleDateFormat("MMM, dd, yyyy 'at' HH:mm");
                        textView_mostRecentExercise.setText("Your most recent exercise was the "+MainActivity.this.getResources().getStringArray(R.array.sports_array)[sportIndexOfMostRecentExercise]+ "you did on "+dateFormat.format(dateOfMostRecentExercise));
                        textView_mostRecentExercise.setVisibility(View.VISIBLE);
                    }
                    else{
                        textView_mostRecentExercise.setVisibility(View.GONE);
                    }
                    //endregion

                    //region "beaten records todays"
                    // if we have beaten at least one record today; display the well done text view
                    if(beatenFarthestRunDistance!=null||beatenShortestRunTime!=null||beatenFastestRunSpeed!=null){
                        if(beatenFarthestRunDistance!=null)
                            textView_beatenFarthestRunDistance.setVisibility(View.VISIBLE);
                        else
                            textView_beatenFarthestRunDistance.setVisibility(View.GONE);
                        if(beatenShortestRunTime!=null)
                            textView_beatenQuickestRunTime.setVisibility(View.VISIBLE);
                        else
                            textView_beatenQuickestRunTime.setVisibility(View.GONE);
                        if(beatenFastestRunSpeed!=null)
                            textView_beatenFastestRunSpeed.setVisibility(View.VISIBLE);
                        else
                            textView_beatenFastestRunSpeed.setVisibility(View.GONE);
                        textView_wellDone.setVisibility(View.VISIBLE);
                    }
                    else{
                        textView_wellDone.setVisibility(View.GONE);
                        textView_beatenFarthestRunDistance.setVisibility(View.GONE);
                        textView_beatenQuickestRunTime.setVisibility(View.GONE);
                        textView_beatenFastestRunSpeed.setVisibility(View.GONE);
                    }
                    //endregion

                    //region "todays stats"
                    // if we have done anything today; this will not be null
                    if(totalDistanceToday!=null){

                        //if we have done a run today; this will not be null
                        if(runningDistanceToday!=null){
                            textView_todaysTotalTime.setText("You have spent "+ MyUtilities.formatTimeNicely(runningTimeToday)+" running today");
                            textView_todaysTotalDistance.setText("You have traveled "+ runningDistanceToday+" km whilst running today");
                            textView_todaysRunningStats.setVisibility(View.VISIBLE);
                            textView_todaysRunningDistance.setVisibility(View.VISIBLE);
                            textView_todaysRunningTime.setVisibility(View.VISIBLE);
                        }
                        else{
                            Log.d("g53mdp3","runningDistanceToday is null");
                            textView_todaysRunningStats.setVisibility(View.GONE);
                            textView_todaysRunningDistance.setVisibility(View.GONE);
                            textView_todaysRunningTime.setVisibility(View.GONE);
                        }

                        textView_todaysTotalTime.setText("You have spent "+ MyUtilities.formatTimeNicely(totalTimeToday)+" exercising today");
                        textView_todaysTotalDistance.setText("You have traveled "+ totalDistanceToday+" km whilst exercising today");
                        textView_todaysStats.setVisibility(View.VISIBLE);
                        textView_todaysTotals.setVisibility(View.VISIBLE);
                        textView_todaysTotalTime.setVisibility(View.VISIBLE);
                        textView_todaysTotalDistance.setVisibility(View.VISIBLE);
                    }
                    else{
                        textView_todaysRunningStats.setVisibility(View.GONE);
                        textView_todaysRunningDistance.setVisibility(View.GONE);
                        textView_todaysRunningTime.setVisibility(View.GONE);
                        textView_todaysStats.setVisibility(View.GONE);
                        textView_todaysTotals.setVisibility(View.GONE);
                        textView_todaysTotalTime.setVisibility(View.GONE);
                        textView_todaysTotalDistance.setVisibility(View.GONE);
                    }
                    //endregion

                    //region "weeks stats"
                    // if we have done this week; this will not be null
                    if(totalDistanceWeek!=null){

                        //if we have done a run this week; this will not be null
                        if(runningDistanceWeek!=null){
                            textView_weeksTotalTime.setText("You have spent "+ MyUtilities.formatTimeNicely(runningTimeToday)+" running this week");
                            textView_weeksTotalDistance.setText("You have traveled "+ runningDistanceToday+" km whilst running this week");
                            textView_weeksRunningStats.setVisibility(View.VISIBLE);
                            textView_weeksRunningDistance.setVisibility(View.VISIBLE);
                            textView_weeksRunningTime.setVisibility(View.VISIBLE);
                        }
                        else{
                            textView_weeksRunningStats.setVisibility(View.GONE);
                            textView_weeksRunningDistance.setVisibility(View.GONE);
                            textView_weeksRunningTime.setVisibility(View.GONE);
                        }

                        textView_weeksTotalTime.setText("You have spent "+ MyUtilities.formatTimeNicely(totalTimeToday)+" exercising this week");
                        textView_weeksTotalDistance.setText("You have traveled "+ totalDistanceToday+" km whilst exercising this week");
                        textView_weeksStats.setVisibility(View.VISIBLE);
                        textView_weeksTotals.setVisibility(View.VISIBLE);
                        textView_weeksTotalTime.setVisibility(View.VISIBLE);
                        textView_weeksTotalDistance.setVisibility(View.VISIBLE);
                    }
                    else{
                        textView_weeksRunningStats.setVisibility(View.GONE);
                        textView_weeksRunningDistance.setVisibility(View.GONE);
                        textView_weeksRunningTime.setVisibility(View.GONE);
                        textView_weeksStats.setVisibility(View.GONE);
                        textView_weeksTotals.setVisibility(View.GONE);
                        textView_weeksTotalTime.setVisibility(View.GONE);
                        textView_weeksTotalDistance.setVisibility(View.GONE);
                    }
                    //endregion

                    //region "records"
                    // if we have at least one record today; display the records text view
                    if(farthestRunDistance!=null||quickestRunTime!=null||fastestRunSpeed!=null) {

                        //if we have have a running record stored; displaying the running records text view
                        if (farthestRunDistance != null || quickestRunTime != null || fastestRunSpeed != null) {
                            if (farthestRunDistance != null){
                                textView_farthestRunDistance.setText("Your farthest run is "+farthestRunDistance+" km");
                                textView_farthestRunDistance.setVisibility(View.VISIBLE);
                            }
                            else
                                textView_farthestRunDistance.setVisibility(View.GONE);

                            if (quickestRunTime != null){
                                textView_quickestRunTime.setText("Your quickest running time is "+MyUtilities.formatTimeNicely(quickestRunTime));
                                textView_quickestRunTime.setVisibility(View.VISIBLE);
                            }
                            else
                                textView_quickestRunTime.setVisibility(View.GONE);

                            if (fastestRunSpeed != null){
                                textView_fastestRunSpeed.setText("Your fastest running speed is "+fastestRunSpeed+" km/h");
                                textView_fastestRunSpeed.setVisibility(View.VISIBLE);
                            }
                            else
                                textView_fastestRunSpeed.setVisibility(View.GONE);
                            textView_runningRecords.setVisibility(View.VISIBLE);
                        }
                        else{
                            textView_runningRecords.setVisibility(View.GONE);
                            textView_farthestRunDistance.setVisibility(View.GONE);
                            textView_quickestRunTime.setVisibility(View.GONE);
                            textView_fastestRunSpeed.setVisibility(View.GONE);
                        }
                    }
                    else{
                        textView_records.setVisibility(View.GONE);
                        textView_runningRecords.setVisibility(View.GONE);
                        textView_farthestRunDistance.setVisibility(View.GONE);
                        textView_quickestRunTime.setVisibility(View.GONE);
                        textView_fastestRunSpeed.setVisibility(View.GONE);
                    }
                    //endregion
                }

            });

            //endregion

            //endregion
        });
        //endregion
    }



    //region "inter-activity communication"
    public void onButtonClick (View v) {

        // generically handling button presses via id
        switch (v.getId()) {

            case R.id.main_button_startExercise: {

                // if this doesn't have access to location permissions, request access, no point in launching activity if they cannot use it
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RunningActivity.REQUEST_CODE_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    return;
                }

                Log.d("g53mdp", "Starting the running activity!");
                Bundle bundle = new Bundle();
                bundle.putInt("SportIndex",spinner_sport.getSelectedItemPosition());
                Intent intent = new Intent(MainActivity.this, RunningActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }


            case R.id.main_button_pastExercise: {

                Log.d("g53mdp", "Starting the saved runs activity!");
                Intent intent = new Intent(MainActivity.this, SavedRunsActivity.class);
                startActivity(intent);
                break;
            }

        }

    }

    //endregion

}