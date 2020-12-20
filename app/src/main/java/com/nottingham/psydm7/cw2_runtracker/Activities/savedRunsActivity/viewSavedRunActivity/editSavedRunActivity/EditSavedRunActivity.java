package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.editSavedRunActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.AllSavedRunsViewModel;
import com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.ViewSavedRunViewModel;
import com.nottingham.psydm7.cw2_runtracker.MyUtilities;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

public class EditSavedRunActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;

    Boolean pictureUpdated = false;
    String newPicturePath = null;

    long savedRunID;
    EditText et_Name;
    EditText et_description;
    ImageView iv_associatedPhoto;
    Button button_removeImage;
    Spinner spinner_sportValue;
    SeekBar seekBar_effort;

    EditSavedRunViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_run);

        //region "retrieving the bundle passed to this activity"
        Bundle bundle = getIntent().getExtras();
        savedRunID = bundle.getLong("SavedRunID");
        //endregion

        //region "getting references to the different views"
        et_Name = findViewById(R.id.editRun_editText_NameValue);
        iv_associatedPhoto = (ImageView) findViewById(R.id.editRun_imageView_associatedPhoto);
        button_removeImage = findViewById(R.id.editRun_button_removeImage);
        spinner_sportValue = (Spinner) findViewById(R.id.editRun_spinner_SportValue);
        et_description = findViewById(R.id.editRun_editText_DescriptionValue);
        seekBar_effort = (SeekBar) findViewById(R.id.editRun_seekBar_FeelValue);
        //endregion

        //region "populating the spinner for sports"
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sports_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_sportValue.setAdapter(adapter);
        //endregion

        // creating new view model
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(EditSavedRunViewModel.class);
        viewModel.setSavedRunID(bundle.getLong("SavedRunID"));

        //observing changes to the livedata within the viewmodel
        viewModel.getSavedRun().observe(this, newRun -> {

            et_Name.setText(newRun.getName());

            spinner_sportValue.setSelection(newRun.getSportIndex());

            String description = newRun.getDescription();
            if(description!=null)
                et_description.setText(description);
            else
                et_description.setText("");

            String picturePath = newRun.getPicturePath();
            if(picturePath!=null) {
                try {
                    iv_associatedPhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    iv_associatedPhoto.setVisibility(View.VISIBLE);
                    button_removeImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.d("g53mdp", "Exception when trying to set image: " + e.toString());
                    iv_associatedPhoto.setImageBitmap(null);
                    button_removeImage.setVisibility(View.INVISIBLE);
                    iv_associatedPhoto.setVisibility(View.INVISIBLE);
                }
            }
            else{
                iv_associatedPhoto.setImageBitmap(null);
                button_removeImage.setVisibility(View.INVISIBLE);
                iv_associatedPhoto.setVisibility(View.INVISIBLE);
            }

            Integer effort = newRun.getEffortIndex();
            if(effort!=null)
                seekBar_effort.setProgress(effort);
            else{
                seekBar_effort.setProgress(0);
            }

        });
        //endregion


    }

    //region "inter-activity communication"
    public void onButtonClick (View v) {

        // generically handling button presses via id
        switch (v.getId()) {

            case R.id.editRun_button_update: {

                Log.d("g53mdp", "Updating entry for this run!");

                //only allow the run to be saved if the name exists!
                String name = et_Name.getText().toString();




                viewModel.updateName(savedRunID, name);
                viewModel.updateSportIndex(savedRunID, spinner_sportValue.getSelectedItemPosition());
                viewModel.updateEffortIndex(savedRunID, seekBar_effort.getProgress());

                String description = et_description.getText().toString();
                viewModel.updateDescription(savedRunID, description);

                if(pictureUpdated==true)
                    viewModel.updatePicturePath(savedRunID, newPicturePath);

                finish();

                break;
            }

            case R.id.editRun_button_delete: {

                Log.d("g53mdp", "Deleting entry for this run!");

                if(viewModel.getSavedRun()!=null)
                    viewModel.getSavedRun().removeObservers(this);

                RunTrackerRoomDatabase.databaseWriteExecutor.execute(() -> {
                    viewModel.deleteRun(savedRunID);
                });

                finish();
                break;
            }

            case R.id.editRun_button_loadImage: {

                // if this doesn't have access to storage permissions, request access then return, no point in launching activity for picking image if they cannot use it
                if (ActivityCompat.checkSelfPermission(EditSavedRunActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditSavedRunActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MyUtilities.REQUEST_CODE_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    return;
                }

                Log.d("g53mdp", "Deleting entry for this run!");

                // launching gallery to pick image
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);

                break;
            }

            case R.id.editRun_button_removeImage: {
                newPicturePath=null;
                iv_associatedPhoto.setImageBitmap(null);
                iv_associatedPhoto.setVisibility(View.INVISIBLE);
                button_removeImage.setVisibility(View.INVISIBLE);
                pictureUpdated=true;
                break;
            }

        }

    }

    //region "reference: https://www.viralpatel.net/pick-image-from-galary-android-app/"
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                try{
                    newPicturePath = picturePath;
                    iv_associatedPhoto.setImageBitmap(BitmapFactory.decodeFile(newPicturePath));
                    iv_associatedPhoto.setVisibility(View.VISIBLE);
                    button_removeImage.setVisibility(View.VISIBLE);
                    pictureUpdated=true;
                }
                catch(Exception e){
                    Log.d("g53mdp","Exception when trying to set image: " + e.toString());
                }
            }
        }
    }
    //endregion

    //endregion
}