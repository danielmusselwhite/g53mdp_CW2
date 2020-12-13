package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.util.List;

public class SavedRunsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

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

        //region "Getting the runs DAO and creating observer for live data"
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
            savedRuns = savedRunDAO.getRunsSortedByDate(); //getting all runs default sorted by date

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
        //endregion

    }

}