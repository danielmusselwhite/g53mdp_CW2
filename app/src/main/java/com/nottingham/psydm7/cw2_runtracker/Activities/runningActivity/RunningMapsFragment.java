package com.nottingham.psydm7.cw2_runtracker.Activities.runningActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nottingham.psydm7.cw2_runtracker.R;

import java.util.ArrayList;

public class RunningMapsFragment extends Fragment {

    private static GoogleMap googleMap;
    private static ArrayList<LatLng> path = new ArrayList<LatLng>();
    private Marker currentLocation = null;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            RunningMapsFragment.googleMap = googleMap;
            googleMap.getUiSettings().setAllGesturesEnabled(false);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        path.clear();
    }

    public void updateMap(Location location, Location lastLocation) {

        LatLng thisLatLng =  new LatLng(location.getLatitude(), location.getLongitude());
        path.add(thisLatLng);

        if (currentLocation != null)
            currentLocation.remove();
        else
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(thisLatLng, 18.5f));

        currentLocation = googleMap.addMarker(new MarkerOptions().position(thisLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        if (lastLocation != null)
            googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), thisLatLng)
                    .width(7)
                    .color(Color.BLUE));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(thisLatLng));
    }

    public ArrayList<LatLng> getPath(){
        return path;
    }
}