package com.nottingham.psydm7.cw2_runtracker.Activities.mainActivity;

import android.app.Application;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

public class MainViewModel extends AndroidViewModel {

    private final LiveData<SavedRun> mostRecentExercise;

    private RunTrackerRoomDatabase db;
    private SavedRunDAO savedRunDAO;

    public MainViewModel(@NonNull Application application) {
        super(application);

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(application);
        savedRunDAO = db.savedRunDAO();
        //endregion

        // region "accessing database to get the most recent exercise to be displayed to the user"
        mostRecentExercise = savedRunDAO.getMostRecentExercise();
        //endregion
    }


    public LiveData<SavedRun> getMostRecentExercise() {
        return mostRecentExercise;
    }

}