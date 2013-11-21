package com.cs160.fall13.MeatUp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

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
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_lunch);
        Log.d("newlunchativity", "here");
        Intent prevIntent = getIntent();
        friendsNames = new ArrayList<String>(prevIntent.getStringArrayListExtra("invitedFriendsArray"));
        select = (Button)findViewById(R.id.btnSelect);
        time = (TextView)findViewById(R.id.textTime);
        date = (TextView)findViewById(R.id.textDate);

        select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                picker = new Dialog(NewLunchActivity.this);
                picker.setContentView(R.layout.time_date_pick_frag);
                picker.setTitle("Select Date and Time");

                datep = (DatePicker)picker.findViewById(R.id.datePicker);
                timep = (TimePicker)picker.findViewById(R.id.timePicker1);
                set = (Button)picker.findViewById(R.id.btnSet);

                set.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        month = datep.getMonth();
                        day = datep.getDayOfMonth();
                        year = datep.getYear();
                        hour = timep.getCurrentHour();
                        minute = timep.getCurrentMinute();
                        time.setText("Time is "+hour+":" +minute);
                        time.setVisibility(View.VISIBLE);
                        date.setText("The date is " + day + "/" + month + "/" + year);
                        date.setVisibility(View.VISIBLE);
                        picker.dismiss();
                    }
                });
                picker.show();

            }
        });
        getRec = (Button) findViewById(R.id.getSuggestion);
        getRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recommendationIntent = new Intent(getApplicationContext(), GetRecommendationActivity.class);
                startActivity(recommendationIntent);
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



}
