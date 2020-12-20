package com.nottingham.psydm7.cw2_runtracker.Activities.mainActivity;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity.RunningActivity;
import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.SavedRunsActivity;
import com.nottingham.psydm7.cw2_runtracker.Activities.statisticsActivity.StatisticsActivity;
import com.nottingham.psydm7.cw2_runtracker.MyUtilities;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
//        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
        //getting the most recent exercise
        mostRecentExercise = savedRunDAO.getMostRecentExercise();
//        });
        //endregion
    }

    public LiveData<SavedRun> getMostRecentExercise() {
        return mostRecentExercise;
    }

}