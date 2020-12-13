package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nottingham.psydm7.cw2_runtracker.Activities.MainActivity;
import com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity.RunningActivity;
import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.SavedRunsActivity;
import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.SavedRunsRecyclerViewAdapter;
import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.editSavedRunActivity.EditSavedRunActivity;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewSavedRunActivity extends AppCompatActivity {

    TextView nameView;
    TextView dateView;
    TextView speedView;
    TextView distanceView;
    TextView timeView;
    ViewSavedRunMapsFragment mapFragment;

    long savedRunID;
    LiveData<SavedRun> savedRun;

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

            savedRun = savedRunDAO.getRunWithID(savedRunID); //getting this run

            //region "creating and attaching observer for the live data"
            //running on the UI thread as necessary for attaching observer and updating the UI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // observer that updates the UI when the run has changed
                    final Observer<SavedRun> runObserver = new Observer<SavedRun>() {
                        @Override
                        public void onChanged(@Nullable final SavedRun newRun) {
                            //if this entry has been deleted
                            if(newRun==null){
                                finish();
                            }
                            //else this has been updated
                            else {
                                String name = newRun.getName();
                                DateFormat dateFormat = new SimpleDateFormat("MMM, dd, yyyy 'at' HH:mm");
                                String dateString = dateFormat.format(newRun.getDate());
                                String speed = (newRun.getSpeed() + " km/h");
                                String distance = (newRun.getDistance() + " km");
                                String time = newRun.getTime();
                                ArrayList<LatLng> path = newRun.getPath();

                                nameView.setText(name);
                                dateView.setText(dateString);
                                speedView.setText(speed);
                                distanceView.setText(distance);
                                timeView.setText(time);
                                mapFragment.savePath(path);
                            }
                        }
                    };

                    //attaching the observer to the runs livedata
                    savedRun.observe(ViewSavedRunActivity.this, runObserver);
                }
            });
            //endregion
        });
        //endregion
    }

    //region "inter-activity communication"
    public void onButtonClick (View v) {

        // generically handling button presses via id
        switch (v.getId()) {

            case R.id.editButton: {

                Bundle bundle = new Bundle();
                bundle.putLong("SavedRunID",savedRunID);
                Intent intent = new Intent(ViewSavedRunActivity.this, EditSavedRunActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
                break;
            }

        }

    }

    //endregion
}