package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.util.List;

public class ViewSavedRunViewModel extends AndroidViewModel {

    private RunTrackerRoomDatabase db;
    private SavedRunDAO savedRunDAO;

    private LiveData<SavedRun> savedRun;

    private long savedRunID;


    public ViewSavedRunViewModel(@NonNull Application application) {
        super(application);

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(application);
        savedRunDAO = db.savedRunDAO();
        //endregion
    }

    public void setSavedRunID(Long savedRunID){
        this.savedRunID=savedRunID;
        savedRun = savedRunDAO.getRunWithID(savedRunID); //getting this run
    }

    public long getSavedRunID() {
        return savedRunID;
    }

    public LiveData<SavedRun> getSavedRun() {
        return savedRun;
    }

}
