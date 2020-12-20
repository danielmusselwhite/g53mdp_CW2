package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.util.List;

public class AllSavedRunsViewModel extends AndroidViewModel {

    private RunTrackerRoomDatabase db;
    private SavedRunDAO savedRunDAO;

    private LiveData<List<SavedRun>> savedRuns;

    public AllSavedRunsViewModel(@NonNull Application application) {
        super(application);

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(application);
        savedRunDAO = db.savedRunDAO();
        //endregion
    }

    public LiveData<List<SavedRun>> getSavedRuns() {
        return savedRuns;
    }

    public void updateSavedRuns(int pos){
        //"Sorting the runs based on the spinners value"
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
    }
}
