package com.example.cw2.repos;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * State of the app, should be initialized in Hilt with a Singleton (Application Scope).
 * Is not persisted.
 * !!!!
 * This is the top level repo, as in it defines a global temp state that can retrieve extra information
 * from other repositories.
 */
public interface StateRepo {
    /**
     * Sets the eId, and updates the geofence point.
     * @param eId An integer corresponding to the currently selected mId
     */
    void setEId(int eId);
    /**
     * This set the mId. Updates the points.
     * @param mId An integer corresponding to the currently selected mId
     */
    void setMId(int mId);

    /**
     * @param mode True iff editing is enabled.
     */
    void setEditing(boolean mode);

    /**
     * @return True if editing is enabled
     */
     MutableLiveData<Boolean> getEditing();

    /**
     * @return  the currently selected Id of a MovementEntity
     */
     MutableLiveData<Integer> getmId();

    /**
     * Note that this should not be carelessly relied upon. It is not useful for creating new instances,
     * as their IDs are not set here unless explicitly done so.
     * @return  the currently selected Id of a ReminderEntity
     */
     MutableLiveData<Integer> geteID();

    /**
     * @return A list of points being used by global entities like the service. It is not useful for creating new instances,
     * as their IDs are not set here unless explicitly done so.
     */
     List<LatLng> getMovementPoints();

    /**
     * Inserts a point, for convenience
     * @param p A latLng point
     */
     void insertMovementPoint(LatLng p);

    /**
     * Used to reset to a new points state.
     * @param points Points to override previous state.
     */
    void setMovementPoints(List<LatLng> points);

    /**
     * @return A point corresponding to the geoblocker location
     */
    LatLng getReminderPoint();

    /**
     * @return The radius of the current selected reminder entity
     */
    int getReminderRadius();


    /**
     * Sets the geoblocker location
     * @param point the location of the geoblocker
     */
    void setReminderPoint(LatLng point) ;

    /**
     * @return Number of reminders livedata form
     */
    public MutableLiveData<Integer> getNumReminders();

    /**
     * @param numReminders defines how many reminders have been witnessed
     */
    public void setNumReminders(int numReminders);


}
