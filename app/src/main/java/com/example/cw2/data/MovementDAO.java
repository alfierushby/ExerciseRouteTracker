package com.example.cw2.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * Holds all route data, meaning lists of positions with some meta data.
 */
@Dao
public interface MovementDAO {
    /**
     * @return A live data form of all the entities (rows) in this table. It is async and so should
     * be used for observations.
     */
    @Query("SELECT * FROM movemententity")
    LiveData<List<MovementEntity>> getAll();

    /**
     * @return A direct form of querying the database, returning a Single which is more suited for
     * traditional code and not observing.
     */
    @Query("SELECT * FROM movemententity")
    Single<List<MovementEntity>> getAllDirect();
    @Query("SELECT * FROM movemententity WHERE date BETWEEN :left AND :right")
    LiveData<List<MovementEntity>> getAllDate(long left, long right);

    /**
     * @param mid Movement ID
     * @return A single entity in a Single form.
     */
    @Query("SELECT * FROM movemententity WHERE mid = :mid")
    Single<MovementEntity> get(int mid);

    /**
     * @param movementEntities 1 or more entities
     * @return A completable async task to be executed.
     */
    @Insert
    public Completable insertAll(MovementEntity... movementEntities);

    /**
     * @param entity the entity to delete
     * @return A completable task to be executed.
     */
    @Delete
    public Completable delete(MovementEntity entity);

    /**
     * @param mId the Movement ID for the entity to be deleted.
     * @return A completable task to be executed.
     */
    @Query("DELETE FROM movemententity WHERE mid = :mId")
    public Completable deleteByUserId(long mId);

    /**
     * @param type Type of entity
     * @param name Name of the entity
     * @param mid ID of the entity
     * @return A completable task to be executed
     */
    @Query("UPDATE MovementEntity SET type=:type, name=:name  WHERE mid = :mid")
    public Completable updateMovements(Integer type, String name, int mid);

    /**
     * @param points A list of location points
     * @param mid ID of the entity
     * @return A completable task to be executed
     */
    @Query("UPDATE MovementEntity SET points=:points  WHERE mid = :mid")
    public Completable updatePoints(List<LatLng> points, int mid);

}