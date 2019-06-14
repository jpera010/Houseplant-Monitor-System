package com.jaynew.houseplantmonitor;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener temp = (TimePickerDialog.OnTimeSetListener)getActivity();
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(), hour, minute, true);
    }

    public static TimePickerFragment instance(int viewId){
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle b = new Bundle();
        b.putInt("KEY_ID", viewId);
        fragment.setArguments(b);
        return fragment;
    }


}
