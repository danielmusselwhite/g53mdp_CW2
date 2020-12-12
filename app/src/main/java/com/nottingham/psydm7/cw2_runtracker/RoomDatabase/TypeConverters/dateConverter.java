package com.nottingham.psydm7.cw2_runtracker.RoomDatabase.TypeConverters;

import androidx.room.TypeConverter;

import java.util.Date;

// reference : https://developer.android.com/training/data-storage/room/referencing-data

// converting date to a long so it can be understood by room
public class dateConverter {

    @TypeConverter
    public static Date fromTimeStamp(Long timeStamp){
        return timeStamp == null ? null : new Date(timeStamp);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }

}
