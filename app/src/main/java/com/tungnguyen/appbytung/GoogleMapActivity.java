package com.tungnguyen.appbytung;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final Location defaultLocation = new Location(LocationManager.GPS_PROVIDER);
    private GoogleMap gMap;
    private Geocoder geocoder;
    private boolean locationPermissionGranted = false;
    private Location lastKnownLocation = null;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.google_map_activity);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        defaultLocation.setLatitude(47.6060531);
        defaultLocation.setLongitude(-122.3321);

        // Functions to handle requests for getting locations
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d("LOCATION", location.toString());
                        showLocationAddress(location);
                        updateMap(location);
                    }
                }
            }
        };

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Initialize Google Map
        gMap = googleMap;
        geocoder = new Geocoder(this, Locale.getDefault());

        // Check|Obtain user location permission
        checkLocationPermission();

        // Get user location
        getDeviceLocation();

        // Load Traffic cam data -> Display marker
        Camera.loadCameraData(this, new Camera.VolleyResponseListener() {
            @Override
            public void onError(String errorMessage) {
                Log.d("JSON", "Error: " + errorMessage);
            }

            @Override
            public void onResponse(List<Camera> cameraList) {
                showCameraMarkers(cameraList);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void checkLocationPermission() {
        Log.d("LOCATION", "checkPermission");
        // Check if the Location permission has been granted
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION", "already granted");
            // Permission is already available. Get user's location
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    Log.d("LOCATION", location.toString());
                    showLocationAddress(location);
                    updateMap(location);
                } else {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            });
        } else {
            // Permission is missing and must be requested.
            Log.d("LOCATION", "should request");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint({"MissingPermission", "MissingSuperCall"})
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Log.d("LOCATION", "granted");
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                    locationPermissionGranted = true;
                    if (location != null) {
                        Log.d("LOCATION", location.toString());
                        updateMap(location);
                    } else {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                });
            }
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        showLocationAddress(lastKnownLocation);
                        updateMap(lastKnownLocation);
                    } else {
                        Log.d("TAG", "Current location is null. Using defaults.");
                        Log.e("TAG", "Exception: %s", task.getException());
                        showLocationAddress(defaultLocation);
                        updateMap(defaultLocation);
                        gMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void showLocationAddress(Location location) {
        if (location != null) {
            try {
                List<Address> currentAddress = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1
                );

                Toast.makeText(this, currentAddress.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
            } catch(IOException error) {
                Log.e("Exception: %s", error.getMessage(), error);
            }
        }
    }

    private void updateMap(Location location) {
        Log.d("LOCATION", "Updating map!");
        if (location != null) {
            Log.d("LOCATION", "Moving map!");
            try {

                gMap.setMinZoomPreference(12f);
                gMap.addMarker( new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .title("My Location")
                                .snippet(geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1
                                ).get(0).getAddressLine(0))
                );
                gMap.moveCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(location.getLatitude(), location.getLongitude()))
                );
            } catch(IOException error) {
                Log.e("Exception: %s", error.getMessage(), error);
            }
        }
    }

    private void showCameraMarkers(@NonNull List<Camera> cameraList) {
        Log.d("LOCATION", "Show camera markers");
        for (Camera camera : cameraList) {
            gMap.addMarker( new MarkerOptions()
                    .position(new LatLng(camera.getLatitude(), camera.getLongitude()))
                    .title(camera.getDescription())
            );
        }
    }

}
