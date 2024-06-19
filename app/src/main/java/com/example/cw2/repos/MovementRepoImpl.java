package com.example.cw2.repos;

import static com.example.cw2.config.Configuration.LOG_TAG;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cw2.data.Database;
import com.example.cw2.data.MovementDAO;
import com.example.cw2.data.MovementEntity;
import com.example.cw2.data.ReminderDAO;
import com.example.cw2.data.ReminderEntity;
import com.example.cw2.models.MovementModel;
import com.example.cw2.models.ReminderModel;
import com.google.android.gms.maps.model.LatLng;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;
import kotlinx.coroutines.flow.Flow;

/**
 * Implementation of the MovementRepo interface.
 */
@Singleton
public class MovementRepoImpl implements MovementRepo{
    private MovementDAO dataSource;

    LiveData<List<MovementEntity>> data;

    @Inject
    public MovementRepoImpl(@ApplicationContext Context c, Database database){
        setDataSource(database.movementDAO());
        setData(getDataSource().getAll());
        Log.d(LOG_TAG,"Hi there movement test " + getDataSource().getAll().getValue());
    }

    /**
     * @return The Data source, the DAO, that is only used in the implementation.
     */
    private MovementDAO getDataSource() {
        return dataSource;
    }

    private void setDataSource(MovementDAO dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return Livedata of all the entities.
     * It is in Entity form so first must be transferred to business logic with getAllLive().
     */
    private LiveData<List<MovementEntity>> getData() {
        return data;
    }

    /**
     * @param data Sets the init data for the entities.
     */
    private void setData(LiveData<List<MovementEntity>> data) {
        this.data = data;
    }

    /**
     * This is required whenever returning livedata lists.
     * @param data Live data of some size that needs to be transformed to a list of models, not entities.
     * @return A list of MovementModels, from MovementEntities
     */
    private LiveData<List<MovementModel>>  transformer(LiveData<List<MovementEntity>> data){
        return Transformations.switchMap(data, movementEntities -> {
            List<MovementModel> list = movementEntities.stream().map(
                    entity -> new MovementModel(entity.mid,entity.name,entity.type,entity.date,entity.points)).collect(Collectors.toList());
            MutableLiveData<List<MovementModel>> live = new MutableLiveData<>();
            live.setValue(list);
            return live;
        });
    }

    @Override
    public LiveData<List<MovementModel>> getAllLive() {
        // Creates a new livedata that is mapped to reminderModel (using nested mapping)
        return transformer(getData());
    }

    @Override
    public LiveData<List<MovementModel>> getAllDateLive(long left, long right) {
        // Creates a new livedata that is mapped to reminderModel (using nested mapping)
        return transformer(getDataSource().getAllDate(left,right));
    }

    @Override
    public Single<MovementModel> getModel(int mId) {
        // Creates a new livedata that is mapped to reminderModel (using nested mapping)
        Single<MovementEntity> sing  = getDataSource().get(mId);
        return sing.subscribeOn(new NewThreadScheduler()).map(e
                -> new MovementModel(e.mid,e.name,e.type,e.date,e.points));
    }


    @Override
    public Completable insertMovement(String name, Integer type, List<LatLng> pairs) {
        MovementEntity movementEntity = new MovementEntity();
        movementEntity.date = Instant.now().toEpochMilli();
        movementEntity.name = name;
        movementEntity.type = type;
        movementEntity.points = pairs;
        return getDataSource().insertAll(movementEntity);
    }

    @Override
    public Completable deleteMovement(int mId) {
        return getDataSource().deleteByUserId(mId);
    }

    @Override
    public Completable updateMovement(int type, String name, int mid) {
        return getDataSource().updateMovements(type,name,mid);
    }

    @Override
    public Completable addPoints(List<LatLng> points, int mid) {
        return getDataSource().updatePoints(points,mid);
    }


}
