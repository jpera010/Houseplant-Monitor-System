package com.jaynew.houseplantmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlantOneFragment extends Fragment {

    View myView;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_plant_one, container, false);
        userChoice = getArguments().getString("userChoice");
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plantTextA = (TextView)myView.findViewById(R.id.plantTextA);                       //TextView for each data display
        temperatureA = (TextView)myView.findViewById(R.id.temperatureViewA);
        moistureViewA = (TextView)myView.findViewById(R.id.moistureViewA);
        plantImageView = (ImageView)myView.findViewById(R.id.plantImageView);

        waterButton = (Button)myView.findViewById(R.id.waterButton);
        lightSwitch = (Switch)myView.findViewById(R.id.lightSwitch);


        plantDataReff = FirebaseDatabase.getInstance().getReference().child("plantData");      //grab firebase information/data
        toolsReff = FirebaseDatabase.getInstance().getReference().child("tools");
        //plantImageReff = FirebaseStorage.getInstance().getReference().child("succ.jpg");
        //base64image = FirebaseDatabase.getInstance().getReference().child("images").child("image");

        //String img = base64image.toString();
        //Bitmap bmp = BitmapFactory.decodeByteArray(img, )

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(myView.getContext(), waterSettingsActivity.class);
                startActivity(startIntent);
            }
        });

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
                            toolsReff.child("light").setValue(true);
                            Toast.makeText(myView.getContext(), "Light on", Toast.LENGTH_SHORT).show();
                            Log.wtf("lightTag", "setting light to true");
                        }
                        else if (b == false){
                            lightSwitch.setChecked(false);
                            toolsReff.child("light").setValue(false);
                            Toast.makeText(myView.getContext(), "Light off", Toast.LENGTH_SHORT).show();
                            Log.wtf("lightTag", "setting light to false");
                        }
                    }
                });

                waterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toolsReff.child("water_pump").setValue(true);
                        Toast.makeText(myView.getContext(), "Watering plant...", Toast.LENGTH_SHORT).show();
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
                temperatureA.setText(temperature);
                moistureViewA.setText(moisture_level);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
