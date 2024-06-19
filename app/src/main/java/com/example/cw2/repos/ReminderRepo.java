package com.example.cw2.repos;

import androidx.lifecycle.LiveData;

import com.example.cw2.data.MovementEntity;
import com.example.cw2.data.ReminderEntity;
import com.example.cw2.models.MovementModel;
import com.example.cw2.models.ReminderModel;
import com.example.cw2.views.Reminders;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


/**
 * Handles the communication to the reminder table in the database.
 * Use this interface when dependency injecting with Hilt.
 */
public interface ReminderRepo {
    /**
     * @return A list of reminder models that are stored in the database, in live data form.
     */
    LiveData<List<ReminderModel>> getAllLive();

    /**
     * Gets all entities within the time interval.
     * @return A list of reminder models that are stored in the database, in live data form.
     */
    LiveData<List<ReminderModel>> getAllDateLive(long left, long right);


    /**
     * @return A single reminder model in aysnc form
     */
    Single<ReminderModel> getModel(int eId);

    /**
     * @return A completable which needs to be executed on non-ui thread.
     * @param name Name of the reminder
     * @param radius Radius of the reminder's point
     * @param lat Latitude of the reminder's point
     * @param lon Longitude of the reminder's point
     */
    Completable insertReminder(String name, int radius, Double lat, Double lon);


    /**
     * @param eId The id of the reminder
     * @return A completable object that needs to be set to execute on a non-ui thread.
     */
    Completable deleteReminder(int eId);


    /**
     * Updates the specified columns. Null does not entail they are not updated.
     * @param radius radius column
     * @param name name column
     * @param eid id of entity
     * @return An async task
     */
    Completable updateReminder(int radius, String name, int eid);

    /**
     * @param longitude Longitude of the geofence
     * @param latitude Latitude of the geofence
     * @param eid id of entity
     * @return An async task
     */
    Completable updateReminders(Double longitude, Double latitude, int eid);
}

