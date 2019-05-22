package com.jaynew.houseplantmonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Console;
import java.io.InputStream;

public class PlantDataActivity extends AppCompatActivity {

    String userChoice;

    private TextView plantTextA;
    private TextView temperatureA;
    private TextView moistureViewA;
    private ImageView plantImageView;

    private Button settingsButton;
    private Button waterButton;
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

        waterButton = (Button)findViewById(R.id.waterButton);
        lightSwitch = (Switch)findViewById(R.id.lightSwitch);
        settingsButton = (Button)findViewById(R.id.settingsButton);

        userChoice = getIntent().getStringExtra("userChoice");       //get user's choice from main activity

        plantDataReff = FirebaseDatabase.getInstance().getReference().child("plantData");      //grab firebase information/data
        toolsReff = FirebaseDatabase.getInstance().getReference().child("tools");
        plantImageReff = FirebaseStorage.getInstance().getReference().child("succ.jpg");
        //base64image = FirebaseDatabase.getInstance().getReference().child("images").child("image");

        //String img = base64image.toString();
        //Bitmap bmp = BitmapFactory.decodeByteArray(img, )
        GlideApp.with(this /* context */)
                .load(plantImageReff)
                .into(plantImageView);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), settingsActivity.class);
                startActivity(startIntent);
            }
        });

        toolsReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int light = Integer.parseInt(dataSnapshot.child("light").getValue().toString());
                Log.wtf("lightTag", "light loaded");

                lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b == true) {
                            lightSwitch.setChecked(true);
                            toolsReff.child("light").setValue(1);
                            Toast.makeText(getBaseContext(), "Light on", Toast.LENGTH_SHORT).show();
                            Log.wtf("lightTag", "setting light to 1");
                        }
                        else if (b == false){
                            lightSwitch.setChecked(false);
                            toolsReff.child("light").setValue(0);
                            Toast.makeText(getBaseContext(), "Light off", Toast.LENGTH_SHORT).show();
                            Log.wtf("lightTag", "setting light to 0");
                        }
                    }
                });

                waterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toolsReff.child("water_pump").setValue(1);
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
                String temperature = dataSnapshot.child(userChoice).child("temperature").getValue().toString();
                String moisture_level = dataSnapshot.child(userChoice).child("moisture_level").getValue().toString();

                plantTextA.setText(name);
                temperatureA.setText(temperature);
                moistureViewA.setText(moisture_level);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
