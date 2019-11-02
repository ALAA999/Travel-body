package com.example.helloworld.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.helloworld.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String COARSE_LOCATION, FINE_LOCATION;
    private boolean locationPermissionGRANTED = true;
    private AlertDialog.Builder builder;
    private LocationManager lm;
    private final int REQUEST_CODE = 88;
    private MarkerOptions markerOptions;
    private LocationListener locationListener;
    private ProgressDialog pd;

    @Override
    public void onResume() {
        super.onResume();
        if (locationPermissionGRANTED) {
            if (!isLocationEnabled(this)) {
                buildAlertMessageNoGps(this);
            } else {
                boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!isGpsEnabled || !isNetworkEnabled) {
                    Toast.makeText(this, getString(R.string.change_to_high_accuracy), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    private void buildAlertMessageNoGps(final FragmentActivity activity) {
        if (builder == null) {
            builder = new AlertDialog.Builder(activity);
            builder
                    .setMessage(activity.getString(R.string.enable_location_services))
                    .setCancelable(false)
                    .setPositiveButton(activity.getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                    builder = null;
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                                }
                            })
                    .setNegativeButton(activity.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            Toast.makeText(activity, activity.getString(R.string.permissions_denied), Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            builder = null;
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void getLocationPermission() {
        String[] array = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    pd = new ProgressDialog(this);
                    pd.setMessage("Getting location...");
                    pd.show();
                    getCurrentLocation();
                } else { // if not granted we will ask user to allow these Permissions
                    locationPermissionGRANTED = false;
                    requestPermissions(array, REQUEST_CODE);
                    Log.e("state", "One of the permissions is false");
                }
            } else { // if not granted we will ask user to allow these Permissions
                locationPermissionGRANTED = false;
                requestPermissions(array, REQUEST_CODE);
                Log.e("state", "One of the permissions is false");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.btSetLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                moveCamera(latLng);
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Unable to get location.", Toast.LENGTH_SHORT).show();
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null && pd != null) {
                    pd.dismiss();
                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                    lm.removeUpdates(locationListener);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
    }

    private void moveCamera(LatLng latLng) {
        mMap.clear();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latLng.latitude, latLng.longitude)).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        markerOptions = new MarkerOptions().position(latLng).title("My location");
        mMap.addMarker(markerOptions);
        AddNoteActivity.latLng = new LatLng(latLng.latitude, latLng.longitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean permissions_granted = true;
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissions_granted = false;
                            locationPermissionGRANTED = false;
                            break;
                        }
                    }
                }
        }
        if (permissions_granted) {
        } else
            Toast.makeText(this, getString(R.string.permissions_denied), Toast.LENGTH_SHORT).show();
    }
}
