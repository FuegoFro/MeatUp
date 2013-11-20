package com.cs160.fall13.MeatUp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class NewLunchActivity extends ActionBarActivity {

    Dialog picker;
    Button select;
    Button set;
    TimePicker timep;
    DatePicker datep;
    Integer hour,minute,month,day,year;
    TextView time,date;
    Button getRec;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_lunch);
        Log.d("newlunchativity", "here");

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

                        date.setText("The date is "+day+"/"+month+"/"+year);
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
    }



}
