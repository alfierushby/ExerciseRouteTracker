package com.example.cw2.services;

import static com.example.cw2.config.Configuration.LOG_TAG;
import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.cw2.Events;
import com.example.cw2.R;
import com.example.cw2.repos.StateRepo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;

import org.greenrobot.eventbus.EventBus;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;

public class RouteService extends Service {

    private final IBinder binder = new RouteService.LocalBinder();
    FusedLocationProviderClient fusedLocationClient;
    Boolean tracking = false, foreground =false;
    RepoEntryPoint entryPoint;

    /**
     * Important for the individual elements to communicate with the repo, without referencing
     * any Model specifically.
     */
    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface RepoEntryPoint {
        /**
         * Retrieves the StateRepo from the repository entry point.
         * @return The StateRepo instance.
         */
        public StateRepo getStateRepo();
    }


    /**
     * This is usefully called when you want to set the playback speed elsewhere without bind.
     * Set the intent float field "playbackSpeed" to do this.
     *
     * @param intent  The Intent passed to the service.
     * @param flags   Additional data about this start request.
     * @param startID A unique integer representing this specific request to start.
     * @return START_STICKY to indicate that if the process is killed, the system should try to
     * restart the service.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        // Use Fused for location stuff
        setFusedLocationClient(LocationServices.getFusedLocationProviderClient(this));
        this.entryPoint = EntryPointAccessors.fromApplication(getApplicationContext(), RepoEntryPoint.class);
        startProcess();
        return START_STICKY;
    }

    /**
     * Retrieves the repository entry point.
     * @return The repository entry point.
     */
    public RepoEntryPoint getEntryPoint() {
        return entryPoint;
    }
    /**
     * Sets the FusedLocationProviderClient used for location services.
     * @param fusedLocationClient The FusedLocationProviderClient instance.
     */
    public void setFusedLocationClient(FusedLocationProviderClient fusedLocationClient) {
        this.fusedLocationClient = fusedLocationClient;
    }
    /**
     * Retrieves the FusedLocationProviderClient used for location services.
     * @return The FusedLocationProviderClient instance.
     */
    public FusedLocationProviderClient getFusedLocationClient() {
        return fusedLocationClient;
    }

    /**
     * Sets the tracking (whether it creates a trail) status.
     * @param tracking Boolean indicating whether tracking is active.
     */
    public void setTracking(Boolean tracking) {
        this.tracking = tracking;
    }
    /**
     * Retrieves the foreground status.
     * @return True if running in the foreground, false otherwise.
     */
    public Boolean getForeground() {
        return foreground;
    }
    /**
     * Sets the foreground status.
     * @param foreground Boolean indicating whether the service is in the foreground.
     */
    public void setForeground(Boolean foreground) {
        this.foreground = foreground;
    }
    /**
     * Starts the foreground service, displaying a notification.
     * Checks for necessary permissions before starting the service.
     */
    private void startProcess(){
        if(getForeground())
            return;
        setForeground(true);

        int foreground = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE);
        if (foreground == PackageManager.PERMISSION_DENIED) {
            Log.d(LOG_TAG, "Permission denied for service!");
            stopSelf();
        }
        try {
            NotificationChannel chan = new NotificationChannel("c1", "Router", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "c1");
            Notification notification = notificationBuilder
                    .setContentTitle("Getting your location!")
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .build();

            startForeground(1, notification);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Unable to run foreground service!");
        }
    }


    /**
     * Starts tracking the location in a separate thread.
     * Checks for necessary permissions before starting the tracking.
     */

    public void startTracking() {

        setTracking(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Explain that functionality isn't working.
            builder.setMessage(R.string.Permission_Explain).setTitle(R.string.Permission_Title);
            builder.setPositiveButton("OK", null);
            AlertDialog alertDialog  = builder.create();
            alertDialog.show();
            return;
        }

        CancellationTokenSource token = new CancellationTokenSource();

        new Thread(() -> {
            try {
                while (tracking) {
                    Thread.sleep(1000);
                    getFusedLocationClient().getCurrentLocation(PRIORITY_HIGH_ACCURACY,token.getToken()).addOnSuccessListener ((location) ->{
                        LatLng p = new LatLng(location.getLatitude(),location.getLongitude());
                        Log.d(LOG_TAG,"Adding " + location.getLatitude());
                        getEntryPoint().getStateRepo().insertMovementPoint(p);
                        Events.NewPoint event = new Events.NewPoint();
                        event.points = getEntryPoint().getStateRepo().getMovementPoints();
                        EventBus.getDefault().post(event);
                    });

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    /**
     * Stops tracking the location.
     */
    public void stopTracking(){
        setTracking(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public RouteService getService() {
            return RouteService.this;
        }
    }

}
