package com.nottingham.psydm7.cw2_runtracker.Activities.savedRunsActivity.viewSavedRunActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nottingham.psydm7.cw2_runtracker.R;

import java.util.ArrayList;

public class ViewSavedRunMapsFragment extends Fragment {

    private static GoogleMap googleMap;
    private ArrayList<LatLng> path;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            ViewSavedRunMapsFragment.googleMap = googleMap;

            if(path!=null)
                updateMap(path);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void savePath(ArrayList<LatLng> path){
        this.path = path;
        if(googleMap!=null)
            updateMap(path);
    }

    private void updateMap(ArrayList<LatLng> path) {

        //region "getting the first and last coordinate in the path"
        if(!path.isEmpty()) {
            LatLng start = path.get(0);
            LatLng end = path.get(path.size() - 1);
            //endregion

            Log.d("g53mdp", "SavedRunsMapFragment, updating the map with path of size: " + path.size() + "; start coords" + start.toString() + "; end coords" + end.toString());

            //region "adding markers for start and end of the run"
            googleMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("Start of run"));
            googleMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("End of run"));
            //endregion

            //moving the camera to the start of the run
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 17f));

            //region "drawing the path to the map"
            for (int i = 0; i < path.size() - 1; i++)
                googleMap.addPolyline(new PolylineOptions()
                        .add(path.get(i), path.get(i + 1))
                        .width(7)
                        .color(Color.BLUE));
            //endregion
        }
    }
}