package com.jaynew.houseplantmonitor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlantTwoActivity extends AppCompatActivity {

    private TextView plantTextB;
    private TextView temperatureB;
    private TextView moistureViewB;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_two);

        plantTextB = (TextView)findViewById(R.id.plantTextB);
        temperatureB = (TextView)findViewById(R.id.temperatureViewB);
        moistureViewB = (TextView)findViewById(R.id.moistureViewB);

        reff = FirebaseDatabase.getInstance().getReference().child("plantData").child("2");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String temperature = dataSnapshot.child("temperature").getValue().toString();
                String moisture_level = dataSnapshot.child("moisture_level").getValue().toString();

                plantTextB.setText(name);
                temperatureB.setText(temperature);
                moistureViewB.setText(moisture_level);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
