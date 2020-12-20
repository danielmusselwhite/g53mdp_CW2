package com.nottingham.psydm7.cw2_runtracker.Activities.mainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.SavedRunsActivity;
import com.nottingham.psydm7.cw2_runtracker.Activities.statisticsActivity.StatisticsActivity;
import com.nottingham.psydm7.cw2_runtracker.MyUtilities;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity.RunningActivity;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity{

    private Spinner spinner_sport;
    private Button button_start;

    private MainViewModel viewModel;


    private TextView textView_mostRecentExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("g53mdp","MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region "getting references to the different views"
        spinner_sport = (Spinner) findViewById(R.id.main_spinner_sport);
        button_start = (Button) findViewById(R.id.main_button_startExercise);

        textView_mostRecentExercise = (TextView) findViewById(R.id.main_textView_mostRecentExercise);
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

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MainViewModel.class);
        viewModel.getMostRecentExercise().observe(this, newMostRecentExercise -> {
                    //if we haven't done any exercise yet then say that then return
                    if(newMostRecentExercise==null){
                        textView_mostRecentExercise.setText("We haven't got any recorded exercise from you yet, why not start today?");
                    }
                    // else we have then display the most recent exercise to the user!
                    else {
                        DateFormat dateFormat = new SimpleDateFormat("MMM, dd, yyyy 'at' HH:mm");
                        textView_mostRecentExercise.setText("Your most recent exercise was the " + MainActivity.this.getResources().getStringArray(R.array.sports_array)[newMostRecentExercise.getSportIndex()] + " you did on " + dateFormat.format(newMostRecentExercise.getDate()));
                    }
                });

    }



    //region "inter-activity communication"
    public void onButtonClick (View v) {

        // generically handling button presses via id
        switch (v.getId()) {

            case R.id.main_button_startExercise: {

                // if this doesn't have access to location permissions, request access then return, no point in launching activity if they cannot use it
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MyUtilities.REQUEST_CODE_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
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



            case R.id.main_button_viewStatistics: {

                Log.d("g53mdp", "Starting the saved statistics activity!");
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
                break;
            }

        }

    }

    //endregion

}