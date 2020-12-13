package com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;

import java.util.List;

@Dao
public interface SavedRunDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SavedRun savedRun);

    @Query("DELETE FROM savedRun_table")
    void deleteAll();

    @Query("DELETE FROM savedRun_table WHERE _id = :id")
    void deleteRun(long id);

    //region "sorting the entries by different columns"
    @Query("SELECT * FROM savedRun_table ORDER BY name ASC")
    LiveData<List<SavedRun>> getRunsSortedByName();

    @Query("SELECT * FROM savedRun_table ORDER BY date DESC")
    LiveData<List<SavedRun>> getRunsSortedByDate();

    @Query("SELECT * FROM savedRun_table ORDER BY distance DESC")
    LiveData<List<SavedRun>> getRunsSortedByDistance();

    @Query("SELECT * FROM savedRun_table ORDER BY time DESC")
    LiveData<List<SavedRun>> getRunsSortedByTime();

    @Query("SELECT * FROM savedRun_table ORDER BY speed DESC")
    LiveData<List<SavedRun>> getRunsSortedBySpeed();

    //endregion

    @Query("SELECT COUNT(_id) FROM savedRun_table")
    LiveData<Integer> getCount();

    @Query("SELECT * FROM savedRun_table WHERE _id = :id")
    LiveData<SavedRun> getRunWithID(long id);

    //region "Updating fields"
    @Query("Update savedRun_table SET name = :newName WHERE _id = :id")
    void updateName(long id, String newName);

    // TODO - add ability to update route and also give your performance a rating
    //endregion

    //region "Used for ContentProvider as it requires cursors"
    @Query("SELECT * FROM savedRun_table")
    Cursor getMultipleRecipeCursor();
    //endregion
}
