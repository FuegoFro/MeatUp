package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.maps.android.SphericalUtil;
public class RecommendationFragment extends Fragment {

    public static final String RESTAURANT_ARGUMENT_KEY = "restaurant argument key";
    private Restaurant restaurant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.get_recommendation, container, false);
        TextView tv = (TextView) root.findViewById(R.id.locName);
        tv.setText(restaurant.getTitle());

        SupportMapFragment mapFragment = new RestaurantMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESTAURANT_ARGUMENT_KEY, restaurant);
        mapFragment.setArguments(args);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_container, mapFragment);
        fragmentTransaction.commit();

        return root;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

}
