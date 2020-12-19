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

        // used if statements so for example if a run is 3m42s long it will display 03:42 instead of 00:03:42
        String getDuration = stringHours +  stringMinutes +  stringSeconds;

        return getDuration;

    }

    //endregion

    //region "formatting large time nice"
    static public String formatLargeTimeNicely(long timeMillis){

        String stringDays = "";
        String stringHours = "";
        String stringMinutes = "";



        long days= TimeUnit.MILLISECONDS.toDays(timeMillis); // converting milliseconds into days
        //if the duration is greater than an hour...
        if(TimeUnit.MILLISECONDS.toHours(timeMillis)>0){
            //.. add the extra section for hours
            stringDays = String.format("%02d", days)+" days, ";
        }

        long totalHours= TimeUnit.MILLISECONDS.toHours(timeMillis); // converting milliseconds into hours
        long actualHours =  totalHours - TimeUnit.DAYS.toHours(days); // actual hours = total hours - the number of days into hours
        //if the duration is greater than an hour...
        if(TimeUnit.MILLISECONDS.toHours(timeMillis)>0){
            //.. add the extra section for hours
            stringHours = String.format("%02d", actualHours)+" hours and ";
        }

        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis); // converting milliseconds into minutes
        long actualMinutes =  totalMinutes - TimeUnit.HOURS.toMinutes(totalHours); // actual minutes = total minutes - the number of hours into minutes

        //if we have any extra seconds increase the minute by 1, rounding it up
        if(TimeUnit.MILLISECONDS.toSeconds(timeMillis) - TimeUnit.MINUTES.toSeconds(actualMinutes) > 0)
            actualMinutes+=1;

        stringMinutes = String.format("%02d", actualMinutes)+" minutes";

        // used if statements so for example if toal runs is  20h30m long it will display "20 hours, 30 minutes" instead of "0 days, 20 hours, 30 minutes"
        String getDuration = stringDays + stringHours +  stringMinutes;

        return getDuration;

    }
    //endregion
    
    //region "rounding"

    static public float roundToDP (float number, int dp){
        return Math.round(number*(float) Math.pow(10, dp))/(float) Math.pow(10, dp);
    }

    //endregion
}
