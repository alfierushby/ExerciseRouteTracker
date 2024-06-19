package com.example.cw2.repos;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import com.example.cw2.models.MovementModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Singleton
public class StateRepoImpl implements StateRepo {

    MutableLiveData<Boolean> editing = new MutableLiveData<>(false);
    MutableLiveData<Integer> mId = new MutableLiveData<>(0);
    MutableLiveData<Integer> eID = new MutableLiveData<>(0);
    MutableLiveData<Integer> numReminders = new MutableLiveData<>(0);

    List<LatLng> points = new ArrayList<>();
    LatLng point;
    int radius;

   private final MovementRepo movementRepo;
   private final ReminderRepo reminderRepo;

    @Inject
    public StateRepoImpl(MovementRepo movementRepo, ReminderRepo reminderRepo) {
        this.reminderRepo = reminderRepo;
        this.movementRepo = movementRepo;
    }

    /**
     * @return The dependency injected reminder repo
     */
    public ReminderRepo getReminderRepo() {
        return reminderRepo;
    }

    /**
     * @return The dependency injected movement repo
     */
    public MovementRepo getMovementRepo() {
        return movementRepo;
    }
    @Override
    public MutableLiveData<Boolean> getEditing() {
        return editing;
    }
    @Override
    public int getReminderRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
    @Override
    public void setEditing(boolean editing) {
        getEditing().setValue(editing);
        if(!editing){
            // Empty points
            setMovementPoints(new ArrayList<>());
            setReminderPoint(new LatLng(0,0));
            setRadius(0);
        }
    }
    @Override
    public MutableLiveData<Integer> getmId() {
        return mId;
    }
    @Override
    @SuppressLint("CheckResult")
    public void setMId(int mId) {
        getmId().setValue(mId);
        getMovementRepo().getModel(mId).subscribe((e) -> {
           setMovementPoints(e.getPoints());
        });
    }
    @Override
    public MutableLiveData<Integer> geteID() {
        return eID;
    }
    @Override
    @SuppressLint("CheckResult")
    public void setEId(int eID) {
        geteID().setValue(eID);
        getReminderRepo().getModel(eID).subscribe((e) -> {
            setReminderPoint(new LatLng(e.getLatitude(),e.getLongitude()));
            setRadius(e.getRadius());
        });
    }
    @Override
    public List<LatLng> getMovementPoints() {
        return points;
    }
    @Override
    public void insertMovementPoint(LatLng p){
        getMovementPoints().add(p);
    }
    @Override
    public void setMovementPoints(List<LatLng> points) {
        this.points = points;
    }
    @Override
    public LatLng getReminderPoint() {
        return point;
    }
    @Override
    public void setReminderPoint(LatLng point) {
        this.point = point;
    }
    @Override
    public MutableLiveData<Integer> getNumReminders() {
        return numReminders;
    }
    @Override
    public void setNumReminders(int v) {
        getNumReminders().setValue(v);
    }
}
