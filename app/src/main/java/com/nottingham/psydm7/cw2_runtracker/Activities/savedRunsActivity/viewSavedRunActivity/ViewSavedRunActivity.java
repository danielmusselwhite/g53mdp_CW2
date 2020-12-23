package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity.RunningActivity;
import com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity.RunningMapsFragment;
import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.editSavedRunActivity.EditSavedRunActivity;
import com.nottingham.psydm7.cw2_runtracker.MyUtilities;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewSavedRunActivity extends AppCompatActivity {

    TextView textView_name;
    TextView textView_date;
    TextView textView_speed;
    TextView textView_distance;
    TextView textView_time;
    TextView textView_sport;

    TextView textView_description;
    TextView textView_descriptionValue;

    TextView textView_image;
    ImageView iv_associatedPhoto;

    TextView textView_effort;
    TextView textView_effortValue;

    ViewSavedRunMapsFragment mapFragment;

    RunTrackerRoomDatabase db;
    SavedRunDAO savedRunDAO;

    ViewSavedRunViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_run);

        //region "getting the different components we will update"
        textView_name = findViewById(R.id.viewRunTextViewRunTitle);
        textView_date = findViewById(R.id.viewRunTextViewDate);
        textView_speed = findViewById(R.id.viewRunTextViewSportValue);
        textView_distance = findViewById(R.id.viewRunTextViewDistanceValue);
        textView_time = findViewById(R.id.viewRunTextViewTimeValue);
        textView_sport = findViewById(R.id.viewRunTextViewSport);

        textView_description  = findViewById(R.id.viewRunTextViewDescription);
        textView_descriptionValue  = findViewById(R.id.viewRunTextViewDescriptionValue);
        textView_image  = findViewById(R.id.viewRunTextViewImage);
        textView_effort  = findViewById(R.id.viewRunTextViewEffort);
        textView_effortValue  = findViewById(R.id.viewRunTextViewEffortValue);
        iv_associatedPhoto = (ImageView) findViewById(R.id.viewRun_imageView_associatedPhoto);
        //endregion

        //region "setting up map, doing on a separate thread so it doesn't slow down main activity / freeze UI"
        new Thread(new Runnable() {

            public void run() {
                mapFragment = (ViewSavedRunMapsFragment) ViewSavedRunActivity.this.getSupportFragmentManager().findFragmentById(R.id.viewRun_mapFragment);
            }
        }).start();
        //endregion

        //region "getting room database and DAOs"
        db = RunTrackerRoomDatabase.getDatabase(getApplicationContext());
        savedRunDAO = db.savedRunDAO();
        //endregion

        //region "retrieving the bundle passed to this activity"
        Bundle bundle = getIntent().getExtras();
        //endregion

        // creating new view model
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(ViewSavedRunViewModel.class);
        viewModel.setSavedRunID(bundle.getLong("SavedRunID"));

        //observing changes to the livedata within the viewmodel
        viewModel.getSavedRun().observe(this, newRun -> {
            //if this entry has been deleted
            if(newRun==null){
                finish();
            }
            //else this has been updated
            else {
                String name = newRun.getName();
                DateFormat dateFormat = new SimpleDateFormat("MMM, dd, yyyy 'at' HH:mm");
                String dateString = dateFormat.format(newRun.getDate());
                String speed = (MyUtilities.roundToDP(newRun.getSpeed(),2) + " km/h");
                String distance = (MyUtilities.roundToDP(newRun.getDistance(),2) + " km");
                String time = MyUtilities.formatTimeNicely(newRun.getTime());
                ArrayList<LatLng> path = newRun.getPath();
                String sport = getResources().getStringArray(R.array.sports_array)[newRun.getSportIndex()];

                textView_name.setText(name);
                textView_date.setText(dateString);
                textView_speed.setText(speed);
                textView_distance.setText(distance);
                textView_time.setText(time);
                textView_sport.setText(sport);
                mapFragment.savePath(path);

                //region "handling columns which can contain nulls"
                String description = newRun.getDescription();
                if(description!=null){
                    textView_descriptionValue.setText(description);
                    textView_description.setVisibility(View.VISIBLE);
                    textView_descriptionValue.setVisibility(View.VISIBLE);
                }
                else{
                    textView_description.setVisibility(View.GONE);
                    textView_descriptionValue.setVisibility(View.GONE);
                }

                Integer effortIndex = newRun.getEffortIndex();
                if(effortIndex!=null){
                    String effort = getResources().getStringArray(R.array.effort_array)[effortIndex];
                    textView_effortValue.setText(effort);
                    textView_effort.setVisibility(View.VISIBLE);
                    textView_effortValue.setVisibility(View.VISIBLE);
                }
                else{
                    textView_effort.setVisibility(View.GONE);
                    textView_effortValue.setVisibility(View.GONE);
                }

                String picturePath = newRun.getPicturePath();
                if(picturePath!=null){
                    try {
                        iv_associatedPhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        //region "scaling the image for taking up 75% of the screens width
//                                        int screenWidth = ViewSavedRunActivity.this.getResources().getDisplayMetrics().widthPixels;
//                                        iv_associatedPhoto.getLayoutParams().width = (int) (0.75 * screenWidth);
                        //endregion
                        textView_image.setVisibility(View.VISIBLE);
                        iv_associatedPhoto.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Log.d("g53mdp", "Exception when trying to set image: " + e.toString());
                        iv_associatedPhoto.setImageBitmap(null);
                        textView_image.setVisibility(View.GONE);
                        iv_associatedPhoto.setVisibility(View.GONE);
                    }
                }
                else{
                    iv_associatedPhoto.setImageBitmap(null);
                    textView_image.setVisibility(View.GONE);
                    iv_associatedPhoto.setVisibility(View.GONE);
                }
                //endregion
            }
        });
    }

    //region "inter-activity communication"
    public void onButtonClick (View v) {

        // generically handling button presses via id
        switch (v.getId()) {

            case R.id.editButton: {

                Bundle bundle = new Bundle();
                bundle.putLong("SavedRunID",viewModel.getSavedRunID());
                Intent intent = new Intent(ViewSavedRunActivity.this, EditSavedRunActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
                break;
            }

        }

    }

    //endregion
}