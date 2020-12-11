package com.nottingham.psydm7.cw2_runtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.nottingham.psydm7.cw2_runtracker.runningActivity.RunningActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("g53mdp","MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //region "inter-activity communication"
    public void onButtonClick (View v) {

        // generically handling button presses via id
        switch (v.getId()) {

            case R.id.button_startRun: {

                // if this doesn't have access to location permissions, request access, no point in launching activity if they cannot use it
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RunningActivity.REQUEST_CODE_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    return;
                }

                Log.d("g53mdp", "Starting the running activity!");
                Intent intent = new Intent(MainActivity.this, RunningActivity.class);
                startActivity(intent);
                break;
            }


            case R.id.button_pastRuns: {
                break;
            }

            case R.id.button_records: {
                break;
            }

        }

    }

    //endregion

}