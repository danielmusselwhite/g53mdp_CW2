package com.nottingham.psydm7.cw2_runtracker.RoomDatabase.TypeConverters;

import androidx.room.TypeConverter;

import java.sql.Date;

public class dateConverter {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }


}
