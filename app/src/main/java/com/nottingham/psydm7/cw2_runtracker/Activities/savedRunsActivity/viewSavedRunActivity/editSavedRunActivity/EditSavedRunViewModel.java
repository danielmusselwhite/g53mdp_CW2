package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.editSavedRunActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

public class EditSavedRunViewModel extends AndroidViewModel {

    private LiveData<SavedRun> savedRun;

    private RunTrackerRoomDatabase db;
    private SavedRunDAO savedRunDAO;

    private long savedRunID;

    Boolean pictureUpdated = false;
    String newPicturePath = null;

    public EditSavedRunViewModel(@NonNull Application application) {
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

    public void updateName(long savedRunID, String name){
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
            savedRunDAO.updateName(savedRunID, name);
        });
    }

    public void updateSportIndex(long savedRunID, int newIndex){
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
        savedRunDAO.updateSportIndex(savedRunID, newIndex);});
    }

    public void updateEffortIndex(long savedRunID, int newIndex){
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
        savedRunDAO.updateEffortIndex(savedRunID, newIndex);});
    }

    public void updateDescription(long savedRunID, String description){
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
        if(!description.isEmpty())
            savedRunDAO.updateDescription(savedRunID, description);
        else
            savedRunDAO.updateDescription(savedRunID, null);});
    }

    public void updatePicturePath(long savedRunID, String newPicturePath){
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
        savedRunDAO.updatePicturePath(savedRunID, newPicturePath);});
    }

    public void deleteRun(long savedRunID){
        RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
        savedRunDAO.deleteRun(savedRunID);});
    }
}