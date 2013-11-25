package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RecommendationFragment extends Fragment {

    private View v;
    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.get_recommendation, container, false);
        TextView tv = (TextView) v.findViewById(R.id.locName);
        tv.setText(title);
        FragmentManager supportFragmentManager = getFragmentManager();
        Fragment fragmentById = supportFragmentManager.findFragmentById(R.id.my_map);
        GoogleMap map = ((SupportMapFragment)fragmentById).getMap();


        LatLng jupiter = new LatLng(37.86984,-122.267491);


        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(jupiter, 13));

        map.addMarker(new MarkerOptions()
                .title("The Jupiter")
                .snippet("Yummy yummy")
                .position(jupiter));
        return v;
    }

    public void setInfo(Restaurant restaurant) {
        title =  restaurant.getTitle();
    }
}
