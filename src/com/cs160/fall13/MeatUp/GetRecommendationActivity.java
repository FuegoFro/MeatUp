package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class GetRecommendationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_recommendation);

        Restaurant[] restaurants = {
                new Restaurant( "test", 1000, 1000, false, false, 4)

        };
    }
}
