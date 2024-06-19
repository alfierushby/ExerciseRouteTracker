package com.example.cw2.views;

import static com.example.cw2.config.Configuration.LOG_TAG;
import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

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
import com.example.cw2.R;
import com.example.cw2.databinding.ActivityReminderCreatorBinding;
import com.example.cw2.services.GeofenceService;
import com.example.cw2.viewmodels.ReminderCreatorViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;

@AndroidEntryPoint
public class ReminderCreator extends AppCompatActivity implements OnMapReadyCallback {
    ReminderCreatorViewModel reminderCreatorViewModel;
    FusedLocationProviderClient fusedLocationClient;
    CancellationTokenSource token = new CancellationTokenSource();
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_creator);

        // Set data binding
        ActivityReminderCreatorBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder_creator);
        reminderCreatorViewModel = new ViewModelProvider(this).get(ReminderCreatorViewModel.class);
        binding.setReminderCreatorViewModel(reminderCreatorViewModel);
        binding.setLifecycleOwner(this);

        reminderCreatorViewModel.setColorResources(((AppState) getApplication()).getColorResources());

        // Add and create the map fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView2, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        // Use Fused for location stuff
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    /**
     * Get the ReminderCreatorViewModel instance.
     * @return The ReminderCreatorViewModel instance.
     */
    public ReminderCreatorViewModel getReminderCreatorViewModel() {
        return reminderCreatorViewModel;
    }

    /**
     * Get the FusedLocationProviderClient instance.
     * @return The FusedLocationProviderClient instance.
     */
    public FusedLocationProviderClient getFusedLocationClient() {
        return fusedLocationClient;
    }

    /**
     * Get the CancellationTokenSource instance. For use with fused location.
     * @return The CancellationTokenSource instance.
     */
    public CancellationTokenSource getToken() {
        return token;
    }

    /**
     * Get the GoogleMap instance.
     * @return The GoogleMap instance.
     */
    public GoogleMap getMap() {
        return map;
    }

    /**
     * Set the GoogleMap instance.
     * @param map The GoogleMap instance to set.
     */
    public void setMap(GoogleMap map) {
        this.map = map;
    }

    /**
     * Finish the activity and save the reminder.
     * @param v The view triggering the method.
     */
    public void finish(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Explain that functionality isn't working.
            builder.setMessage(R.string.Permission_Explain).setTitle(R.string.Permission_Title);
            builder.setPositiveButton("OK", null);
            AlertDialog alertDialog  = builder.create();
            alertDialog.show();
        } else{
            getFusedLocationClient().getCurrentLocation(PRIORITY_HIGH_ACCURACY, getToken().getToken()).addOnSuccessListener((location) -> {
                getReminderCreatorViewModel().setReminderLatitude(location.getLatitude());
                getReminderCreatorViewModel().setReminderLongitude(location.getLongitude());
                getReminderCreatorViewModel().recordReminder();
            });
            finish();
        }
    }

    public void cancel(View v){
        finish();
    }

    /**
     * Draw a circle on the map at the reminder location.
     * @param point  The center of the circle.
     * @param radius The radius of the circle.
     */
    private void drawCircle(LatLng point, int radius){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        // Radius of the circle
        circleOptions.radius(radius);
        // Border color of the circle
        circleOptions.strokeColor(Color.BLUE);
        // Fill color of the circle
        circleOptions.fillColor(Color.CYAN);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        getMap().addCircle(circleOptions);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Check permissions first with location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Ask for permissions for location
            builder.setMessage(R.string.Permission_Explain).setTitle(R.string.Permission_Title);
            builder.setPositiveButton("OK", null);
            AlertDialog alertDialog  = builder.create();
            alertDialog.show();
        }

        // Set google maps to track the user's location
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        // Make it zoom into the last known location of the user
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),16.0f));
                        }
                    }
                });

        // Set the global map for ease of use
        setMap(googleMap);

        // Draw a circle if editing. Not editing entails no circle.
        if(Boolean.TRUE.equals(getReminderCreatorViewModel().getStateRepo().getEditing().getValue()))
            drawCircle(getReminderCreatorViewModel().getStateRepo().getReminderPoint(), Integer.parseInt(Objects.requireNonNull(getReminderCreatorViewModel().getReminderRadius().getValue())));

    }
}