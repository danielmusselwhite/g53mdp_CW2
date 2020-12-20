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

    TextView nameView;
    TextView dateView;
    TextView speedView;
    TextView distanceView;
    TextView timeView;
    TextView tv_sport;

    TextView tv_description;
    TextView tv_descriptionValue;

    TextView tv_image;
    ImageView iv_associatedPhoto;

    TextView tv_effort;
    TextView tv_effortValue;

    ViewSavedRunMapsFragment mapFragment;

    RunTrackerRoomDatabase db;
    SavedRunDAO savedRunDAO;

    ViewSavedRunViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_run);

        //region "getting the different components we will update"
        nameView = findViewById(R.id.viewRunTextViewRunTitle);
        dateView = findViewById(R.id.viewRunTextViewDate);
        speedView = findViewById(R.id.viewRunTextViewSportValue);
        distanceView = findViewById(R.id.viewRunTextViewDistanceValue);
        timeView = findViewById(R.id.viewRunTextViewTimeValue);
        tv_sport = findViewById(R.id.viewRunTextViewSport);

        tv_description  = findViewById(R.id.viewRunTextViewDescription);
        tv_descriptionValue  = findViewById(R.id.viewRunTextViewDescriptionValue);
        tv_image  = findViewById(R.id.viewRunTextViewImage);
        tv_effort  = findViewById(R.id.viewRunTextViewEffort);
        tv_effortValue  = findViewById(R.id.viewRunTextViewEffortValue);
        iv_associatedPhoto = (ImageView) findViewById(R.id.viewRun_imageView_associatedPhoto);

        //getting the map fragment
        mapFragment = (ViewSavedRunMapsFragment) this.getSupportFragmentManager().findFragmentById(R.id.viewRun_mapFragment);
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

                nameView.setText(name);
                dateView.setText(dateString);
                speedView.setText(speed);
                distanceView.setText(distance);
                timeView.setText(time);
                tv_sport.setText(sport);
                mapFragment.savePath(path);

                //region "handling columns which can contain nulls"
                String description = newRun.getDescription();
                if(description!=null){
                    tv_descriptionValue.setText(description);
                    tv_description.setVisibility(View.VISIBLE);
                    tv_descriptionValue.setVisibility(View.VISIBLE);
                }
                else{
                    tv_description.setVisibility(View.GONE);
                    tv_descriptionValue.setVisibility(View.GONE);
                }

                Integer effortIndex = newRun.getEffortIndex();
                if(effortIndex!=null){
                    String effort = getResources().getStringArray(R.array.effort_array)[effortIndex];
                    tv_effortValue.setText(effort);
                    tv_effort.setVisibility(View.VISIBLE);
                    tv_effortValue.setVisibility(View.VISIBLE);
                }
                else{
                    tv_effort.setVisibility(View.GONE);
                    tv_effortValue.setVisibility(View.GONE);
                }

                String picturePath = newRun.getPicturePath();
                if(picturePath!=null){
                    try {
                        iv_associatedPhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        //TODO - either remove this or implement it; decide which look you prefer
                        //region "scaling the image for taking up 75% of the screens width
//                                        int screenWidth = ViewSavedRunActivity.this.getResources().getDisplayMetrics().widthPixels;
//                                        iv_associatedPhoto.getLayoutParams().width = (int) (0.75 * screenWidth);
                        //endregion
                        tv_image.setVisibility(View.VISIBLE);
                        iv_associatedPhoto.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Log.d("g53mdp", "Exception when trying to set image: " + e.toString());
                        iv_associatedPhoto.setImageBitmap(null);
                        tv_image.setVisibility(View.GONE);
                        iv_associatedPhoto.setVisibility(View.GONE);
                    }
                }
                else{
                    iv_associatedPhoto.setImageBitmap(null);
                    tv_image.setVisibility(View.GONE);
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