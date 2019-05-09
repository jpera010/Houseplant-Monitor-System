package com.jaynew.houseplantmonitor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    private TextView plantTextA;
    private TextView temperatureA;
    private TextView moistureViewA;
    private ImageView plantImageView;

    private Button testButton;

    DatabaseReference reff;
    StorageReference plantImageReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_one);

        plantTextA = (TextView)findViewById(R.id.plantTextA);                       //TextView for each data display
        temperatureA = (TextView)findViewById(R.id.temperatureViewA);
        moistureViewA = (TextView)findViewById(R.id.moistureViewA);
        plantImageView = (ImageView)findViewById(R.id.plantImageView);

        testButton = (Button)findViewById(R.id.testButton);


        String userChoice = getIntent().getStringExtra("userChoice");       //get user's choice from main activity

        reff = FirebaseDatabase.getInstance().getReference().child("plantData").child(userChoice);      //grab firebase information/data
        plantImageReff = FirebaseStorage.getInstance().getReference().child("succ.jpg");

        GlideApp.with(this /* context */)
                .load(plantImageReff)
                .into(plantImageView);

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String temperature = dataSnapshot.child("temperature").getValue().toString();
                String moisture_level = dataSnapshot.child("moisture_level").getValue().toString();

                final String light = dataSnapshot.child("light").getValue().toString();
                Log.wtf("lightTag", "light loaded");
                plantTextA.setText(name);
                temperatureA.setText(temperature);
                moistureViewA.setText(moisture_level);

                testButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (light == "1") {
                            reff.child("light").setValue("0");
                            Log.wtf("lightTag", "setting light to 0");
                        }
                        else {
                            reff.child("light").setValue("1");
                            Log.wtf("lightTag", "setting light to 1");
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
