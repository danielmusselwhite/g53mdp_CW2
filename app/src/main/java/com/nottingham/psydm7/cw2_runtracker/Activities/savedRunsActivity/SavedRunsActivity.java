package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


        updateDatabaseDisplay();
    }

    public void updateDatabaseDisplay() {

        //region "sorting by date"
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<SavedRun> runs = savedRunDAO.getRunsSortedByDate();
            for (SavedRun run : runs) {
                Log.d("g53mdp", "Sorting by date " + run.get_id() + " " + run.getName());
            }

            // running on UI thread to safely update the recyclerView with new new data
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SavedRunsRecyclerViewAdapter) recyclerView.getAdapter()).setData(runs);
                }
            });
        });
        //endregion
    }

}