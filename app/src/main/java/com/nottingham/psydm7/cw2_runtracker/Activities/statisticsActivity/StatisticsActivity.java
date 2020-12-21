package com.nottingham.psydm7.cw2_runtracker.Activities.statisticsActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.nottingham.psydm7.cw2_runtracker.MyUtilities;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {

    TextView textView_totalStatsTitle;
    TextView textView_totalStatsTime;
    TextView textView_totalStatsDistance;
    TextView textView_sportStatsTitle;
    TextView textView_sportStatsTime;
    TextView textView_sportStatsDistance;
    TextView textView_sportRecordsTitle;
    TextView textView_sportRecordsDistance;
    TextView textView_sportRecordsTime;
    TextView textView_sportRecordsSpeed;
    TextView textView_noRecordedExercise;
    TextView textView_noRecordedSport;
    TextView textView_wellDoneTitle;
    TextView textView_beatenSportRecordsDistance;
    TextView textView_beatenSportRecordsTime;
    TextView textView_beatenSportRecordsSpeed;

    Spinner spinner_sport;
    Spinner spinner_timePeriod;

    int sportIndex;
    int timePeriodIndex;

    RunTrackerRoomDatabase db;
    SavedRunDAO savedRunDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //region "getting references to the different views"
        textView_totalStatsTitle = (TextView) findViewById(R.id.statistics_textView_timePeriodTotalStatsTitle);
        textView_totalStatsTime = (TextView) findViewById(R.id.statistics_textView_timePeriodTotalTime);
        textView_totalStatsDistance = (TextView) findViewById(R.id.statistics_textView_timePeriodTotalDistance);
        textView_sportStatsTitle = (TextView) findViewById(R.id.statistics_textView_timePeriodSportStatsTitle);
        textView_sportStatsTime = (TextView) findViewById(R.id.statistics_textView_timePeriodSportTime);
        textView_sportStatsDistance = (TextView) findViewById(R.id.statistics_textView_timePeriodSportDistance);
        textView_sportRecordsTitle = (TextView) findViewById(R.id.statistics_textView_timePeriodSportRecordTitle);
        textView_sportRecordsDistance = (TextView) findViewById(R.id.statistics_textView_timePeriodSportRecordDistance);
        textView_sportRecordsTime = (TextView) findViewById(R.id.statistics_textView_timePeriodSportRecordTime);
        textView_sportRecordsSpeed = (TextView) findViewById(R.id.statistics_textView_timePeriodSportRecordSpeed);
        textView_noRecordedExercise = (TextView) findViewById(R.id.statistics_textView_NoRecordedExercise);
        textView_noRecordedSport = (TextView) findViewById(R.id.statistics_textView_NoRecordedSport);
        textView_wellDoneTitle = (TextView) findViewById(R.id.statistics_textView_wellDoneTitle);
        textView_beatenSportRecordsDistance = (TextView) findViewById(R.id.statistics_textView_timePeriodSportBeatenRecordDistance);
        textView_beatenSportRecordsTime = (TextView) findViewById(R.id.statistics_textView_timePeriodSportBeatenRecordTime);
        textView_beatenSportRecordsSpeed = (TextView) findViewById(R.id.statistics_textView_timePeriodSportBeatenRecordSpeed);

        spinner_sport = (Spinner) findViewById(R.id.statistics_spinner_sport);
        spinner_timePeriod = (Spinner) findViewById(R.id.statistics_spinner_timePeriod);
        //endregion

        //region "populating the spinner for sport"
        // Create an ArrayAdapter using the string array and a default spinner layout
         {
             ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sports_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner_sport.setAdapter(spinnerAdapter);
        }
        //endregion

        //region "populating the spinner for timePeriod"
        // Create an ArrayAdapter using the string array and a default spinner layout
        {
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.timePeriod_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner_timePeriod.setAdapter(spinnerAdapter);
        }
        //endregion

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(getApplicationContext());
        savedRunDAO = db.savedRunDAO();
        //endregion

        //region "attaching listener for displaying the correct sport and timePeriod"
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(parent.getId()){
                    case R.id.statistics_spinner_sport:
                        sportIndex = spinner_sport.getSelectedItemPosition();
                        break;
                    case R.id.statistics_spinner_timePeriod:
                        timePeriodIndex = spinner_timePeriod.getSelectedItemPosition();
                        break;
                }
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner_sport.setOnItemSelectedListener(spinnerListener);
        spinner_timePeriod.setOnItemSelectedListener(spinnerListener);
        //endregion
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUI(){

        //region "handling the sport"
        String sporting = "";
        switch(sportIndex){
            case 0:
                sporting="Running";
                break;
            case 1:
                sporting="Jogging";
                break;
            case 2:
                sporting="Walking";
                break;
            case 3:
                sporting="Cycling";
                break;
            default:
                sporting="Hiking";
                break;
        }
        String finalSporting = sporting; //effectively final temp string for use in thread
        //endregion

        //region "handling the time period"
        Date dateLimit;
        String timePeriodString = "";
        switch(timePeriodIndex){
            case 0:
                LocalDate todayLocalDate = LocalDate.now();
                dateLimit = Date.from(todayLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                timePeriodString="today";
                break;
            case 1:
                LocalDate weekStartLocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY ));
                dateLimit = Date.from(weekStartLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                timePeriodString="this week";
                break;
            case 2:
                LocalDate monthStartLocalDate = LocalDate.now().with(LocalDate.now().withDayOfMonth(1));
                dateLimit = Date.from(monthStartLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                timePeriodString="this month";
                break;
            case 3:
                LocalDate yearStartLocalDate = LocalDate.now().with(LocalDate.now().withDayOfYear(1));
                dateLimit = Date.from(yearStartLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                timePeriodString="this year";
                break;
            default:
                dateLimit = new Date(0); // All Time so get every date after 0 (1970)
                timePeriodString="";
                break;
        }

        long dateLimitLong = dateLimit.getTime();
        String finalTimePeriodString = timePeriodString;
        //endregion

        String sport = getResources().getStringArray(R.array.sports_array)[sportIndex];
        String timePeriod = getResources().getStringArray(R.array.timePeriod_array)[timePeriodIndex];


        //region "accessing database to display useful information to the user"
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
            //region "getting the different stats we want to display"

            //region "Total Stats"
            Long totalTime = savedRunDAO.getTotalTimeWithinTimePeriod(dateLimitLong);
            Float totalDistance = savedRunDAO.getTotalDistanceWithinTimePeriod(dateLimitLong);
            //endregion

            //region "Sport Stats"
            Long sportTotalTime = savedRunDAO.getSportsTotalTimeWithinTimePeriod(dateLimitLong, sportIndex);
            Float sportTotalDistance = savedRunDAO.getSportsTotalDistanceWithinTimePeriod(dateLimitLong, sportIndex);
            //endregion

            //region "Sports Records"
            Long sportRecordTime = savedRunDAO.getSportsRecordTimeWithinTimePeriod(dateLimitLong, sportIndex);
            Float sportRecordDistance = savedRunDAO.getSportsRecordDistanceWithinTimePeriod(dateLimitLong, sportIndex);
            Float sportRecordSpeed = savedRunDAO.getSportsRecordSpeedWithinTimePeriod(dateLimitLong, sportIndex);
            //endregion

            //region "Beaten Sports Records"
            Long beatenSportRecordTime = savedRunDAO.checkIfBeatenSportsRecordTimeWithinTimePeriod(dateLimitLong, sportIndex);
            Float beatenSportRecordDistance = savedRunDAO.checkIfBeatenSportsRecordDistanceWithinTimePeriod(dateLimitLong, sportIndex);
            Float beatenSportRecordSpeed = savedRunDAO.checkIfBeatenSportsRecordSpeedWithinTimePeriod(dateLimitLong, sportIndex);
            //endregion

            //endregion
            //region "handling which views should be shown and updating the text views to these values"
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //if totalTime is not null we have done any exercise of any type during this time period
                    if(totalTime!=null){
                        //so lets populate the totals
                        textView_totalStatsTitle.setText(timePeriod+" Total Statistics");
                        textView_totalStatsTime.setText("You have spent "+MyUtilities.formatLargeTimeNicely(totalTime)+" exercising "+finalTimePeriodString);
                        textView_totalStatsDistance.setText("You have traveled "+MyUtilities.roundToDP(totalDistance,2)+" km whilst exercising "+finalTimePeriodString);
                        textView_totalStatsTitle.setVisibility(View.VISIBLE);
                        textView_totalStatsTime.setVisibility(View.VISIBLE);
                        textView_totalStatsDistance.setVisibility(View.VISIBLE);
                        textView_noRecordedExercise.setVisibility(View.GONE);

                        //then check if we have done any exercise of this sport during this time period
                        // if sportTotalTime isn't null its because we have done some exercise of this type during this time period
                        if(sportTotalTime!=null){
                            //so lets populate this sports fields
                            textView_sportStatsTitle.setText(timePeriod+" "+sport+" Statistics");
                            textView_sportStatsTime.setText("You have spent "+MyUtilities.formatLargeTimeNicely(sportTotalTime)+" "+finalSporting+" "+finalTimePeriodString);
                            textView_sportStatsDistance.setText("You have traveled "+MyUtilities.roundToDP(sportTotalDistance,2)+" km whilst "+finalSporting+" "+finalTimePeriodString);
                            textView_sportRecordsTitle.setText(timePeriod+" "+sport+" Records");
                            textView_sportRecordsDistance.setText("The farthest distance you travelled in a single "+sport+" "+finalTimePeriodString+" is "+MyUtilities.roundToDP(sportRecordDistance,2)+" km");
                            textView_sportRecordsTime.setText("The quickest time you finished a single "+sport+" in "+finalTimePeriodString+" is "+MyUtilities.formatLargeTimeNicely(sportRecordTime));
                            textView_sportRecordsSpeed.setText("The fastest average speed you had for a single "+sport+" "+finalTimePeriodString+" is "+MyUtilities.roundToDP(sportRecordSpeed,2)+" km/h");
                            textView_sportStatsTitle.setVisibility(View.VISIBLE);
                            textView_sportStatsTime.setVisibility(View.VISIBLE);
                            textView_sportStatsDistance.setVisibility(View.VISIBLE);
                            textView_sportRecordsTitle.setVisibility(View.VISIBLE);
                            textView_sportRecordsDistance.setVisibility(View.VISIBLE);
                            textView_sportRecordsTime.setVisibility(View.VISIBLE);
                            textView_sportRecordsSpeed.setVisibility(View.VISIBLE);
                            textView_noRecordedSport.setVisibility(View.GONE);

                            //then check if we have beaten any of our records within this time period
                            // if this timePeriod is not set to All Time (as obviously you'll have beaten your record) AND at least one of these exist display well done then display message for each
                            if(timePeriodIndex!=4 && (beatenSportRecordDistance!=null || beatenSportRecordSpeed!=null || beatenSportRecordTime != null)){
                                textView_wellDoneTitle.setVisibility(View.VISIBLE);

                                if(beatenSportRecordDistance!=null){
                                    textView_beatenSportRecordsDistance.setText("You beat your all time farthest distance record for a single "+sport+" "+finalTimePeriodString);
                                    textView_beatenSportRecordsDistance.setVisibility(View.VISIBLE);
                                }
                                else
                                    textView_beatenSportRecordsDistance.setVisibility(View.GONE);

                                if(beatenSportRecordTime!=null){
                                    textView_beatenSportRecordsTime.setText("You beat your all time quickest "+sport+" time record "+finalTimePeriodString);
                                    textView_beatenSportRecordsTime.setVisibility(View.VISIBLE);
                                }
                                else
                                    textView_beatenSportRecordsTime.setVisibility(View.GONE);

                                if(beatenSportRecordSpeed!=null){
                                    textView_beatenSportRecordsSpeed.setText("You beat your all time fastest average speed record for a "+sport+" "+finalTimePeriodString);
                                    textView_beatenSportRecordsSpeed.setVisibility(View.VISIBLE);
                                }
                                else
                                    textView_beatenSportRecordsSpeed.setVisibility(View.GONE);
                            }
                            //else we haven't beaten any records this time period
                            else{
                                textView_wellDoneTitle.setVisibility(View.GONE);
                                textView_beatenSportRecordsDistance.setVisibility(View.GONE);
                                textView_beatenSportRecordsTime.setVisibility(View.GONE);
                                textView_beatenSportRecordsSpeed.setVisibility(View.GONE);
                            }
                        }
                        // else sportTotalTime is null then everything else will be too as we haven't done any exercise of this typein this time period
                        else{
                            textView_noRecordedSport.setText("No recorded "+sport+"s "+finalTimePeriodString);
                            textView_noRecordedSport.setVisibility(View.VISIBLE);
                            textView_sportStatsTitle.setVisibility(View.GONE);
                            textView_sportStatsTime.setVisibility(View.GONE);
                            textView_sportStatsDistance.setVisibility(View.GONE);
                            textView_sportRecordsTitle.setVisibility(View.GONE);
                            textView_sportRecordsDistance.setVisibility(View.GONE);
                            textView_sportRecordsTime.setVisibility(View.GONE);
                            textView_sportRecordsSpeed.setVisibility(View.GONE);
                            textView_wellDoneTitle.setVisibility(View.GONE);
                            textView_wellDoneTitle.setVisibility(View.GONE);
                            textView_beatenSportRecordsDistance.setVisibility(View.GONE);
                            textView_beatenSportRecordsTime.setVisibility(View.GONE);
                            textView_beatenSportRecordsSpeed.setVisibility(View.GONE);
                        }
                    }
                    // else totalTime is null then everything else will be too as we haven't done any exercise in this time period
                    else{
                        textView_noRecordedExercise.setText("No recorded exercise "+finalTimePeriodString);
                        textView_noRecordedExercise.setVisibility(View.VISIBLE);
                        textView_totalStatsTitle.setVisibility(View.GONE);
                        textView_totalStatsTime.setVisibility(View.GONE);
                        textView_totalStatsDistance.setVisibility(View.GONE);
                        textView_sportStatsTitle.setVisibility(View.GONE);
                        textView_sportStatsTime.setVisibility(View.GONE);
                        textView_sportStatsDistance.setVisibility(View.GONE);
                        textView_sportRecordsTitle.setVisibility(View.GONE);
                        textView_sportRecordsDistance.setVisibility(View.GONE);
                        textView_sportRecordsTime.setVisibility(View.GONE);
                        textView_sportRecordsSpeed.setVisibility(View.GONE);
                        textView_noRecordedSport.setVisibility(View.GONE);
                        textView_wellDoneTitle.setVisibility(View.GONE);
                        textView_wellDoneTitle.setVisibility(View.GONE);
                        textView_beatenSportRecordsDistance.setVisibility(View.GONE);
                        textView_beatenSportRecordsTime.setVisibility(View.GONE);
                        textView_beatenSportRecordsSpeed.setVisibility(View.GONE);
                    }
                }
            });
        });

        //endregion


        float totalDistance=0;
        long totalTime=1000000000;

        float sportDistance=0;
        long sportTime=0;

        float sportRecordDistance=0;
        long sportRecordTime=0;
        float sportRecordSpeed=0;




    }
}