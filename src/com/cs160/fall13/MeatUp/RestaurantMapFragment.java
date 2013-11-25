package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RestaurantMapFragment extends SupportMapFragment {
    private Restaurant restaurant = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.restaurant = (Restaurant) getArguments().getSerializable(RecommendationFragment.RESTAURANT_ARGUMENT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupMap();
        return view;
    }

    private void setupMap() {
        if (restaurant != null) {
            GoogleMap map = getMap();
            LatLng location = new LatLng(restaurant.getLat(), restaurant.getLon());


            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

            map.addMarker(new MarkerOptions()
                    .title(restaurant.getTitle())
                    .snippet("Yummy yummy")
                    .position(location));
        }
    }
}
