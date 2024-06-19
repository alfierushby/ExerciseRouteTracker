package com.example.cw2.viewmodels;

import com.example.cw2.repos.ReminderRepo;
import com.example.cw2.repos.SettingsRepo;
import com.example.cw2.repos.StateRepo;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RemindersViewModel extends CommonDataViewModel {
    // Repository for managing reminder data
    private final ReminderRepo reminderRepo;
    /**
     * Constructor for RemindersViewModel.
     * @param settingsRepo The SettingsRepo instance.
     * @param reminderRepo The ReminderRepo instance.
     * @param stateRepo    The StateRepo instance.
     */
    @Inject
    public RemindersViewModel(SettingsRepo settingsRepo, ReminderRepo reminderRepo, StateRepo stateRepo) {
        super(settingsRepo, stateRepo);
        this.reminderRepo = reminderRepo;
    }
    /**
     * Retrieves the ReminderRepo instance.
     * @return The ReminderRepo instance.
     */
    public ReminderRepo getReminderRepo() {
        return reminderRepo;
    }
}
