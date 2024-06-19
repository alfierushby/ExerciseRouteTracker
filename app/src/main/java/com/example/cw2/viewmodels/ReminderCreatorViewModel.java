package com.example.cw2.viewmodels;

import static com.example.cw2.config.Configuration.DEFAULT_REMINDER_LATITUDE;
import static com.example.cw2.config.Configuration.DEFAULT_REMINDER_LONGITUDE;
import static com.example.cw2.config.Configuration.DEFAULT_REMINDER_RADIUS;
import static com.example.cw2.config.Configuration.LOG_TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cw2.Events;
import com.example.cw2.data.ReminderEntity;
import com.example.cw2.repos.ReminderRepo;
import com.example.cw2.repos.SettingsRepo;
import com.example.cw2.repos.StateRepo;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;

@HiltViewModel
public class ReminderCreatorViewModel extends CommonDataViewModel {
    // Repository for managing reminder data
    private final ReminderRepo reminderRepo;

    // Scheduler for threading
    private final NewThreadScheduler threadScheduler = new NewThreadScheduler();

    // MutableLiveData for reminder properties
    private final MutableLiveData<String>  reminderName = new MutableLiveData<>("");
    private final MutableLiveData<String> reminderRadius = new MutableLiveData<>(Integer.toString(DEFAULT_REMINDER_RADIUS));

    // Reminder coordinates
    private Double reminderLongitude = 0.;
    private Double reminderLatitude = 0.;


    /**
     * Constructor for ReminderCreatorViewModel.
     * @param settingsRepo The SettingsRepo instance.
     * @param reminderRepo The ReminderRepo instance.
     * @param stateRepo    The StateRepo instance.
     */
    @SuppressLint("CheckResult")
    @Inject
    public ReminderCreatorViewModel(SettingsRepo settingsRepo, ReminderRepo reminderRepo, StateRepo stateRepo) {
        super(settingsRepo,stateRepo);
        this.reminderRepo = reminderRepo;

        if (Boolean.TRUE.equals(getStateRepo().getEditing().getValue())){
            assert getStateRepo().geteID().getValue() != null;
            getReminderRepo().getModel(getStateRepo().geteID().getValue()).subscribe((e)->{
                getReminderName().postValue(e.getName());
                getReminderRadius().postValue(Integer.toString(e.getRadius()));
            });
        }
    }
    /**
     * Retrieves the ReminderRepo instance.
     * @return The ReminderRepo instance.
     */
    public ReminderRepo getReminderRepo() {
        return reminderRepo;
    }
    /**
     * Retrieves the MutableLiveData representing the reminder name.
     * @return MutableLiveData representing the reminder name.
     */
    public MutableLiveData<String> getReminderName() {
        return reminderName;
    }
    /**
     * Retrieves the MutableLiveData representing the reminder radius.
     * @return MutableLiveData representing the reminder radius.
     */
    public MutableLiveData<String> getReminderRadius() {
        return reminderRadius;
    }
    /**
     * Retrieves the reminder longitude.
     * @return The reminder longitude.
     */
    public Double getReminderLongitude() {
        return reminderLongitude;
    }
    /**
     * Retrieves the reminder latitude.
     * @return The reminder latitude.
     */
    public Double getReminderLatitude() {
        return reminderLatitude;
    }
    /**
     * Retrieves the thread scheduler for asynchronous operations.
     * @return The NewThreadScheduler instance.
     */
    private NewThreadScheduler getThreadScheduler() {
        return threadScheduler;
    }
    /**
     * Sets the reminder latitude.
     * @param reminderLatitude The reminder latitude to set.
     */
    public void setReminderLatitude(Double reminderLatitude) {
        this.reminderLatitude = reminderLatitude;
    }
    /**
     * Sets the reminder longitude.
     * @param reminderLongitude The reminder longitude to set.
     */
    public void setReminderLongitude(Double reminderLongitude) {
        this.reminderLongitude = reminderLongitude;
    }

    /**
     * Adds a new reminder or updates an existing one. Called by the view.
     */
    public void recordReminder(){
        if(Boolean.FALSE.equals(getStateRepo().getEditing().getValue())) {
            // The latitude and longitude are set beforehand by a location service.
            Completable exec = getReminderRepo().insertReminder(getReminderName().getValue(),
                    Integer.parseInt(Objects.requireNonNull(getReminderRadius().getValue())),
                    getReminderLatitude()
                    , getReminderLongitude());
            // Sets it to run on a new thread
            exec.subscribeOn(getThreadScheduler()).subscribe();
            EventBus.getDefault().post(new Events.FinishEvent());
            return;
        } else{
            getReminderRepo().updateReminders(getReminderLongitude(),getReminderLatitude(),getStateRepo().geteID().getValue()).subscribeOn(getThreadScheduler()).subscribe();
            getReminderRepo().updateReminder( Integer.parseInt(getReminderRadius().getValue()), getReminderName().getValue(), getStateRepo().geteID().getValue()).subscribeOn(getThreadScheduler()).subscribe();
        }
    }

}
