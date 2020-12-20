package com.nottingham.psydm7.cw2_runtracker.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQuery;
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

        Log.d("g53mdp-2", "Querying: "+uri.toString() + " " + uriMatcher.match(uri));

        Cursor cursor = null;

        //region "Using the URI to insert to the correct table"
        switch(uriMatcher.match(uri)) {
            case 1:
                cursor =  savedRunsDAO.getMultipleSavedRunsCursor();
//                cursor = db.query(new SimpleSQLiteQuery("SELECT * FROM savedRun_table "));
                break;
            default:
                Log.d("g53mdp","URI for this query doesn't match to a valid table");
        }
        //endregion

        if (getContext() != null && cursor!=null) {
            cursor.setNotificationUri(getContext()
                    .getContentResolver(), uri);
            return cursor;
        }

        return null;
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

    //region "Uneeded methods, asked Martin and he said that he only wants us to query the database and that there are no marks to be gained for implementing insert, update or delete"
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // NOT implemented as I asked Martin as brief only says you have to implement query and he confirmed that no extra marks available for delete, update, or insert
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // NOT implemented as I asked Martin as brief only says you have to implement query and he confirmed that no extra marks available for delete, update, or insert
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // NOT implemented as I asked Martin as brief only says you have to implement query and he confirmed that no extra marks available for delete, update, or insert
        return 0;
    }
    //endregion
}
