package com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.TypeConverters.arrayListConverter;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.TypeConverters.dateConverter;

import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "savedRun_table")
public class SavedRun {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long _id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "date")
    @TypeConverters({dateConverter.class})
    private Date date;

    @NonNull
    @ColumnInfo(name = "distance")
    private float distance;

    @NonNull
    @ColumnInfo(name = "speed")
    private float speed;

    @NonNull
    @ColumnInfo(name = "time")
    private long time;

    @NonNull
    @ColumnInfo(name = "path")
    @TypeConverters({arrayListConverter.class})
    private ArrayList<LatLng> path;


    @NonNull
    @ColumnInfo(name = "sportIndex")
    private int sportIndex;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "effortIndex")
    private Integer effortIndex; //can be NULL before annotating so Integer not int

    @ColumnInfo(name = "picturePath")
    private String picturePath;

    public SavedRun(@NonNull String name, @NonNull Date date, @NonNull float distance, @NonNull float speed, @NonNull long time, @NonNull ArrayList<LatLng> path, @NonNull int sportIndex) {
        this.name = name;
        this.date = date;
        this.distance = distance;
        this.speed = speed;
        this.time = time;
        this.path = path;
        this.sportIndex = sportIndex;
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
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
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
    public long getTime() {
        return time;
    }

    public void setTime(@NonNull long time) {
        this.time = time;
    }

    @NonNull
    public ArrayList<LatLng> getPath() {
        return path;
    }

    public void setPath(@NonNull ArrayList<LatLng> path) {
        this.path = path;
    }

    public int getSportIndex() {
        return sportIndex;
    }

    public void setSportIndex(int sportIndex) {
        this.sportIndex = sportIndex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Integer getEffortIndex() {
        return effortIndex;
    }

    public void setEffortIndex(Integer effortIndex) {
        this.effortIndex = effortIndex;
    }
}