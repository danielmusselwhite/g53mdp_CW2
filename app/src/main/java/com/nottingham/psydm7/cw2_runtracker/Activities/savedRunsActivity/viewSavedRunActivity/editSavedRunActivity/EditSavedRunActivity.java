package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.editSavedRunActivity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;
import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.ViewSavedRunActivity;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditSavedRunActivity extends AppCompatActivity {

    long savedRunID;
    EditText et_Name;

    RunTrackerRoomDatabase db;
    SavedRunDAO savedRunDAO;

    LiveData<SavedRun> savedRun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_run);

        //region "populating the spinner for sports"
        Spinner spinner = (Spinner) findViewById(R.id.editRun_spinner_SportValue);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sports_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //endregion

        //region "retrieving the bundle passed to this activity"
        Bundle bundle = getIntent().getExtras();
        savedRunID = bundle.getLong("SavedRunID");
        //endregion

        //region "getting references to the different views"
        et_Name = findViewById(R.id.editRun_editText_NameValue);
        //endregion

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(getApplicationContext());
        savedRunDAO = db.savedRunDAO();
        //endregion

        //region "updating UI using the values from the database"
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
                            String name = newRun.getName();
                            et_Name.setText(name);
                        }
                    };

                    //attaching the observer to the runs livedata
                    savedRun.observe(EditSavedRunActivity.this, runObserver);
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

            case R.id.editRun_button_update: {

                Log.d("g53mdp", "Updating entry for this run!");

                savedRun.removeObservers(this);

                RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
                    savedRunDAO.updateName(savedRunID, et_Name.getText().toString());
                });

                finish();
                break;
            }

            case R.id.editRun_button_delete: {

                Log.d("g53mdp", "Deleting entry for this run!");
                
                savedRun.removeObservers(this);

                RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
                    savedRunDAO.deleteRun(savedRunID);
                });

                finish();
                break;
            }

        }

    }
    //endregion
}