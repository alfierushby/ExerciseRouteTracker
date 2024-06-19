package com.example.cw2.geofencing;

import static android.provider.Settings.System.getString;

import static com.example.cw2.config.Configuration.LOG_TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.cw2.R;
import com.example.cw2.repos.ReminderRepo;
import com.example.cw2.repos.StateRepo;
import com.example.cw2.services.GeofenceService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;

/**
 * Thanks to <a href="https://developer.android.com/develop/sensors-and-location/location/geofencing#create-geofence-objects">Link</a>
 * for guidance on this receiver.
 */
public class GeoReceiver extends BroadcastReceiver {


    RepoEntryPoint repoEntryPoint;

    /**
     * Important for the individual elements to communicate with the repo, without referencing
     * any Model specifically.
     */
    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface RepoEntryPoint {
        public StateRepo getStateRepo();

    }

    /**
     * @return The Repo entry point containing specified dependency injected repositories.
     */
    public RepoEntryPoint getRepoEntryPoint() {
        return repoEntryPoint;
    }


    /**
     * @return A notification channel for the reminder notifications (when entering and leaving a reminder)
     */
    private NotificationChannel createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("GEO", "C1", importance);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        return channel;
    }

    /**
     * Called whenever a user enters or exits a reminder zone.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // If the repo entrypoint is null set it, with the context.
        if(getRepoEntryPoint() == null)
            this.repoEntryPoint = EntryPointAccessors.fromApplication((context.getApplicationContext()), GeoReceiver.RepoEntryPoint.class);
        // Increment the number of reminders counted.
        getRepoEntryPoint().getStateRepo().setNumReminders(getRepoEntryPoint().getStateRepo().getNumReminders().getValue()+1);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(LOG_TAG, errorMessage);
            return;
        }
        // Get the transition type (enter or exit).
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Determine the notification title.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            String title = "";
            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                title = context.getResources().getString(R.string.enterGeoFence);
            else
                title = context.getResources().getString(R.string.exitedGeoFence);

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Send notification so the user knows they have left or entered a reminder zone

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "GEO")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(createNotificationChannel());
            notificationManager.notify(0, builder.build());
        }
        }

}