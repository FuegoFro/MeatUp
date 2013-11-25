package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecommendationFragment extends Fragment {

    private View v;
    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.get_recommendation, container, false);
        TextView tv = (TextView) v.findViewById(R.id.locName);
        tv.setText(title);
        return v;
    }

    public void setInfo(Restaurant restaurant) {
        title =  restaurant.getTitle();
    }
}
