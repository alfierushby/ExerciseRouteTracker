package com.example.cw2.repos;

import static com.example.cw2.config.Configuration.LOG_TAG;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cw2.data.Database;
import com.example.cw2.data.MovementEntity;
import com.example.cw2.data.ReminderDAO;
import com.example.cw2.data.ReminderEntity;
import com.example.cw2.models.MovementModel;
import com.example.cw2.models.ReminderModel;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;

/**
 * A repository that saves and gets from a room database, specifically for the reminders.
 */
@Singleton
public class ReminderRepoImpl implements ReminderRepo {

    private ReminderDAO dataSource;

    LiveData<List<ReminderEntity>> data;


    @Inject
    public ReminderRepoImpl(@ApplicationContext Context c, Database database){
        setDataSource(database.reminderDAO());
        setData(getDataSource().getAll());
        Log.d(LOG_TAG,"Hi there reminder");
    }

    /**
     * This is required whenever returning livedata lists.
     * @param data Live data of some size that needs to be transformed to a list of models, not entities.
     * @return A list of ReminderModels, from ReminderEntities
     */
    private LiveData<List<ReminderModel>>  transformer(LiveData<List<ReminderEntity>> data) {
        return Transformations.switchMap(data, reminderEntities -> {
            List<ReminderModel> list = reminderEntities.stream().map(
                    entity -> new ReminderModel(entity.eid, entity.name, entity.radius, entity.date, entity.latitude, entity.longitude)).collect(Collectors.toList());
            MutableLiveData<List<ReminderModel>> live = new MutableLiveData<>();
            live.setValue(list);
            return live;
        });
    }


    @Override
    public LiveData<List<ReminderModel>> getAllLive(){
        // Creates a new livedata that is mapped to reminderModel (using nested mapping)
         return transformer(getData());
    }

    @Override
    public LiveData<List<ReminderModel>> getAllDateLive(long left, long right) {
        return transformer(getDataSource().getAllDate(left,right));
    }

    @Override
    public Single<ReminderModel> getModel(int eId) {
        // Creates a new livedata that is mapped to reminderModel (using nested mapping)
        Single<ReminderEntity> sin  = getDataSource().get(eId);
        return sin.subscribeOn(new NewThreadScheduler()).map(e
                -> new ReminderModel(e.eid,e.name,e.radius,e.date,e.latitude, e.longitude));
    }
    @Override
    public Completable insertReminder(String name, int radius, Double lat, Double lon){
        ReminderEntity reminderEntity = new ReminderEntity();
        reminderEntity.date = Instant.now().toEpochMilli();
        reminderEntity.name = name;
        reminderEntity.radius = radius;
        reminderEntity.latitude = lat;
        reminderEntity.longitude = lon;
        Completable completable = getDataSource().insertAll(reminderEntity);
        return completable;
    }
    @Override
    public Completable deleteReminder(int eId){
        return getDataSource().deleteByEId(eId);
    }

    @Override
    public Completable updateReminder(int radius, String name, int eid) {
        return getDataSource().updateReminders( radius,  name,  eid);
    }

    @Override
    public Completable updateReminders(Double longitude, Double latitude, int eid) {
        return getDataSource().updateReminders( longitude, latitude,  eid);
    }

    /**
     * @return gets the live data, for usage in transforming the entity it holds to a ReminderModel.
     */
    private LiveData<List<ReminderEntity>> getData() {
        return data;
    }

    /**
     * @param data Initialise function to see the initial live data of the database
     */
    private void setData(LiveData<List<ReminderEntity>> data) {
        this.data = data;
    }

    /**
     * @return Gets the DAO of the reminder table
     */
    private ReminderDAO getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the source to be set in init
     */
    private void setDataSource(ReminderDAO dataSource) {
        this.dataSource = dataSource;
    }
}
