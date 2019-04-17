package com.jaynew.houseplantmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    //FirebaseHelper mFirebase = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button plant_one_activity = (Button)findViewById(R.id.plantButtonOne);
        Button plant_two_activity = (Button)findViewById(R.id.plantButtonTwo);

        plant_one_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), PlantOneActivity.class);
                startActivity(startIntent);
            }
        });

        plant_two_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), PlantTwoActivity.class);
                startActivity(startIntent);
            }
        });
    }



}
