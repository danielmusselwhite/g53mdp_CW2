package com.nottingham.psydm7.cw2_runtracker.ContentProvider;

import android.net.Uri;

public class RunsContract {

    public static final String AUTHORITY = "uk.ac.nott.cs.runs";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri SAVEDRUN_URI = Uri.parse("content://"+AUTHORITY+"/savedRun_table/");

    //field names
    public static final String RUN_ID = "_id";
    public static final String RUN_NAME = "name";
    public static final String RUN_DATE = "date";
    public static final String RUN_DISTANCE = "distance";
    public static final String RUN_SPEED = "speed";
    public static final String RUN_TIME = "time";
    public static final String RUN_PATH = "path";
    public static final String RUN_SPORTINDEX = "sportIndex";
    public static final String RUN_DESCRIPTION = "description";
    public static final String RUN_EFFORTINDEX = "effortIndex";
    public static final String RUN_PICTUREPATH = "picturePath";


    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/recipes.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/recipes.data.text";

}