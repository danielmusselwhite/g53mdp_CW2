package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.util.List;

public class SavedRunsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Spinner spinner_sorting;

    RunTrackerRoomDatabase db;
    SavedRunDAO savedRunDAO;

    LiveData<List<SavedRun>> savedRuns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_runs);


        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(getApplicationContext());
        savedRunDAO = db.savedRunDAO();
        //endregion

        //region "setting up the recycler view"
        recyclerView = findViewById(R.id.savedRuns_recyclerView);
        final SavedRunsRecyclerViewAdapter adapter = new SavedRunsRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //endregion

        //region "getting references to the different views"
        spinner_sorting = (Spinner) findViewById(R.id.savedRuns_spinner_sorting);
        //endregion

        //region "populating the spinner for sorting"
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sorting_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_sorting.setAdapter(spinnerAdapter);
        //endregion

        //endregion

        //region "sorting entries based on the spinners value"
        spinner_sorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Log.d("g53mdp","sorting spinner has changed to: '"+getResources().getStringArray(R.array.sorting_array)[pos]+"'");

                //region "Handling cleaning of previous savedRuns if it exists"
                if(savedRuns!=null){
                    Log.d("g53mdp","handling cleaning of prior savedRuns");
                    savedRuns.removeObservers(SavedRunsActivity.this); // removing observers as we no longer care about it
                    savedRuns = null; // remove reference to it so java can deallocate memory
                }
                //endregion

                RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
                    //region "Sorting the runs based on the spinners value"
                    switch(pos) {
                        case 0:
                            savedRuns = savedRunDAO.getRunsSortedByDate();
                            break;
                        case 1:
                            savedRuns = savedRunDAO.getRunsSortedByName();
                            break;
                        case 2:
                            savedRuns = savedRunDAO.getRunsSortedByDistance();
                            break;
                        case 3:
                            savedRuns = savedRunDAO.getRunsSortedByTime();
                            break;
                        case 4:
                            savedRuns = savedRunDAO.getRunsSortedBySport();
                            break;
                    }
                    //endregion

                    //region "creating and attaching obsever for the live data"
                    //running on the UI thread as necessary for attaching observer and updating the UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // telling the recyclerView to update when Runs have changed
                            final Observer<List<SavedRun>> runsObserver = new Observer<List<SavedRun>>() {
                                @Override
                                public void onChanged(@Nullable final List<SavedRun> newRuns) {
                                    ((SavedRunsRecyclerViewAdapter) recyclerView.getAdapter()).setData(newRuns);
                                }
                            };

                            //attaching the observer to the runs livedata
                            savedRuns.observe(SavedRunsActivity.this, runsObserver);
                        }
                    });
                    //endregion
                });
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        //endregion

    }

}