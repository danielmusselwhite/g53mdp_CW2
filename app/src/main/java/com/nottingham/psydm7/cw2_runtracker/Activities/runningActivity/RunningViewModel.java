package com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.util.List;

public class RunningViewModel extends AndroidViewModel {

    private RunTrackerRoomDatabase db;
    private SavedRunDAO savedRunDAO;

    private LiveData<List<SavedRun>> savedRuns;

    public RunningViewModel(@NonNull Application application) {
        super(application);

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(application);
        savedRunDAO = db.savedRunDAO();
        //endregion
    }

    public LiveData<List<SavedRun>> getSavedRuns() {
        return savedRuns;
    }

}
