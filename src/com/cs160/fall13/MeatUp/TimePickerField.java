package com.cs160.fall13.MeatUp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePickerField {
    private Runnable timeSetAction = null;
    private Calendar time;
    private TextView field;
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("h:mma");


    TimePickerField(final Calendar time, final TextView field, Context context) {
        this.time = time;
        this.field = field;
        updateTimeField();
        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTime(hourOfDay, minute);
                if (timeSetAction != null) {
                    timeSetAction.run();
                }
            }
        };
        // Extract initial values for picker
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        final TimePickerDialog timeDialog = new TimePickerDialog(context, timeSetListener, hour, minute, false);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog.show();
            }
        });
    }

    public void setOnTimeChangedListener(Runnable listener) {
        timeSetAction = listener;
    }

    public void setTime(Date newTime) {
        time.setTime(newTime);
    }

    public void setTime(int hourOfDay, int minute) {
        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
        time.set(Calendar.MINUTE, minute);
        updateTimeField();
    }

    public void updateTimeField() {
        String timeString = TIME_FORMATTER.format(time.getTime());
        field.setText(timeString);
    }
}
