package com.jaynew.houseplantmonitor;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PlantDataActivity extends AppCompatActivity {

    String userChoice;

    private TextView plantTextA;
    private TextView temperatureA;
    private TextView moistureViewA;
    private ImageView plantImageView;

    private ImageButton waterButton;
    private Switch lightSwitch;

    DatabaseReference toolsReff;
    DatabaseReference plantDataReff;
    DatabaseReference base64image;
    StorageReference plantImageReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_one);

        plantTextA = (TextView)findViewById(R.id.plantTextA);                       //TextView for each data display
        temperatureA = (TextView)findViewById(R.id.temperatureViewA);
        moistureViewA = (TextView)findViewById(R.id.moistureViewA);
        plantImageView = (ImageView)findViewById(R.id.plantImageView);

        waterButton = (ImageButton)findViewById(R.id.waterButton);
        lightSwitch = (Switch)findViewById(R.id.lightSwitch);

        userChoice = getIntent().getStringExtra("userChoice");       //get user's choice from main activity

        plantDataReff = FirebaseDatabase.getInstance().getReference().child("plantData");      //grab firebase information/data
        toolsReff = FirebaseDatabase.getInstance().getReference().child("care");
        plantImageReff = FirebaseStorage.getInstance().getReference().child("succ.jpg");

        if (Integer.parseInt(userChoice) == 1) {
            plantDataReff.child("currentPlant").setValue(1);
        }
        else if (Integer.parseInt(userChoice) == 2) {
            plantDataReff.child("currentPlant").setValue(2);
        }

        GlideApp.with(this /* context */)
                .load(plantImageReff)
                .into(plantImageView);

        toolsReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //final boolean light = dataSnapshot.child("light").getValue();
                Log.wtf("lightTag", "light loaded");

                lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b == true) {
                            lightSwitch.setChecked(true);
                            toolsReff.child("lightSet").setValue(true);
                            Toast.makeText(getBaseContext(), "Light on", Toast.LENGTH_SHORT).show();
                            Log.wtf("lightTag", "setting light to true");
                        }
                        else if (b == false){
                            lightSwitch.setChecked(false);
                            toolsReff.child("lightSet").setValue(false);
                            Toast.makeText(getBaseContext(), "Light off", Toast.LENGTH_SHORT).show();
                            Log.wtf("lightTag", "setting light to false");
                        }
                    }
                });

                waterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toolsReff.child("waterPumpSet").setValue(true);
                        Toast.makeText(getBaseContext(), "Watering plant...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        plantDataReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child(userChoice).child("name").getValue().toString();
                String temperature = dataSnapshot.child("temperature").getValue().toString();
                String moisture_level = dataSnapshot.child(userChoice).child("moisture_level").getValue().toString();

                plantTextA.setText(name);
                temperatureA.setText(temperature + (char) 0x00B0);
                moistureViewA.setText(moisture_level + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
