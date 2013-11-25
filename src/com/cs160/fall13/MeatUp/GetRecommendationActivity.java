package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetRecommendationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_recommendation);

        Restaurant[] restaurants = {
                new Restaurant( "La Val's", 37.8755322, -122.2603641, true, true, 4),
                new Restaurant("The Cheese Board", 37.8799915, -122.2694861, true, true, 4.5),
                new Restaurant("Herbivore", 37.864683,-122.266847, true, true, 3.5),
                new Restaurant("Saturn Cafe", 37.8697487,-122.2660694, true, true, 3.5),
                new Restaurant("Cafe Gratitude", 37.8759474,-122.269075, true, true, 3.5),
                new Restaurant("Udupi Palace", 37.8717547,-122.2728575, true, true, 4),
                new Restaurant("Urbann Turbann", 37.8752509,-122.2602815, true, true, 4),
                new Restaurant("Gregoire's", 37.878695,-122.268545, true, true, 4.5),
                new Restaurant("La Note", 37.8661957,-122.2673496, true, true, 4),
                new Restaurant("The Jupiter", 37.86984,-122.267491, true, true, 4),
                new Restaurant("Chez Pannisse", 37.8795938,-122.2689348, true, true, 4),
                new Restaurant("McDonald's",37.87239,-122.268728, true, true, 2.5),
                new Restaurant("Bear's Ramen House", 37.8679625,-122.2579367, true, true, 3.5),
                new Restaurant("Cafe Strada", 37.8692854,-122.2546207,true, true, 4),
                new Restaurant("Thallasa", 37.86635,-122.267166, true, true, 4)
        };


            // Get a handle to the Map Fragment
            GoogleMap map = ((MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            LatLng jupiter = new LatLng(37.86984,-122.267491);

            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(jupiter, 13));

            map.addMarker(new MarkerOptions()
                    .title("The Jupiter")
                    .snippet("Yummy yummy")
                    .position(jupiter));
    }
}
