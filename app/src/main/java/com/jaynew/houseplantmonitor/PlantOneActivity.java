package com.jaynew.houseplantmonitor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class PlantOneActivity extends AppCompatActivity {

    private TextView plantTextOne;
    private TextView temperatureView;
    private TextView moistureView;
    private Button showButton;
    //private String key;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_one);

        plantTextOne = (TextView)findViewById(R.id.plantTextOne);
        temperatureView = (TextView)findViewById(R.id.temperatureView);
        moistureView = (TextView)findViewById(R.id.moistureView);
        showButton = (Button)findViewById(R.id.showButton);

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff = FirebaseDatabase.getInstance().getReference().child("plantData").child("1");
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String temperature = dataSnapshot.child("temperature").getValue().toString();
                        String moisture_level = dataSnapshot.child("moisture_level").getValue().toString();

                        plantTextOne.setText(name);
                        temperatureView.setText(temperature);
                        moistureView.setText(moisture_level);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }




}
