package com.jaynew.houseplantmonitor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavigationMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NotificationManager notificationManager;
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";
    boolean locked, breachAlert, plantDryAlert, unsafeTempAlert;
    private ImageButton lockButton;
    DatabaseReference securityReff, alertsReff;

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");

            NotificationChannel channel3 = new NotificationChannel(CHANNEL_3_ID, "Channel 3", NotificationManager.IMPORTANCE_LOW);
            channel3.setDescription("This is Channel 3");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }
    }

    public void sendOnChannel1() {
        String title = "Warning";
        String message = "System breach detected!";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.breakin)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2() {
        String title = "Alert";
        String message = "Plant needs watering! Automatically watering...";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.water_hose)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }

    public void sendOnChannel3() {
        String title = "Caution";
        String message = "Unsafe temperature for plants!";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setSmallIcon(R.drawable.error_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(3, notification);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        createNotificationChannels();
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        lockButton = (ImageButton)findViewById(R.id.lockButton);

        securityReff = FirebaseDatabase.getInstance().getReference().child("security");
        alertsReff = FirebaseDatabase.getInstance().getReference().child("alerts");

        /////PUSH NOTIFICATIONS
        alertsReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                breachAlert = (boolean)dataSnapshot.child("breakIn").getValue();
                plantDryAlert = (boolean)dataSnapshot.child("plantDry").getValue();
                unsafeTempAlert = (boolean)dataSnapshot.child("unsafeTemp").getValue();

                if (breachAlert == true) {
                    sendOnChannel1();
                    alertsReff.child("breakIn").setValue(false);
                }

                if (plantDryAlert == true) {
                    sendOnChannel2();
                    alertsReff.child("plantDry").setValue(false);
                }

                if (unsafeTempAlert == true) {
                    sendOnChannel3();
                    alertsReff.child("unsafeTemp").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /////LOCK BUTTON
        securityReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("lockSet").getValue().toString() == "true") {
                    locked = true;
                    lockButton.setImageResource(R.drawable.big_padlock);
                }
                else {
                    locked = false;
                    lockButton.setImageResource(R.drawable.unlock_big);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locked == true) {
                    securityReff.child("lockSet").setValue(false);
                    Toast.makeText(getBaseContext(), "System unlocked!", Toast.LENGTH_SHORT).show();
                }
                else {
                    securityReff.child("lockSet").setValue(true);
                    Toast.makeText(getBaseContext(), "System locked!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_plant_one) {
            Intent startIntent = new Intent(getApplicationContext(), PlantDataActivity.class);
            startIntent.putExtra("userChoice", "1");                                //menu select 1st plant
            startActivity(startIntent);
        } else if (id == R.id.nav_plant_two) {
            Intent startIntent = new Intent(getApplicationContext(), PlantDataActivity.class);
            startIntent.putExtra("userChoice", "2");                                //menu select 1st plant
            startActivity(startIntent);
        } else if (id == R.id.nav_settings) {
            Intent startIntent = new Intent(getApplicationContext(), waterSettingsActivity.class);
            startActivity(startIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
