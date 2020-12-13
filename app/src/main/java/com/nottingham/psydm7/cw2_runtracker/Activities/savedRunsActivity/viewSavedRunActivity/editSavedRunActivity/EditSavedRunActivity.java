package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity.editSavedRunActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.nottingham.psydm7.cw2_runtracker.R;

public class EditSavedRunActivity extends AppCompatActivity {

    long savedRunID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_run);

        //region "populating the spinner for sports"
        Spinner spinner = (Spinner) findViewById(R.id.editRun_spinner_SportValue);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sports_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //endregion

        //region "retrieving the bundle passed to this activity"
        Bundle bundle = getIntent().getExtras();
        savedRunID = bundle.getLong("SavedRunID");
        //endregion
    }
}