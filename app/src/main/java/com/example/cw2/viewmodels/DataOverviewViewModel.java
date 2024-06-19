package com.example.cw2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cw2.repos.MovementRepo;
import com.example.cw2.repos.ReminderRepo;
import com.example.cw2.repos.SettingsRepo;
import com.example.cw2.repos.StateRepo;

import java.time.Instant;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DataOverviewViewModel extends CommonDataViewModel{


    MovementRepo movementRepo;
    ReminderRepo reminderRepo;

    // Exposed Livedata
    LiveData<Integer> numberRoutesToday;
    LiveData<Integer> numberRemindersToday;
    LiveData<Integer> numberReminders;


    /**
     * Constructor for DataOverviewViewModel.
     * @param repo      The SettingsRepo instance.
     * @param stateRepo The StateRepo instance.
     * @param movementRepo The MovementRepo instance.
     * @param reminderRepo The ReminderRepo instance.
     */
    @Inject
    public DataOverviewViewModel(SettingsRepo repo, StateRepo stateRepo, MovementRepo movementRepo, ReminderRepo reminderRepo) {
        super(repo, stateRepo);
        numberReminders = getStateRepo().getNumReminders();
        this.movementRepo = movementRepo;
        this.reminderRepo = reminderRepo;

        // Get all routes done within 24 hours
        long left = Instant.now().toEpochMilli() - 60*60*24*500;
        long right = Instant.now().toEpochMilli() + 60*60*24*500;


        numberRoutesToday = Transformations.switchMap(movementRepo.getAllDateLive(left,right),(ls)-> new MutableLiveData<>(ls.size()));
        numberRemindersToday = Transformations.switchMap(reminderRepo.getAllDateLive(left,right),(ls)-> new MutableLiveData<>(ls.size()));
    }
    /**
     * Retrieves the LiveData representing the number of reminders.
     * @return LiveData representing the number of reminders.
     */
    public LiveData<Integer> getNumberReminders() {
        return numberReminders;
    }

    /**
     * @return LiveData representing the number of routes made in 12 hours each way
     */
    public LiveData<Integer> getNumberRoutesToday() {
        return numberRoutesToday;
    }

    /**
     * @return LiveData representing the number of reminders made in 12 hours each way
     */
    public LiveData<Integer> getNumberRemindersToday() {
        return numberRemindersToday;
    }
}
