package com.nottingham.psydm7.cw2_runtracker.runningActivity;

import android.location.Location;

public interface IRunningCallback {
    public void locationChangeEvent(Location location, Location lastLocation, float distance);
}
