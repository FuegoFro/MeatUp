package com.cs160.fall13.MeatUp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends ActionBarActivity {
    private static final String APP_PREFERENCES = "meatup_app_preferences";
    private static final String LUNCH_START_KEY = "meatup_preference_lunch_start";
    private static final String LUNCH_END_KEY = "meatup_preference_lunch_end";
    private Calendar startTime;
    private Calendar endTime;
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mma");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // ============= Get previous preferences =============
        // Zero out date, just care about time
        startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, 0);
        startTime.set(Calendar.DAY_OF_YEAR, 0);
        endTime = Calendar.getInstance();
        endTime.set(Calendar.YEAR, 0);
        endTime.set(Calendar.DAY_OF_YEAR, 0);

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String startString = prefs.getString(LUNCH_START_KEY, "11:15AM");
        String endString = prefs.getString(LUNCH_END_KEY, "3:30PM");
        try {
            startTime.setTime(timeFormatter.parse(startString));
            endTime.setTime(timeFormatter.parse(endString));
        } catch (ParseException e) {
            // This should never happen, we're only ever storing valid formats
            // Will default to current time
            e.printStackTrace();
        }

        // ============= Setup time pickers =============
        TextView startTimeField = (TextView) findViewById(R.id.lunch_start_time);
        TextView endTimeField = (TextView) findViewById(R.id.lunch_end_time);
        final TimePickerField startPicker = new TimePickerField(startTime, startTimeField, this);
        final TimePickerField endPicker = new TimePickerField(endTime, endTimeField, this);

        startPicker.setOnTimeChangedListener(new Runnable() {
            @Override
            public void run() {
                // If start time is after end time, move end time to be new start time
                if (startTime.compareTo(endTime) > 0) {
                    endPicker.setTime(startTime.getTime());
                    endPicker.updateTimeField();
                }
                saveTimePreferences();
            }
        });
        endPicker.setOnTimeChangedListener(new Runnable() {
            @Override
            public void run() {
                // If end time is before start time, set start time to be new end time
                if (endTime.compareTo(startTime) < 0) {
                    startPicker.setTime(endTime.getTime());
                    startPicker.updateTimeField();
                }
                saveTimePreferences();
            }
        });
    }

    private void saveTimePreferences() {
        String startString = timeFormatter.format(startTime.getTime());
        String endString = timeFormatter.format(endTime.getTime());
        getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
                .edit()
                .putString(LUNCH_START_KEY, startString)
                .putString(LUNCH_END_KEY, endString)
                .commit();
    }
}
