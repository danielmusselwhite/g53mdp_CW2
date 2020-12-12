package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewSavedRunActivity extends AppCompatActivity {

    TextView nameView;
    TextView dateView;
    TextView speedView;
    TextView distanceView;
    TextView timeView;
    ViewSavedRunMapsFragment mapFragment;

    long savedRunID;

    RunTrackerRoomDatabase db;
    SavedRunDAO savedRunDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_run);

        //region "getting the different components we will update"
        nameView = findViewById(R.id.viewRunTextViewRunTitle);
        dateView = findViewById(R.id.viewRunTextViewDate);
        speedView = findViewById(R.id.viewRunTextViewSpeedValue);
        distanceView = findViewById(R.id.viewRunTextViewDistanceValue);
        timeView = findViewById(R.id.viewRunTextViewTimeValue);

        //getting the map fragment
        mapFragment = (ViewSavedRunMapsFragment) this.getSupportFragmentManager().findFragmentById(R.id.viewRun_mapFragment);
        //endregion

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(getApplicationContext());
        savedRunDAO = db.savedRunDAO();
        //endregion

        //region "retrieving the bundle passed to this activity"
        Bundle bundle = getIntent().getExtras();
        savedRunID = bundle.getLong("SavedRunID");
        //endregion

        //region "updating using the values from the database"
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {

            //getting this runs details
            SavedRun savedRun = savedRunDAO.getRunWithID(savedRunID);
            String name = savedRun.getName();
            DateFormat dateFormat = new SimpleDateFormat("MMM, dd, yyyy 'at' HH:mm");
            String dateString = dateFormat.format(savedRun.getDate());
            String speed = (savedRun.getSpeed()+" km/h");
            String distance = (savedRun.getDistance()+" km");
            String time = savedRun.getTime();
            ArrayList<LatLng> path = savedRun.getPath();

            //region "running on UI thread to safely update the different UI components"
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nameView.setText(name);
                    dateView.setText(dateString);
                    speedView.setText(speed);
                    distanceView.setText(distance);
                    timeView.setText(time);
                    mapFragment.savePath(path);
                }

            });
            //endregion
        });
        //endregion
    }
}