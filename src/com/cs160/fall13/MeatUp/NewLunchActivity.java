package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class NewLunchActivity extends ActionBarActivity {
    private Calendar lunchTime;
    private TextView locationField;
    private TextView locationSearchField;
    ArrayList<String> friendsNames;
    private boolean locationSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_lunch);

        // Initialize invitation to be next 15 minute increment
        lunchTime = Calendar.getInstance();
//        Calendar calendar = Calendar.getInstance();
        int minute = lunchTime.get(Calendar.MINUTE);
        int minutesToAdd = 15 - minute % 15;
        lunchTime.add(Calendar.MINUTE, minutesToAdd); // Does roll over for you

        // ============= Setup list of guests =============
        Intent prevIntent = getIntent();
        friendsNames = prevIntent.getStringArrayListExtra("invitedFriendsArray");
        if (friendsNames == null) {
            // Just in case we get to this activity in a strange way, better to not show invited people than to crash
            friendsNames = new ArrayList<String>();
        }
        ListView invitedFriendsView = (ListView) findViewById(R.id.invitedFriendsList);
        invitedFriendsView.setAdapter(new InvitedFriendsAdapter(friendsNames));

        // ============= Setup location search =============
        locationSearchField = (TextView) findViewById(R.id.location_search_field);
        locationSearchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getApplicationContext(), SearchRestaurantActivity.class);
                startActivityForResult(searchIntent, GetRecommendationActivity.RESTAURANT_SELECTED);
            }
        });

        // ============= Setup location suggestion =============
        locationField = (TextView) findViewById(R.id.location_field);
        locationField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recommendationIntent = new Intent(getApplicationContext(), GetRecommendationActivity.class);
                startActivityForResult(recommendationIntent, GetRecommendationActivity.RESTAURANT_SELECTED);
            }
        });

        // ============= Setup time picker =============
        final TextView timeButton = (TextView) findViewById(R.id.time_button);
        setTimeField(timeButton, lunchTime);
        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                lunchTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                lunchTime.set(Calendar.MINUTE, minute);
                setTimeField(timeButton, lunchTime);
            }
        };
        // Extract initial values for picker
        int hour = lunchTime.get(Calendar.HOUR_OF_DAY);
        minute = lunchTime.get(Calendar.MINUTE);
        final TimePickerDialog timeDialog = new TimePickerDialog(this, timeSetListener, hour, minute, false);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog.show();
            }
        });

        // ============= Setup date picker =============
        final TextView dateButton = (TextView) findViewById(R.id.date_button);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd");
        if (Build.VERSION.SDK_INT >= 11) {
            // On 3.0 and higher, create custom date picker with fewer fields that need setting
            // Setup picker view
            View dialogView = getLayoutInflater().inflate(R.layout.condensed_date_picker, null);
            final NumberPicker datePicker = (NumberPicker) dialogView.findViewById(R.id.date_picker);

            // Generate list of dates
            int numDaysToShow = 14;
            final String[] dateValues = new String[numDaysToShow];
            final Date[] dates = new Date[numDaysToShow];
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < numDaysToShow; i++) {
                dates[i] = calendar.getTime();
                // Special case today and tomorrow
                if (i == 0) {
                    dateValues[i] = "Today";
                } else if (i == 1) {
                    dateValues[i] = "Tomorrow";
                } else {
                    dateValues[i] = dateFormat.format(calendar.getTime());
                }
                calendar.add(Calendar.DATE, 1);
            }
            // Set dates on picker
            datePicker.setDisplayedValues(dateValues);
            datePicker.setMinValue(0);
            datePicker.setMaxValue(numDaysToShow - 1);
            datePicker.setValue(0);
            datePicker.setWrapSelectorWheel(false);

            // Create dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            builder.setTitle("Set date");
            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    int value = datePicker.getValue();
                    // Set year, month, day on lunch time
                    GregorianCalendar tempCal = new GregorianCalendar();
                    tempCal.setTime(dates[value]);
                    for (int field : new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH}) {
                        lunchTime.set(field, tempCal.get(field));
                    }
                    dateButton.setText(dateValues[value]);
                }
            });
            final AlertDialog dateDialog = builder.create();
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateDialog.show();
                }
            });
        } else {
            // Gingerbread and lower don't have the number picker. Just use the built in date picker.
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    lunchTime.set(Calendar.YEAR, year);
                    lunchTime.set(Calendar.MONTH, monthOfYear);
                    lunchTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateButton.setText(dateFormat.format(lunchTime));
                }
            };
            int year = lunchTime.get(Calendar.YEAR);
            int month = lunchTime.get(Calendar.MONTH);
            int day = lunchTime.get(Calendar.DAY_OF_MONTH);
            final DatePickerDialog dateDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateDialog.show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lunch_invitation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_invite:
                if (!locationSet) {
                    // Require them to select a location
                    Toast.makeText(this, "Please select a location for your meet up", Toast.LENGTH_SHORT).show();
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("lunch_time", lunchTime);
                    resultIntent.putExtra("location", locationField.getText());
                    resultIntent.putExtra("invited_friends", friendsNames);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
                // Falls-through
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (GetRecommendationActivity.RESTAURANT_SELECTED): {
                if (resultCode == Activity.RESULT_OK) {
                    String restaurantName = data.getStringExtra("restaurant_name");
                    locationField.setText(restaurantName);
                    locationField.setTextColor(getResources().getColor(android.R.color.white));
                    locationSet = true;
                }
                break;
            }
        }
    }

    private void setTimeField(TextView timeField, Calendar calendar) {
        String timeString = new SimpleDateFormat("h:mma").format(calendar.getTime());
        timeField.setText(timeString);
    }

    private class InvitedFriendsAdapter extends ArrayAdapter<String> {
        private final LayoutInflater inflater;
        private static final int RESOURCE = R.layout.invited_friends;

        public InvitedFriendsAdapter(List<String> names) {
            super(getApplicationContext(), RESOURCE, names);
            inflater = getLayoutInflater();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = inflater.inflate(RESOURCE, parent, false);
            } else {
                view = convertView;
            }
            final String name = getItem(position);
            TextView nameField = (TextView) view.findViewById(R.id.invited_friend_name);
            View removeFriendButton = view.findViewById(R.id.remove_friend_button);
            removeFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(name);
                }
            });
            nameField.setText(name);

            return view;
        }
    }
}
