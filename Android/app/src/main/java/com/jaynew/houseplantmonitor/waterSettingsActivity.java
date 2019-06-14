package com.jaynew.houseplantmonitor;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class waterSettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private Button mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;
    private Button dailyButton, weeklyButton;
    private Button lightStartButton, lightEndButton;
    private DatabaseReference reff, lightOnReff, lightOffReff;
    private EditText hourInput, minuteInput;
    private SeekBar waterAmountBar;
    private TextView waterAmountView;
    int waterAmount;
    int schedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_settings);

        mondayButton = (Button)findViewById(R.id.mondayButton);
        tuesdayButton = (Button)findViewById(R.id.tuesdayButton);
        wednesdayButton = (Button)findViewById(R.id.wednesdayButton);
        thursdayButton = (Button)findViewById(R.id.thursdayButton);
        fridayButton = (Button)findViewById(R.id.fridayButton);
        saturdayButton = (Button)findViewById(R.id.saturdayButton);
        sundayButton = (Button)findViewById(R.id.sundayButton);
        dailyButton = (Button)findViewById(R.id.dailyButton);
        weeklyButton = (Button)findViewById(R.id.weeklyButton);
        hourInput = (EditText)findViewById(R.id.hourInput);
        minuteInput = (EditText)findViewById(R.id.minuteInput);

        lightStartButton = (Button)findViewById(R.id.lightStartTimeButton);
        lightEndButton = (Button)findViewById(R.id.lightEndTimeButton);

        waterAmountBar = (SeekBar)findViewById(R.id.waterAmountBar);
        waterAmount = waterAmountBar.getProgress();
        waterAmountView = (TextView)findViewById(R.id.waterAmountView);
        waterAmountView.setText("Water amount (mL): " + waterAmount);


        reff = FirebaseDatabase.getInstance().getReference().child("care").child("waterSchedule");
        lightOnReff = FirebaseDatabase.getInstance().getReference().child("care").child("lightScheduleOn");
        lightOffReff = FirebaseDatabase.getInstance().getReference().child("care").child("lightScheduleOff");

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hourInput.setText(dataSnapshot.child("hour").getValue().toString());
                minuteInput.setText(dataSnapshot.child("minute").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ///// sunday = 0, monday = 1...
        mondayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("day").setValue(1);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        tuesdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("day").setValue(2);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        wednesdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("day").setValue(3);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        thursdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("day").setValue(4);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        fridayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("day").setValue(5);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        saturdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("day").setValue(6);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        sundayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("day").setValue(0);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("setting").setValue(1);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("setting").setValue(2);
                reff.child("hour").setValue(Integer.parseInt(hourInput.getText().toString()));
                reff.child("minute").setValue(Integer.parseInt(minuteInput.getText().toString()));
            }
        });

        waterAmountBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                waterAmount = waterAmountBar.getProgress();
                waterAmountView.setText("Water amount (mL): " + waterAmount);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (waterAmount < 65) {
                    reff.child("waterSetting").setValue(1);
                }
                else {
                    reff.child("waterSetting").setValue(2);
                }
            }
        });

        lightStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedButton = 1;
                TimePickerFragment timeStartPicker = TimePickerFragment.instance(R.id.lightStartTimeButton);
                timeStartPicker.show(getSupportFragmentManager(), "start time picker");
            }
        });

        lightEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedButton = 2;
                TimePickerFragment timeEndPicker = TimePickerFragment.instance(R.id.lightEndTimeButton);
                timeEndPicker.show(getSupportFragmentManager(), "end time picker");
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        if (schedButton == 1) {
            lightOnReff.child("hour").setValue(hour);
            lightOnReff.child("minute").setValue(minute);
            schedButton = 0;
        }
        else if (schedButton == 2) {
            lightOffReff.child("hour").setValue(hour);
            lightOffReff.child("minute").setValue(minute);
            schedButton = 0;
        }
    }
}
