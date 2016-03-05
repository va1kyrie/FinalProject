package hu.ait.android.helen.finalproject.fragments;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import hu.ait.android.helen.finalproject.R;
import hu.ait.android.helen.finalproject.data.CountriesVisited;
import hu.ait.android.helen.finalproject.data.MyTransaction;

/**
 * Created by Helen on 5/12/2015.
 */
public class MapPage extends FragmentActivity {

    private GoogleMap gMap;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_frag);
        setUpMapIfNeeded();

        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        //gMap.setMyLocationEnabled(true);
    }//onCreate

    private void populateMap() {
        Geocoder geo = new Geocoder(this, Locale.getDefault());

        List<CountriesVisited> countries =
                CountriesVisited.listAll(CountriesVisited.class);

        List<Address> place = null;
        for(int i = 0; i < countries.size(); i++){
            try {
                place = geo.getFromLocationName(countries.get(i).getCountry(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }//catch

            if(place != null){
                Address mark = place.get(0);
                LatLng coords = new LatLng(mark.getLatitude(), mark.getLongitude());
                setMapMarker(coords, countries.get(i).getCountry());
            }//if

        }//for
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (gMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            gMap = ((SupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (gMap != null) {
                populateMap();
            }//if
        }//if
    }//setUpMapIfNeeded

    private void setMapMarker(LatLng coords, String name) {
        gMap.addMarker(new MarkerOptions().position(coords).title(name));
    }//setUpMap

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
}//class MapPage
