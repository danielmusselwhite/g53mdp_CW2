package com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.TypeConverters.arrayListConverter;

import java.util.ArrayList;

@Entity(tableName = "savedRun_table")
public class SavedRun {

    @PrimaryKey(autoGenerate=true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long _id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @NonNull
    @ColumnInfo(name = "distance")
    private float distance;

    @NonNull
    @ColumnInfo(name = "speed")
    private float speed;

    @NonNull
    @ColumnInfo(name = "time")
    private String time;

    @NonNull
    @ColumnInfo(name = "path")
    @TypeConverters({arrayListConverter.class})
    private ArrayList<LatLng> path;



    public SavedRun(@NonNull String name, @NonNull String date, @NonNull float distance, @NonNull float speed, @NonNull String time, @NonNull  ArrayList<LatLng> path) {
        this.name = name;
        this.date = date;
        this.distance = distance;
        this.speed = speed;
        this.time = time;
        this.path = path;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @NonNull
    public String getTime() {
        return time;
    }

    public void setTime(@NonNull String time) {
        this.time = time;
    }

    @NonNull
    public ArrayList<LatLng> getPath() {
        return path;
    }

    public void setPath(@NonNull ArrayList<LatLng> path) {
        this.path = path;
    }
}
