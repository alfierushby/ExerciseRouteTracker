package com.example.cw2.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * Used to store Reminder entities, for reminders.
 */
@Dao
public interface ReminderDAO {
    /**
     * @return A live data of all the reminders. Suitable for observing change.
     */
    @Query("SELECT * FROM reminderentity")
    LiveData<List<ReminderEntity>> getAll();

    /**
     * @return A single of all the reminders. Suitable for direct extraction.
     */
    @Query("SELECT * FROM reminderentity")
    Single<List<ReminderEntity>> getAllDirect();
    @Query("SELECT * FROM reminderentity WHERE date BETWEEN :left AND :right")
    LiveData<List<ReminderEntity>> getAllDate(long left, long right);

    /**
     * @param eId Entity ID
     * @return A single of a reminder. Suitable for direct extraction.
     */
    @Query("SELECT * FROM reminderentity WHERE eid = :eId")
    Single<ReminderEntity> get(long eId);

    /**
     * @param reminderEntities 1 or more reminder entities
     * @return A completable task to be executed.
     */
    @Insert
    public Completable insertAll(ReminderEntity... reminderEntities);

    /**
     * @param entity a  reminder entity to be deleted
     * @return A completable task to be executed.
     */
    @Delete
    public Completable delete(ReminderEntity entity);

    /**
     * @param eId Id of reminder entity
     * @return A completable task to be executed.
     */
    @Query("DELETE FROM reminderentity WHERE eid = :eId")
    public Completable deleteByEId(long eId);

    /**
     * @param radius radius of the reminder in the map.
     * @param name name of the reminder
     * @param eid id of the reminder to be updated.
     * @return A completable task to be executed.
     */
    @Query("UPDATE ReminderEntity SET radius=:radius, name=:name  WHERE eid = :eid")
    public Completable updateReminders(int radius, String name, int eid);

    /**
     * @param longitude longitude of the reminder
     * @param latitude latitiude of the reminder
     * @param eid id of the reminder to be updated.
     * @return A completable task to be executed.
     */
    @Query("UPDATE ReminderEntity SET longitude=:longitude, latitude=:latitude  WHERE eid = :eid")
    public Completable updateReminders(Double longitude, Double latitude, int eid);
}
