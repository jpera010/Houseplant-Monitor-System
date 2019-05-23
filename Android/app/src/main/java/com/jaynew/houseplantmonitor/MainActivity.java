package com.jaynew.houseplantmonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    //FirebaseHelper mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button plant_one_activity = (Button)findViewById(R.id.plantButtonOne);
        Button plant_two_activity = (Button)findViewById(R.id.plantButtonTwo);

        plant_one_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), PlantDataActivity.class);
                startIntent.putExtra("userChoice", "1");                                //menu select 1st plant
                startActivity(startIntent);
            }
        });

        plant_two_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), PlantDataActivity.class);
                startIntent.putExtra("userChoice", "2");                                //menu select 2nd plant
                startActivity(startIntent);
            }
        });
    }



}
