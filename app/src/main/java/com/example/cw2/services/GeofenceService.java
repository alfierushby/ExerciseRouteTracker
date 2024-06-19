package com.example.cw2.services;

import static com.example.cw2.config.Configuration.GEOFENCE_EXPIRATION;
import static com.example.cw2.config.Configuration.LOG_TAG;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import androidx.lifecycle.LiveData;

import com.example.cw2.R;
import com.example.cw2.geofencing.GeoReceiver;
import com.example.cw2.models.ReminderModel;
import com.example.cw2.repos.ReminderRepo;
import com.example.cw2.repos.StateRepo;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;

/**
 * A service that contains the geofence functionality.
 * Functionality inspired from <a href="https://developer.android.com/develop/sensors-and-location/location/geofencing#create-geofence-objects">Link</a>.
 */
@Singleton
public class GeofenceService extends Service {
    private final IBinder binder = new GeofenceService.LocalBinder();
    private GeofencingClient geofencingClient;
    private List<Geofence> geoFences;
    LiveData<List<ReminderModel>> models;

    private PendingIntent geoPendingIntent;
    private boolean foreground = false;
    RepoEntryPoint entryPoint;

    /**
     * Important for the individual elements to communicate with the repo, without referencing
     * any Model specifically.
     */
    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface RepoEntryPoint {
         StateRepo getStateRepo();
         ReminderRepo getReminderRepo();
    }


    /**
     * This is usefully called when you want to set the playback speed elsewhere without bind.
     * Set the intent float field "playbackSpeed" to do this.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        geofencingClient = LocationServices.getGeofencingClient(this);
        this.entryPoint = EntryPointAccessors.fromApplication(getApplicationContext(), RepoEntryPoint.class);
        if(getModels() == null)
            models = getEntryPoint().getReminderRepo().getAllLive();
        // Note that this will be a foreground service, so observing forever is expected.
        if (!getModels().hasObservers()){
            getModels().observeForever(this::addGeofences);
        }


        startProcess();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        getModels().removeObserver(this::addGeofences);
        geoFences = null;
        setForegroundBool(false);
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(getMainExecutor(), (e)->{
                    Log.d(LOG_TAG,"Removed geofences " + getModels().hasObservers());
                });
    }

    /**
     * This adds the GeoFences currently defined into the client, and so allows them to observe the
     * Reminders. Call this function when the reminders need geofencing enabled.
     */
    public void startObserving() {
        if(getGeoFences().size()>0){
            // Add geofence
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Ask for permissions for location
                builder.setMessage(R.string.Permission_Explain).setTitle(R.string.Permission_Title);
                builder.setPositiveButton("OK", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return;
            }
            getGeofencingClient().addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnSuccessListener(
                            getApplication().getMainExecutor(), (a) -> {
                Log.d(LOG_TAG, "Added geofence successfully");
            });
            }
    }
    /**
     * Retrieves the repository entry point.
     * @return The repository entry point.
     */
    public RepoEntryPoint getEntryPoint() {
        return entryPoint;
    }

    /**
     * Retrieves the LiveData containing a list of ReminderModel objects.
     * @return LiveData containing a list of ReminderModel objects.
     */
    public LiveData<List<ReminderModel>> getModels() {
        return models;
    }
    /**
     * Retrieves the GeofencingClient used for managing geofences.
     * @return The GeofencingClient.
     */
    public GeofencingClient getGeofencingClient() {
        return geofencingClient;
    }
    /**
     * Checks whether the service is running in the foreground.
     * @return True if running in the foreground, false otherwise.
     */
    public boolean isForeground() {
        return foreground;
    }
    /**
     * Sets the foreground status of the service.
     * @param foreground Boolean indicating whether the service is in the foreground.
     */
    public void setForegroundBool(boolean foreground) {
        this.foreground = foreground;
    }
    /**
     * Retrieves the list of geofences.
     * @return List of geofences.
     */
    public List<Geofence> getGeoFences() {
        return geoFences;
    }
    /**
     * Sets the list of geofences.
     * @param geoFences The list of geofences to be set.
     */
    public void setGeoFences(List<Geofence> geoFences) {
        this.geoFences = geoFences;
    }
    /**
     * Retrieves the PendingIntent for geofence events.
     * @return The PendingIntent for geofence events.
     */
    public PendingIntent getGeoPendingIntent() {
        return geoPendingIntent;
    }
    /**
     * Sets the PendingIntent for geofence events.
     * @param geoPendingIntent The PendingIntent for geofence events.
     */
    public void setGeoPendingIntent(PendingIntent geoPendingIntent) {
        this.geoPendingIntent = geoPendingIntent;
    }
    /**
     * Constructs and retrieves the GeofencingRequest based on the list of geofences.
     * @return The GeofencingRequest.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(getGeoFences());
        return builder.build();
    }

    /**
     * Retrieves or creates a PendingIntent for geofence events.
     * @return The PendingIntent for geofence events.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (getGeoPendingIntent() != null) {
            return getGeoPendingIntent();
        }
        Intent intent = new Intent(this, GeoReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        setGeoPendingIntent(PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_MUTABLE));
        return getGeoPendingIntent();
    }


    /**
     * Adds geofences based on a list of ReminderModel objects.
     * It removes existing geofences and adds new ones.
     * @param realModels List of ReminderModel objects to create geofences from.
     */
    public void addGeofences(List<ReminderModel> realModels){
        if(isForeground()){
            geofencingClient.removeGeofences(getGeofencePendingIntent())
                    .addOnSuccessListener(getMainExecutor(), (e)->{
                        Log.d(LOG_TAG,"Removed geofences");
                    });
            // Clear out the geofences
            setGeoFences(new ArrayList<>());

            // Add all the
            for( ReminderModel m : realModels){
                getGeoFences().add(new Geofence.Builder()
                        .setRequestId("Single")
                        .setCircularRegion(
                                m.getLatitude(),
                                m.getLongitude(),
                                m.getRadius()
                        )
                        .setExpirationDuration(GEOFENCE_EXPIRATION)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }

            startObserving();
        }
    }

    /**
     * Starts the foreground service, displaying a notification.
     * Checks for necessary permissions before starting the service.
     */
    private void startProcess(){
        if(isForeground())
            return;
        setForegroundBool(true);

        int foreground = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE);
        if (foreground == PackageManager.PERMISSION_DENIED) {
            Log.d(LOG_TAG, "Permission denied for service!");
            stopSelf();
        }
        try {
            NotificationChannel chan = new NotificationChannel("c2", "GeoLocator", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "c2");
            Notification notification = notificationBuilder
                    .setContentTitle("Tracking for Geofences!")
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .build();

            startForeground(1, notification);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Unable to run foreground service!");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public GeofenceService getService() {
            return GeofenceService.this;
        }
    }
}