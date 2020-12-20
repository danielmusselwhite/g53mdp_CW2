package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.nottingham.psydm7.cw2_runtracker.Activities.mainActivity.MainActivity;
import com.nottingham.psydm7.cw2_runtracker.Activities.mainActivity.MainViewModel;
import com.nottingham.psydm7.cw2_runtracker.R;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.DAOs.SavedRunDAO;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.Entities.SavedRun;
import com.nottingham.psydm7.cw2_runtracker.RoomDatabase.RunTrackerRoomDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class AllSavedRunsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Spinner spinner_sorting;

    AllSavedRunsViewModel viewModel;

    LiveData<List<SavedRun>> savedRuns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_saved_runs);

        //region "setting up the recycler view"
        recyclerView = findViewById(R.id.savedRuns_recyclerView);
        final AllSavedRunsRecyclerViewAdapter adapter = new AllSavedRunsRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //endregion

        //region "getting references to the different views"
        spinner_sorting = (Spinner) findViewById(R.id.savedRuns_spinner_sorting);
        //endregion

        //region "populating the spinner for sorting"
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sorting_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_sorting.setAdapter(spinnerAdapter);
        //endregion

        //endregion

        // creating the view model
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AllSavedRunsViewModel.class);

        //region "sorting entries based on the spinners value"
        spinner_sorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Log.d("g53mdp","sorting spinner has changed to: '"+getResources().getStringArray(R.array.sorting_array)[pos]+"'");

                //removing previous observers if one exists
                if(viewModel.getSavedRuns()!=null)
                    viewModel.getSavedRuns().removeObservers(AllSavedRunsActivity.this);

                viewModel.updateSavedRuns(pos);

                viewModel.getSavedRuns().observe(AllSavedRunsActivity.this, newRuns -> {
                    Log.d("g53mdp","updating the recycler view");
                    ((AllSavedRunsRecyclerViewAdapter) recyclerView.getAdapter()).setData(newRuns);
                });
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });





    }

}