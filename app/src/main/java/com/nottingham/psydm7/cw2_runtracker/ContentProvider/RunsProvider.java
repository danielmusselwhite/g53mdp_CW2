package com.nottingham.psydm7.cw2_runtracker.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteProgram;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

public class RunsProvider extends ContentProvider {

    RunTrackerRoomDatabase db=null;
    SavedRunDAO savedRunsDAO;

    //region "URIMatcher to match specific URI paths to the tables in your database, used for querying and inserting"
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RunsContract.AUTHORITY, "savedRun_table", 1);
    }
    //endregion

    //region "Instantiating ContentProvider from another component in this or another activity"
    // called when ContentProvider is instantiated by another component in this or another activity
    // creates a handle to the underlying database + the DAOs
    @Override
    public boolean onCreate() {
        Log.d("g53mdp", "contentprovider oncreate");

        db = RunTrackerRoomDatabase.getDatabase(this.getContext().getApplicationContext());
        savedRunsDAO = db.savedRunDAO();
        return true;
    }
    //endregion

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //region "reference https://stackoverflow.com/questions/49221753/exposing-a-room-database-to-other-apps"
        final Context context = getContext();
        if (context == null)
            return null;

        switch (uriMatcher.match(uri)) {
            case 1:
            SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
            builder.setTables("savedRun_table");
            String query = builder.buildQuery(projection, selection, null, null, sortOrder, null);

            final Cursor cursor = db
                    .getOpenHelper()
                    .getWritableDatabase()
                    .query(query, selectionArgs);

            cursor.setNotificationUri(context.getContentResolver(), uri);
            Log.d("g53mdp","Returning the result of the savedRuns query");
            return cursor;

            default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //endregion
    }

    //region "getType determines the form of the data users of the ContentProvder will get back"
    // eg is it a cursor pointing to a single item, or a cursor pointing to a number of items
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String contentType;

        if (uri.getLastPathSegment()==null) {
            contentType = RunsContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = RunsContract.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }
    //endregion

    //region "Uneeded methods brief states only that data can be accessed"
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
    //endregion
}
