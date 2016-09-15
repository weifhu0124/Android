package com.weifeng.android.callmemaybe;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Fragment that holds the time picker dialog
 * Created by weifenghu on 8/12/2016.
 */
public class TaskTimeFragment extends DialogFragment{
    public static final String EXTRA_TIME = "com.weifeng.android.callmemaybe.time";
    private Calendar mCalendar;
    // for some reason it gives me the wrong year
    private int year = 0;
    private int month = 0;
    private int day = 0;

    // pass data to this fragment
    public static TaskTimeFragment newInstance(Calendar calendar){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, calendar);
        TaskTimeFragment fragment = new TaskTimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode){
        if (getTargetFragment() == null)
            return;

        Intent i = new Intent();
        i.putExtra(EXTRA_TIME, mCalendar);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // extract the time
        mCalendar = (Calendar)getArguments().getSerializable(EXTRA_TIME);

        View v = getActivity().getLayoutInflater().inflate(R.layout.time_pick_task, null);
        TimePicker tPicker = (TimePicker)v.findViewById(R.id.dialog_timePicker);
        tPicker.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance();

        // create a Calendar to get hour and min
        if (mCalendar != null) {
            calendar.setTime(mCalendar.getTime());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            if (Build.VERSION.SDK_INT >= 23 ) {
                tPicker.setHour(hour);
                tPicker.setMinute(min);
            }else {
                tPicker.setCurrentHour(hour);
                tPicker.setCurrentMinute(min);
            }
        }
        tPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(year, month, day, hourOfDay, minute);

                // update argument to preserve selected value on rotation
                getArguments().putSerializable(EXTRA_TIME, mCalendar);
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.what_time)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // warn if the set time is before current time
                        Calendar current = Calendar.getInstance();
                        int hourNow = current.get(Calendar.HOUR_OF_DAY);
                        int minNow = current.get(Calendar.MINUTE);
                        int hourSet = mCalendar.get(Calendar.HOUR_OF_DAY);
                        int minSet = mCalendar.get(Calendar.MINUTE);
                        // compare the hour first
                        if (hourNow > hourSet) {
                            createWarning();
                        } else if (hourNow == hourSet) {
                            // if the hour is the same, compare the minutes
                            if (minNow > minSet) {
                                createWarning();
                            }
                        }
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    // warning dialog if the set time is before the current time
    private void createWarning(){
        AlertDialog warns = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.warning)
                .setPositiveButton(android.R.string.ok, null)
                .create();
        warns.show();
    }
}
