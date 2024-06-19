package com.example.cw2.viewmodels;

import static com.example.cw2.config.Configuration.LOG_TAG;
import static com.example.cw2.config.Configuration.NUMBER_OF_MOVEMENT_TYPES;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cw2.Events;
import com.example.cw2.data.MovementEntity;
import com.example.cw2.repos.MovementRepo;
import com.example.cw2.repos.SettingsRepo;
import com.example.cw2.repos.StateRepo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;

@HiltViewModel
public class MovementCreatorViewModel extends CommonDataViewModel {

    // Repository for managing movement data
    MovementRepo movementRepo;
    // MutableLiveData for movement type and name
    private MutableLiveData<Integer> movementType = new MutableLiveData<>(0);
    private MutableLiveData<String> movementName = new MutableLiveData<>("");
    // Scheduler for threading
    private final NewThreadScheduler threadScheduler = new NewThreadScheduler();

    /**
     * @param newMovement True if a new movement is being created
     */
    public void setNewMovement(Boolean newMovement) {
        getStateRepo().setEditing(newMovement);
    }


    /**
     * Constructor for MovementCreatorViewModel.
     * @param repo       The SettingsRepo instance.
     * @param movementRepo The MovementRepo instance.
     * @param stateRepo   The StateRepo instance.
     */
    @SuppressLint("CheckResult")
    @Inject
    public MovementCreatorViewModel(SettingsRepo repo, MovementRepo movementRepo, StateRepo stateRepo) {
        super(repo, stateRepo);
        this.movementRepo = movementRepo;
        Log.d(LOG_TAG, "Editing!");


        if (Boolean.TRUE.equals(getStateRepo().getEditing().getValue())) {
            assert getStateRepo().getmId().getValue() != null;
            Log.d(LOG_TAG, "MID " + getStateRepo().getmId().getValue());
            getMovementRepo().getModel(getStateRepo().getmId().getValue()).subscribe((e) -> {
                getMovementType().postValue(e.getType());
                getMovementName().postValue(e.getName());
            });
        }


    }

    /**
     * Retrieves the MovementRepo instance.
     * @return The MovementRepo instance.
     */
    public MovementRepo getMovementRepo() {
        return movementRepo;
    }

    /**
     * Sets the movement type, ensuring it does not exceed the maximum value.
     * @param type The movement type to set.
     */
    public void setType(int type) {
        if (type >= NUMBER_OF_MOVEMENT_TYPES) {
            type = NUMBER_OF_MOVEMENT_TYPES - 1;
        }
        getMovementType().setValue(type);
    }
    /**
     * Retrieves the MutableLiveData representing the movement type.
     * @return MutableLiveData representing the movement type.
     */
    public MutableLiveData<Integer> getMovementType() {
        return movementType;
    }

    /**
     * Retrieves the MutableLiveData representing the movement name.
     * @return MutableLiveData representing the movement name.
     */
    public MutableLiveData<String> getMovementName() {
        return movementName;
    }
    /**
     * Retrieves the thread scheduler for asynchronous operations.
     * @return The NewThreadScheduler instance.
     */
    public NewThreadScheduler getThreadScheduler() {
        return threadScheduler;
    }

    /**
     * Records the movement by inserting a new movement or updating an existing one.
     */
    public void recordMovement() {
        if (Boolean.FALSE.equals(getStateRepo().getEditing().getValue())) {
            // As new, so editing is now true.
            getStateRepo().setEditing(true);
            Completable exec = getMovementRepo().insertMovement(getMovementName().getValue(),
                    getMovementType().getValue(),
                    getStateRepo().getMovementPoints());
            // Sets it to run on a new thread
            exec.subscribeOn(getThreadScheduler()).subscribe();
            EventBus.getDefault().post(new Events.FinishEvent());
            // Set to edit mode.
            setNewMovement(false);
        } else {
            MovementEntity entity = new MovementEntity();
            getMovementRepo().updateMovement(getMovementType().getValue(), getMovementName().getValue(), getStateRepo().getmId().getValue()).subscribeOn(getThreadScheduler()).subscribe();
            updatePoints();
        }

    }

    /**
     * Updates the movement points in the repository.
     * Note that this assumes the global state contains the correct array of points.
     * The global state should be reset on each change in the selected entity.
     */
    public void updatePoints() {
        if (Boolean.TRUE.equals(getStateRepo().getEditing().getValue())) {
            getMovementRepo().addPoints(getStateRepo().getMovementPoints(), getStateRepo().getmId().getValue()).subscribeOn(getThreadScheduler()).subscribe();
        }
    }


}