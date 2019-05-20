package com.jaynew.houseplantmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class settingsActivity extends AppCompatActivity {

    private Button mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;
    private DatabaseReference reff;
    private EditText hourInput, minuteInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mondayButton = (Button)findViewById(R.id.mondayButton);
        tuesdayButton = (Button)findViewById(R.id.tuesdayButton);
        wednesdayButton = (Button)findViewById(R.id.wednesdayButton);
        thursdayButton = (Button)findViewById(R.id.thursdayButton);
        fridayButton = (Button)findViewById(R.id.fridayButton);
        saturdayButton = (Button)findViewById(R.id.saturdayButton);
        sundayButton = (Button)findViewById(R.id.sundayButton);

        hourInput = (EditText)findViewById(R.id.hourInput);
        minuteInput = (EditText)findViewById(R.id.minuteInput);

        reff = FirebaseDatabase.getInstance().getReference().child("schedule");

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


    }
}
