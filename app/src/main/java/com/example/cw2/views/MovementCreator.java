package com.example.cw2.views;

import static com.example.cw2.config.Configuration.LOG_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.cw2.AppState;
import com.example.cw2.Events;
import com.example.cw2.R;
import com.example.cw2.services.RouteService;
import com.example.cw2.databinding.ActivityMovementCreatorBinding;
import com.example.cw2.viewmodels.MovementCreatorViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovementCreator extends AppCompatActivity implements OnMapReadyCallback {

    MovementCreatorViewModel movementCreatorViewModel;
    FusedLocationProviderClient fusedLocationClient;
    RouteService routeService;
    Boolean bound = false;

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_creator);

        // Set data binding
        ActivityMovementCreatorBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_movement_creator);
        movementCreatorViewModel = new ViewModelProvider(this).get(MovementCreatorViewModel.class);
        binding.setMovementCreatorViewModel(movementCreatorViewModel);
        binding.setLifecycleOwner(this);

        //Setup viewmodel resources.
        movementCreatorViewModel.setColorResources(((AppState) getApplication()).getColorResources());

        // Initialize the map fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);


        // Use FusedLocationProviderClient for location-related tasks
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Setup the service for recording
        setupService();
    }

    /**
     * Initializes and starts the RouteService for recording movement.
     */
    private void setupService() {
        // Setup service
        Intent serviceIntent = new Intent(MovementCreator.this, RouteService.class); // Build the intent for the service
        getApplicationContext().startForegroundService(serviceIntent);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        setBound(true);
    }

    /**
     * Handles the connection to the RouteService.
     */
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binded) {
            Log.d(LOG_TAG, "I am binded!");
            RouteService.LocalBinder binder = (RouteService.LocalBinder) binded;
            setRouteService(binder.getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            setBound(false);
        }
    };

    /**
     * Gets the binding state of the service.
     * @return The binding state.
     */
    public Boolean getBound() {
        return bound;
    }

    /**
     * Sets the binding state of the service.
     * @param bound The new binding state.
     */
    public void setBound(Boolean bound) {
        this.bound = bound;
    }

    /**
     * Gets the instance of RouteService.
     * @return The RouteService instance.
     */
    public RouteService getRouteService() {
        return routeService;
    }

    /**
     * Sets the instance of RouteService.
     * @param routeService The RouteService instance.
     */
    public void setRouteService(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * Gets the GoogleMap instance. Used for drawing.
     * @return The GoogleMap instance.
     */
    public GoogleMap getMap() {
        return map;
    }

    /**
     * Sets the GoogleMap instance.
     * @param map The GoogleMap instance.
     */
    public void setMap(GoogleMap map) {
        this.map = map;
    }

    /**
     * Gets the MovementCreatorViewModel instance.
     * @return The MovementCreatorViewModel instance.
     */
    public MovementCreatorViewModel getMovementCreatorViewModel() {
        return movementCreatorViewModel;
    }


    /**
     * Finishes the activity, stops tracking, and releases resources.
     * @param v The view triggering the method.
     */
    public void finish(View v) {
        getMovementCreatorViewModel().recordMovement();
        getRouteService().stopTracking();
        Intent serviceIntent = new Intent(MovementCreator.this, RouteService.class);
        unbindService(connection);
        stopService(serviceIntent);
        setBound(false);
        finish();
    }

    /**
     * Starts recording the location of the user and drawing it on the map.
     * @param v The view triggering the method.
     */
    public void startRecording(View v) {
        getRouteService().startTracking();
    }

    /**
     * Stops recording the location of the user.
     * @param v The view triggering the method.
     */
    public void stopRecording(View v) {
        getRouteService().stopTracking();
    }

    /**
     * Registers the event bus on activity start.
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Unregisters the event bus on activity stop.
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Receives new points from the event bus and updates the map accordingly.
     * @param event The event containing new points.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPoints(Events.NewPoint event) {
        Log.d(LOG_TAG, "Adding points");
        PolylineOptions lineOptions = new PolylineOptions();

        for (LatLng location : event.points) {
            lineOptions.add(location);
        }

        lineOptions.width(13);
        lineOptions.color(Color.BLUE);

        getMap().addPolyline(lineOptions);
    }
    /**
     * Handles the creation of the map and sets up initial configurations.
     * @param googleMap The GoogleMap instance.
     */
    /**
     * Handles the creation of the map and sets up initial configurations.
     *
     * @param googleMap The GoogleMap instance.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showLocationPermissionDialog();
        }

        // Enable user's location on the map
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Move the camera to the last known location
        moveCameraToLastKnownLocation(googleMap);

        setMap(googleMap);

        // Draw existing movement points on the map
        if (getMovementCreatorViewModel().getStateRepo().getMovementPoints().size() > 0) {
            Events.NewPoint event = new Events.NewPoint();
            event.points = getMovementCreatorViewModel().getStateRepo().getMovementPoints();
            setPoints(event);
        }
    }

    /**
     * Shows a dialog explaining the need for location permissions.
     */
    private void showLocationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.Permission_Explain).setTitle(R.string.Permission_Title);
        builder.setPositiveButton("OK", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Moves the camera to the last known location of the device.
     * @param googleMap The GoogleMap instance.
     */
    private void moveCameraToLastKnownLocation(GoogleMap googleMap) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showLocationPermissionDialog();
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));
                        }
                    }
                });
    }
}