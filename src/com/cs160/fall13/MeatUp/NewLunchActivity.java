package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class NewLunchActivity extends ActionBarActivity {
    private Calendar lunchTime;
    private TextView locationField,
                     selectedLocationField,
                     locationSearchField;
    ArrayList<String> friendsNames;
    private boolean locationSet = false;
    Lunch lunch;
    boolean isEdit;
    private final int MAX_FRIENDS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_lunch);

        Intent prevIntent = getIntent();
        isEdit = prevIntent.getBooleanExtra("isEdit", false);

        // get location select textview
        selectedLocationField = (TextView) findViewById(R.id.selected_location_text);

        if (isEdit) {
            lunch = prevIntent.getParcelableExtra("lunch");
            lunchTime = lunch.getTime();
            locationSet = true;
            this.setTitle("Edit Lunch");
        } else {
            // Initialize invitation to be next 15 minute increment
            lunchTime = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            int minute = lunchTime.get(Calendar.MINUTE);
            int minutesToAdd = 15 - minute % 15;
            lunchTime.add(Calendar.MINUTE, minutesToAdd); // Does roll over for you
        }

        // ============= Setup list of guests =============
        if (!isEdit) {
            friendsNames = prevIntent.getStringArrayListExtra("invitedFriendsArray");
        } else {
            friendsNames = lunch.getAttendees();
        }
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
        if (isEdit) {
            selectedLocationField.setText(lunch.getLocation());
            selectedLocationField.setVisibility(View.VISIBLE);
        }
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
                setTime(hourOfDay, minute, timeButton);
            }
        };
        // Extract initial values for picker
        int hour = lunchTime.get(Calendar.HOUR_OF_DAY);
        int minute = lunchTime.get(Calendar.MINUTE);
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
        final NumberPicker datePicker;
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
        if (Build.VERSION.SDK_INT >= 11) {
            // On 3.0 and higher, create custom date picker with fewer fields that need setting
            // Setup picker view
            View dialogView = getLayoutInflater().inflate(R.layout.condensed_date_picker, null);
            datePicker = (NumberPicker) dialogView.findViewById(R.id.date_picker);

            // Set dates on picker
            datePicker.setDisplayedValues(dateValues);
            datePicker.setMinValue(0);
            datePicker.setMaxValue(numDaysToShow - 1);
            datePicker.setValue(0);
            datePicker.setWrapSelectorWheel(false);
            datePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

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
            //just for later processing
            datePicker = null;
        }

        //set the date picker to open at our new date
        if (isEdit) {
            Calendar time = lunch.getTime();
            setTime(time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), timeButton);
            setTimeField(timeButton, lunchTime);
            Date currDate = time.getTime();
            for (int i = 0; i < dates.length; i++) {
               if (dates[i].getDate() == currDate.getDate() &&
                       dates[i].getMonth() == currDate.getMonth()) {
                   dateButton.setText(dateValues[i]);
                   datePicker.setValue(i);

                   break;
               }
            }
        }

        //add friends button
        TextView inviteMore = (TextView) findViewById(R.id.invite_more);
        inviteMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAddFriendsDialog();
            }
        });
    }

    private void showAddFriendsDialog () {
        if (friendsNames.size() == MAX_FRIENDS) {
            Toast.makeText(this, "No more friends to invite! Try adding some new friends.", Toast.LENGTH_SHORT).show();
        } else {
            AddFriendsDialog addFriendsDialog = new AddFriendsDialog();
            for (int i = 0; i < friendsNames.size(); i++) {
                addFriendsDialog.removeAlreadyInvited(friendsNames);
            }
            addFriendsDialog.show(getSupportFragmentManager(), "Invite More Friends");
        }
    }

    private void setTime(int hourOfDay, int minute, TextView timeButton) {
        lunchTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        lunchTime.set(Calendar.MINUTE, minute);
        setTimeField(timeButton, lunchTime);
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
                    int lunchId = (int) (Math.random() * Integer.MAX_VALUE);
                    Intent resultIntent = new Intent();
                    if (isEdit) {
                        resultIntent.putExtra("updated_lunch", lunch);
                    } else {
                        resultIntent.putExtra("lunch_time", lunchTime);
                        resultIntent.putExtra("location", selectedLocationField.getText());
                        resultIntent.putExtra("invited_friends", friendsNames);
                        resultIntent.putExtra("lunch_id", lunchId);
                    }
                    setResult(Activity.RESULT_OK, resultIntent);
                    HttpAsyncTask httpAsyncTask = new HttpAsyncTask();
                    lunch = new Lunch(lunchTime, selectedLocationField.getText().toString(), lunchId);
                    lunch.setAttendees(friendsNames);
                    httpAsyncTask.lunch = lunch;
                    httpAsyncTask.execute("http://10.10.81.102:5000/new_or_edit_lunch");
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
                    selectedLocationField.setText(restaurantName);
                    selectedLocationField.setTextColor(getResources().getColor(android.R.color.white));
                    selectedLocationField.setVisibility(View.VISIBLE);
                    locationSet = true;
                    if (isEdit) {
                        lunch.setLocation(restaurantName);
                    }
                }
                break;
            }
        }
    }

    public static String POST(String url, Lunch data){
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        try {
            // Add your data
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.getAttendees().size(); i++) {
                sb.append(data.getAttendees().get(i) + "_");
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("location", data.getLocation()));
            nameValuePairs.add(new BasicNameValuePair("attendees", sb.toString()));
            nameValuePairs.add(new BasicNameValuePair("time", Long.toString((data.getTime().getTimeInMillis()))));
            nameValuePairs.add(new BasicNameValuePair("id", Integer.toString(data.getId())));
            nameValuePairs.add(new BasicNameValuePair("identifier", "5626122483"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            return response.toString();
        } catch (ClientProtocolException e) {
            return "Error";
        } catch (IOException e) {
            return "Error";
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        Lunch lunch;

        @Override
        protected String doInBackground(String... urls) {
            Log.e("asdf", "do in background");
            return POST(urls[0], lunch);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

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
                    friendsNames.remove(name);
                }
            });
            nameField.setText(name);

            return view;
        }
    }
}
