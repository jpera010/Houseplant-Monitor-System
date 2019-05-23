package com.jaynew.houseplantmonitor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settingsActivity extends AppCompatActivity {

    private Button mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;
    private Button dailyButton, weeklyButton;
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

        dailyButton = (Button)findViewById(R.id.dailyButton);
        weeklyButton = (Button)findViewById(R.id.weeklyButton);

        hourInput = (EditText)findViewById(R.id.hourInput);
        minuteInput = (EditText)findViewById(R.id.minuteInput);


        reff = FirebaseDatabase.getInstance().getReference().child("schedule");

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
            }
        });

        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.child("setting").setValue(2);
            }
        });


    }
}
