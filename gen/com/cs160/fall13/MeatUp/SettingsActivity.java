package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class SettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        TextView lunchStartTime = (TextView) findViewById(R.id.lunch_start_time);
        TextView lunchEndTime = (TextView) findViewById(R.id.lunch_end_time);


    }
}
