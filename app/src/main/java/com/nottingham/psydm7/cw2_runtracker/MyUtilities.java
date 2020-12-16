package com.nottingham.psydm7.cw2_runtracker;

import java.util.concurrent.TimeUnit;

public class MyUtilities {
    //region "formatting time nice"
    static public String formatTimeNicely(long timeMillis){
        String stringHours = "";
        String stringMinutes = "";
        String stringSeconds = "";

        long hours= TimeUnit.MILLISECONDS.toHours(timeMillis); // converting milliseconds into hours
        //if the duration is greater than an hour...
        if(TimeUnit.MILLISECONDS.toHours(timeMillis)>0){
            //.. add the extra section for hours
            stringHours = String.format("%02d", hours);
        }

        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis); // converting milliseconds into minutes
        long actualMinutes =  totalMinutes - TimeUnit.HOURS.toMinutes(hours); // actual minutes = total minutes - the number of hours into minutes
        stringMinutes = String.format("%02d", actualMinutes)+":";

        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis); // converting milliseconds into seconds
        long actualSeconds = totalSeconds - TimeUnit.MINUTES.toSeconds(totalMinutes); // actual seconds = totalseconds - number of seconds in the minutes
        stringSeconds = String.format("%02d", actualSeconds);

        // used if statements so for example if a song is 3m42s long it will display 03:42 instead of 00:03:42
        String getDuration = stringHours +  stringMinutes +  stringSeconds;

        return getDuration;

    }

    //endregion
}
