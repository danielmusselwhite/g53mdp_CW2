package com.nottingham.psydm7.cw2_runtracker.RoomDatabase.TypeConverters;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

// converting to JSON so the path for each run can easily be stored and retrieved
public class arrayListConverter {
    @TypeConverter
    public static ArrayList<LatLng> fromString(String str){
        Type listType = new TypeToken<ArrayList<LatLng>>() {}.getType();
        return new Gson().fromJson(str, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<LatLng> list){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}