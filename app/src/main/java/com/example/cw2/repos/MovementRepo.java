package com.example.cw2.repos;

import androidx.lifecycle.LiveData;

import com.example.cw2.data.MovementEntity;
import com.example.cw2.models.MovementModel;
import com.example.cw2.models.ReminderModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * The exposed repository for movements. This exposes the used functions for other parts of the app.
 */
public interface MovementRepo {

    /**
     * @return A list of reminder models that are stored in the database, in live data form.
     */
    LiveData<List<MovementModel>> getAllLive();

    /**
     * Gets all models within the date time interval.
     * @return A list of reminder models that are stored in the database, in live data form.
     */
    LiveData<List<MovementModel>> getAllDateLive(long left, long right);

    /**
     * This must be observed to get a value.
     * @param mId The id of the movement
     * @return An asynchronous flowable.
     */
    Single<MovementModel> getModel(int mId);

    /**
     * @param name Name of the movement
     * @param type Type of the movement (running, walking, cycling)
     * @param pairs List of location pairs
     * @return A completable object that needs to be set to execute on a non-ui thread.
     */
    Completable insertMovement(String name, Integer type, List<LatLng> pairs);


    /**
     * @param mId The id of the movement
     * @return A completable object that needs to be set to execute on a non-ui thread.
     */
    Completable deleteMovement(int mId);


    /**
     * All non-equal fields will be updated. Must include an id.
     * @return An async task
     * @param type Type columns
     * @param name name column
     * @param mid id of entity
     */
    Completable updateMovement(int type, String name, int mid);

    /**
     * Just for updating the points list.
     * @param points A list of points for use by maps
     * @param mid Id of the entity
     * @return An async task
     */
    Completable addPoints(List<LatLng> points, int mid);
}
