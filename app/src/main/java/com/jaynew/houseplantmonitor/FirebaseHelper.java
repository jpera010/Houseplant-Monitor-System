package com.jaynew.houseplantmonitor;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper implements Serializable {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mreffplantData;

    private List<Plant> plantList = new ArrayList<>();


    public FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mreffplantData = mDatabase.getReference("plantData");
    }

    public void readPlantData() {
        mreffplantData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {      //occurs each time there is a change to database
                plantList.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Plant plant = keyNode.getValue(Plant.class);
                    plantList.add(plant);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("loadPost:onCancelled", databaseError.toException());  //On fail
            }
        });
    }

    public List<Plant> getPlantList() {
        return plantList;
    }

}
