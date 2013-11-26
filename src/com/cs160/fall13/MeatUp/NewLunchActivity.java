package com.cs160.fall13.MeatUp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class NewLunchActivity extends ActionBarActivity {
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_lunch);

        // Initialize invitation to be next 15 minute increment
        Calendar calendar = Calendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        int minutesToAdd = 15 - minute % 15;
        calendar.add(Calendar.MINUTE, minutesToAdd); // Does roll over for you
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        // ============= Setup list of guests =============
        Intent prevIntent = getIntent();
        ArrayList<String> friendsNames = prevIntent.getStringArrayListExtra("invitedFriendsArray");
        if (friendsNames == null) {
            // Just in case we get to this activity in a strange way, better to not show invited people than to crash
            friendsNames = new ArrayList<String>();
        }
        ListView invitedFriendsView = (ListView) findViewById(R.id.invitedFriendsList);
        invitedFriendsView.setAdapter(new InvitedFriendsAdapter(friendsNames));

        // ============= Setup location suggestion =============
        View locationField = findViewById(R.id.location_field);
        locationField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recommendationIntent = new Intent(getApplicationContext(), GetRecommendationActivity.class);
                startActivity(recommendationIntent);
            }
        });

        // ============= Setup time picker =============
        final TextView timeButton = (TextView) findViewById(R.id.time_button);
        setTimeField(timeButton, hour, minute);
        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                NewLunchActivity.this.minute = minute;
                setTimeField(timeButton, hourOfDay, minute);
            }
        };
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
            Date[] dates = new Date[numDaysToShow];
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
                    dateButton.setText(dateValues[datePicker.getValue()]);
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
                    Date currentDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
                    dateButton.setText(dateFormat.format(currentDate));
                }
            };
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
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

    private void setTimeField(TextView timeField, int hourOfDay, int minute) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String timeString = new SimpleDateFormat("hh:mma").format(calendar.getTime());
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
