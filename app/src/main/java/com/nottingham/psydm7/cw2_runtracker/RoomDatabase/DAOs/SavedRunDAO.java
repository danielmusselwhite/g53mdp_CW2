package com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.TypeConverters.dateConverter;

import java.util.Date;
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
    @Query("SELECT * FROM savedRun_table ORDER BY UPPER(name) ASC")
    LiveData<List<SavedRun>> getRunsSortedByName();

    @Query("SELECT * FROM savedRun_table ORDER BY date DESC")
    LiveData<List<SavedRun>> getRunsSortedByDate();

    @Query("SELECT * FROM savedRun_table ORDER BY distance DESC")
    LiveData<List<SavedRun>> getRunsSortedByDistance();

    @Query("SELECT * FROM savedRun_table ORDER BY time DESC")
    LiveData<List<SavedRun>> getRunsSortedByTime();

    @Query("SELECT * FROM savedRun_table ORDER BY speed DESC")
    LiveData<List<SavedRun>> getRunsSortedBySpeed();

    @Query("SELECT * FROM savedRun_table ORDER BY sportIndex ASC")
    LiveData<List<SavedRun>> getRunsSortedBySport();
    //endregion

    @Query("SELECT COUNT(_id) FROM savedRun_table")
    LiveData<Integer> getCount();

    @Query("SELECT * FROM savedRun_table WHERE _id = :id")
    LiveData<SavedRun> getRunWithID(long id);

    //region "Updating fields"
    @Query("Update savedRun_table SET name = :newName WHERE _id = :id")
    void updateName(long id, String newName);

    @Query("Update savedRun_table SET sportIndex = :newSportIndex WHERE _id = :id")
    void updateSportIndex(long id, int newSportIndex);

    @Query("Update savedRun_table SET description = :newDescription WHERE _id = :id")
    void updateDescription(long id, String newDescription);

    @Query("Update savedRun_table SET effortIndex = :newEffortIndex WHERE _id = :id")
    void updateEffortIndex(long id, Integer newEffortIndex);

    @Query("Update savedRun_table SET picturePath = :newPicturePath WHERE _id = :id")
    void updatePicturePath(long id, String newPicturePath);
    //endregion

    //region "Used for ContentProvider as it requires cursors"
    @Query("SELECT * FROM savedRun_table")
    Cursor getMultipleSavedRunsCursor();
    //endregion




    //region "answering user 'questions'"



    // TODO - CONVERT THESE TO LIVEDATA

    //region "getting total exercising distance and time within a time period"
    //region "total for all sports"
    // "how far have I travelled during excercising within time period?"
    @Query("SELECT SUM(distance) FROM savedRun_table WHERE date >= :dateLimit")
    Float getTotalDistanceWithinTimePeriod(long dateLimit);

    // "how much time have I spent excercising within time period?"
    @Query("SELECT SUM(time) FROM savedRun_table WHERE date >= :dateLimit")
    Long getTotalTimeWithinTimePeriod(long dateLimit);
    //endregion

    //region "each individual sport"
    // "how far have I travelled during this sport this week?"
    @Query("SELECT SUM(distance) FROM savedRun_table WHERE sportIndex = :thisSportIndex AND date >= :dateLimit")
    Float  getSportsTotalDistanceWithinTimePeriod(long dateLimit, int thisSportIndex);

    // "how much time have I spent doing this sports within this limit?"
    @Query("SELECT SUM(time) FROM savedRun_table WHERE sportIndex = :thisSportIndex AND date >= :dateLimit")
    Long getSportsTotalTimeWithinTimePeriod(long dateLimit, int thisSportIndex);
    //endregion
    //endregion

    //region "getting records for each sport (shortest time/greatest distance/fastest speed)"
    //"what is my farthest for this sport?"
    @Query("SELECT MAX(distance) FROM savedRun_table WHERE sportIndex = :thisSportIndex AND date >= :dateLimit")
    Float getSportsRecordDistanceWithinTimePeriod(long dateLimit, int thisSportIndex);

    //"what is my best time for this sport? (shortest) "
    @Query("SELECT MIN(time) FROM savedRun_table WHERE sportIndex = :thisSportIndex AND date >= :dateLimit")
    Long getSportsRecordTimeWithinTimePeriod(long dateLimit, int thisSportIndex);

    //"what is my fastest speed for this sport?"
    @Query("SELECT MAX(speed) FROM savedRun_table WHERE sportIndex = :thisSportIndex AND date >= :dateLimit")
    Float getSportsRecordSpeedWithinTimePeriod(long dateLimit, int thisSportIndex);
    //endregion

    //region "finding out if you have beaten your records today today (shortest time/greatest distance/fastest speed)"

    //"have I beaten my record for this sport?"
    /* select max distance from saved run table WHERE sportIndex = thisSportIndex AND MaxDistance for today for this sport = MaxDistance for this sport
     this way will get null if empty and highest value if not*/
    @Query("SELECT MAX(distance) FROM savedRun_table WHERE sportIndex = :thisSportIndex AND (SELECT MAX(distance) from savedRun_table WHERE date >= :dateLimit AND sportIndex = :thisSportIndex) = (SELECT MAX(distance) from savedRun_table WHERE sportIndex = :thisSportIndex)")
    Float checkIfBeatenSportsRecordDistanceWithinTimePeriod(long dateLimit, int thisSportIndex);

    @Query("SELECT MIN(time) FROM savedRun_table WHERE sportIndex = :thisSportIndex AND (SELECT MIN(time) from savedRun_table WHERE date >= :dateLimit AND sportIndex = :thisSportIndex) = (SELECT MIN(time) from savedRun_table WHERE sportIndex = :thisSportIndex)")
    Long checkIfBeatenSportsRecordTimeWithinTimePeriod(long dateLimit, int thisSportIndex);

    @Query("SELECT MAX(speed) FROM savedRun_table WHERE sportIndex = :thisSportIndex AND (SELECT MAX(speed) from savedRun_table WHERE date >= :dateLimit AND sportIndex = :thisSportIndex) = (SELECT MAX(speed) from savedRun_table WHERE sportIndex = :thisSportIndex)")
    Float checkIfBeatenSportsRecordSpeedWithinTimePeriod(long dateLimit, int thisSportIndex);
    //endregion

    //region "most recent exercising"
    // "when was the last time I exercised?"
    @TypeConverters({dateConverter.class})
    @Query("SELECT MAX(date) FROM savedRun_table")
    Date getDateOfMostRecentExercise();

    // "what was the last exercise I did?"
    @Query("SELECT sportIndex[0] FROM savedRun_table ORDER BY date DESC")
    Integer getSportIndexOfMostRecentExercise();
    //endregion




    //endregion
}
