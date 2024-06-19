package com.example.cw2.views;

import static com.example.cw2.config.Configuration.LOG_TAG;
import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.cw2.AppState;

import com.example.cw2.R;
import com.example.cw2.databinding.ActivityHomeBinding;
import com.example.cw2.databinding.ActivitySettingsBinding;
import com.example.cw2.services.GeofenceService;
import com.example.cw2.services.RouteService;
import com.example.cw2.viewmodels.HomeViewModel;
import com.example.cw2.viewmodels.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Home extends AppCompatActivity {
    // ViewModel for managing home screen data
    HomeViewModel homeViewModel;
    // Service for geofencing functionality
    GeofenceService geofenceService;
    // Binder for the geofence service
    GeofenceService.LocalBinder binder;
    // Boolean to track if the service is bound
    Boolean bound = false;
    // ActivityResultLauncher for handling permission requests
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

    /**
     * Sets up the geofence service.
     */
    private void setupService(){
        // Setup service
        Intent serviceIntent = new Intent(Home.this, GeofenceService.class); // Build the intent for the service
        getApplicationContext().startForegroundService(serviceIntent);
        setBound(true);
    }

    /**
     * Accounts for the view getting killed and the user needing to click twice to remove the service.
     */
    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        outState.putBoolean("bound", getBound());
        super.onSaveInstanceState(outState);
    }

    /**
     * Retrieves the geofence service.
     * @return The GeofenceService instance.
     */
    public GeofenceService getGeofenceService() {
        return geofenceService;
    }
    /**
     * Sets the geofence service.
     * @param geofenceService The GeofenceService instance to set.
     */
    public void setGeofenceService(GeofenceService geofenceService) {
        this.geofenceService = geofenceService;
    }

    public Boolean getBound() {
        return bound;
    }

    public void setBound(Boolean bound) {
        this.bound = bound;
    }

    public void watchService(View v){
        if(getBound()){
            Intent serviceIntent = new Intent(Home.this, GeofenceService.class);
            stopService(serviceIntent);
            homeViewModel.setResource(getApplicationContext().getResources().getString(R.string.Watch_Geofences));
            setBound(false);
        } else{
            setupService();
            setBound(true);
            homeViewModel.setResource(getApplicationContext().getResources().getString(R.string.Stop_Geofences));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey("bound"))
            bound = savedInstanceState.getBoolean("bound");

        setContentView(R.layout.activity_home);

        // Set data binding
        ActivityHomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.setHomeViewModel(homeViewModel);
        binding.setLifecycleOwner(this);

        //Setup viewmodel resources.
        homeViewModel.setColorResources(((AppState) getApplication()).getColorResources());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        perm(Manifest.permission.ACCESS_BACKGROUND_LOCATION, R.string.Permission_Explain_Background, (a,b)->{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        });
        perm(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.Permission_Explain, (a,b)->{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        });
        // Setup default wording for the observing button

        if(getBound())
            homeViewModel.setResource(getApplicationContext().getResources().getString(R.string.Stop_Geofences));
        else
            homeViewModel.setResource(getApplicationContext().getResources().getString(R.string.Watch_Geofences));
    }
    public void perm(String permCheck, int message,android.content.DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(ContextCompat.checkSelfPermission(this, permCheck) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permissions for location
            builder.setMessage(message).setTitle(R.string.Permission_Title);
            builder.setPositiveButton("OK", listener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    public void onRemindersClick(View v) {
        Intent intent = new Intent(Home.this,Reminders.class);
        startActivity(intent);
    }
    public void onMovementsClick(View v) {
        Intent intent = new Intent(Home.this,Movements.class);
        startActivity(intent);
    }
    public void onSettingsClick(View v) {
        Intent intent = new Intent(Home.this,Settings.class);
        startActivity(intent);
    }
    public void onObservationsClick(View v) {
        Intent intent = new Intent(Home.this,DataOverview.class);
        startActivity(intent);
    }
}