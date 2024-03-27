package com.example.mygeolocation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * MainActivity is the entry point of the application.
 * It is responsible for requesting location permissions, starting the LocationService,
 * and updating the UI with location updates.
 */
public class MainActivity extends AppCompatActivity {

    // TextView to display location information
    private TextView locationInfoTextView;
    // BroadcastReceiver to receive location updates
    private BroadcastReceiver broadcastReceiver;

    // Request code for location permission request
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    /**
     * Called when the activity is starting.
     * This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextView
        locationInfoTextView = findViewById(R.id.locationInfoTextView);

        // Initialize the BroadcastReceiver
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract location data from the intent
                double latitude = intent.getDoubleExtra("latitude", 0);
                double longitude = intent.getDoubleExtra("longitude", 0);
                float speed = intent.getFloatExtra("speed", 0);
                float altitude = intent.getFloatExtra("altitude", 0);
                float bearing = intent.getFloatExtra("bearing", 0);
                float accuracy = intent.getFloatExtra("accuracy", 0);
                String time = intent.getStringExtra("time");
                String provider = intent.getStringExtra("provider");

                // Format the location data and set it to the TextView
                String locationInfo = "Latitude: " + latitude +
                        "\nLongitude: " + longitude +
                        "\nSpeed: " + speed +
                        "\nAltitude: " + altitude +
                        "\nBearing: " + bearing +
                        "\nAccuracy: " + accuracy +
                        "\nTime: " + time +
                        "\nProvider: " + provider;
                locationInfoTextView.setText(locationInfo);
            }
        };

        // Check if location permission is already granted
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted, start the LocationService
            startService(new Intent(this, LocationService.class));
        } else {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Called when the user responds to the permission request.
     *
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, start the LocationService
                    startService(new Intent(this, LocationService.class));
                } else {
                    // Permission was denied. Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // Handle other permission requests if any
        }
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(),
     * for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices (such as the camera), etc.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver to receive location updates
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
     * The counterpart to onResume().
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver when the activity is paused
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Perform any final cleanup before an activity is destroyed.
     * This can happen either because the activity is finishing (someone called finish() on it),
     * or because the system is temporarily destroying this instance of the activity to save space.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the LocationService when the activity is destroyed
        stopService(new Intent(this, LocationService.class));
    }
}