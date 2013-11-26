package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewLunchActivity extends ActionBarActivity {

    Dialog picker;
    Button select;
    Button set;
    TimePicker timep;
    DatePicker datep;
    Integer hour,minute,month,day,year;
    TextView time,date;
    Button getRec;
    ListView invitedFriendsView;
    static ArrayList<String> friendsNames;
    private static final int RESTAURANT_SELECTED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_lunch);
        Log.d("newlunchativity", "here");

        /********************************/
        /** handle android wheel widget **/
        /********************************/
        final WheelView hours = (WheelView) findViewById(R.id.hour);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(this, 0, 23);
        hourAdapter.setItemResource(R.layout.wheel_text_item);
        hourAdapter.setItemTextResource(R.id.text);
        hours.setViewAdapter(hourAdapter);

        final WheelView mins = (WheelView) findViewById(R.id.mins);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(this, 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_item);
        minAdapter.setItemTextResource(R.id.text);
        mins.setViewAdapter(minAdapter);
        mins.setCyclic(true);

        final WheelView ampm = (WheelView) findViewById(R.id.ampm);
        ArrayWheelAdapter<String> ampmAdapter =
                new ArrayWheelAdapter<String>(this, new String[] {"AM", "PM"});
        ampmAdapter.setItemResource(R.layout.wheel_text_item);
        ampmAdapter.setItemTextResource(R.id.text);
        ampm.setViewAdapter(ampmAdapter);

        // set current time
        Calendar calendar = Calendar.getInstance(Locale.US);
        hours.setCurrentItem(calendar.get(Calendar.HOUR));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        ampm.setCurrentItem(calendar.get(Calendar.AM_PM));


        final WheelView day = (WheelView) findViewById(R.id.day);
        ArrayList dates = new ArrayList();
        Date originalDate = new Date();
        int days = 10;
        long offset;
        for(int i= 0; i<= days; i++){
            offset = 86400 * 1000L * i;
            Date date = new Date( originalDate.getTime()+offset);
            dates.add(date);
        }

        day.setViewAdapter(new DayWheelAdapter(this, dates));

        /** end android wheel widget FIXME refactor this section **/




        Intent prevIntent = getIntent();
        friendsNames = new ArrayList<String>(prevIntent.getStringArrayListExtra("invitedFriendsArray"));
      //  select = (Button)findViewById(R.id.btnSelect);
      //  time = (TextView)findViewById(R.id.textTime);
      //  date = (TextView)findViewById(R.id.textDate);

  //      select.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View view) {
//                picker = new Dialog(NewLunchActivity.this);
//                picker.setContentView(R.layout.time_date_pick_frag);
//                picker.setTitle("Select Date and Time");
//
//                //datep = (DatePicker)picker.findViewById(R.id.datePicker);
//                timep = (TimePicker)picker.findViewById(R.id.timePicker1);
//                set = (Button)picker.findViewById(R.id.btnSet);
//
//                set.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
////                        month = -1;//datep.getMonth();
////                        day = -1;// datep.getDayOfMonth();
////                        year = -1;// datep.getYear();
////                        hour = -1; //timep.getCurrentHour();
////                        minute = -1; //timep.getCurrentMinute();
////                        time.setText("Time is "+hour+":" +minute);
////                        time.setVisibility(View.VISIBLE);
////                        date.setText("The date is " + day + "/" + month + "/" + year);
////                        date.setVisibility(View.VISIBLE);
////                        picker.dismiss();
//                    }
//                });
//                picker.show();

   //         }
   //     });
        getRec = (Button) findViewById(R.id.getSuggestion);
        getRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recommendationIntent = new Intent(getApplicationContext(), GetRecommendationActivity.class);
                startActivityForResult(recommendationIntent, RESTAURANT_SELECTED);
            }
        });

        invitedFriendsView = (ListView) findViewById(R.id.invitedFriendsList);
        // stupid java, you cant cast Object[] to String[] so, you have to make a stupid temp variable.
        String[] names = new String[friendsNames.size()];
        names = (String[]) friendsNames.toArray(names);
        invitedFriendsView.setAdapter(new InvitedFriendsAdapter(names));

    }


    private class InvitedFriendsAdapter extends ArrayAdapter<String> {

        private final LayoutInflater inflater;
        private static final int RESOURCE = R.layout.invited_friends;

        public InvitedFriendsAdapter(String[] objects) {
            super(getApplicationContext(), RESOURCE, objects);
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
            TextView name = (TextView) view.findViewById(R.id.invitedFriend);
            name.setText(getItem(position));

            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (RESTAURANT_SELECTED) : {
                if (resultCode == Activity.RESULT_OK) {
                    String restaurantName = data.getStringExtra("restaurant_name");
                    TextView tv = (TextView) findViewById(R.id.locationTitle);
                    tv.setText("Location - " + restaurantName);
                }
                break;
            }
        }
    }
}
