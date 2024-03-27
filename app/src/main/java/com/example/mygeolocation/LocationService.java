package com.example.mygeolocation;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * LocationService is a service that listens for location updates and broadcasts them.
 * It implements the LocationListener interface to receive updates from the LocationManager.
 */
public class LocationService extends Service implements LocationListener {

    // LocationManager provides access to the system location services
    private LocationManager locationManager;

    /**
     * This method is called when the service is bound. It returns null because the service is started and not bound.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return null because it's an unbound service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This method is called when the service is created. It initializes the LocationManager and requests location updates.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Get the LocationManager instance
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            // Request location updates from the GPS provider with minimum time interval and minimum distance between updates set to 0
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the location has changed. It gets the location data and broadcasts it.
     *
     * @param location The new location.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Here we get the location data
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float speed = location.getSpeed();
        float altitude = (float) location.getAltitude();
        float bearing = location.getBearing();
        float accuracy = location.getAccuracy();
        // The time when the location was updated, in hh:mm:ss format
        String time = java.text.DateFormat.getTimeInstance().format(location.getTime());
        String provider = location.getProvider();

        // Here we send this data back to MainActivity
        Intent intent = new Intent("location_update");
        // Put the location data into the intent
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("speed", speed);
        intent.putExtra("altitude", altitude);
        intent.putExtra("bearing", bearing);
        intent.putExtra("accuracy", accuracy);
        intent.putExtra("time", time);
        intent.putExtra("provider", provider);
        sendBroadcast(intent);
    }

    /**
     * This method is called when the service is destroyed. It removes location updates from the LocationManager.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            try {
                // Remove location updates
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    // These methods need to be implemented as part of the LocationListener interface, but we don't need to use them
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
