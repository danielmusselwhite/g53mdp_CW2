package com.nottingham.psydm7.cw2_runtracker.runningActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nottingham.psydm7.cw2_runtracker.R;

import java.util.ArrayList;

public class RunningMapsFragment extends Fragment {

    private static GoogleMap googleMap;
    private static ArrayList<LatLng> path = new ArrayList<LatLng>();
    private Marker currentLocation = null;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @SuppressLint("MissingPermission") // ONLY NEEDED FOR SETMYLOCATIONENABLED TRUE
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            RunningMapsFragment.googleMap = googleMap;
            googleMap.getUiSettings().setAllGesturesEnabled(false);

            googleMap.setMyLocationEnabled(true); // not sure if its better to have this or the marker?
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