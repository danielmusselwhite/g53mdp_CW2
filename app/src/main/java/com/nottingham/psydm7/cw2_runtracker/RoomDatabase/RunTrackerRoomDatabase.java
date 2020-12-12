package com.nottingham.psydm7.cw2_runtracker.RoomDatabase;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {SavedRun.class}, version = 2, exportSchema=false)
public abstract class RunTrackerRoomDatabase extends RoomDatabase {

    public  abstract SavedRunDAO savedRunDAO();

    private static volatile RunTrackerRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RunTrackerRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RunTrackerRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RunTrackerRoomDatabase.class, "runTracker_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(createCallback)
                            //allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback createCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Log.d("g53mdp", "Rdboncreate");
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                SavedRunDAO savedRunDAO = INSTANCE.savedRunDAO();
            });
        }
    };

}
