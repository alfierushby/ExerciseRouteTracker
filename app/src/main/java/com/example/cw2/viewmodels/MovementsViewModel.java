package com.example.cw2.viewmodels;

import com.example.cw2.repos.MovementRepo;
import com.example.cw2.repos.ReminderRepo;
import com.example.cw2.repos.SettingsRepo;
import com.example.cw2.repos.StateRepo;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MovementsViewModel extends CommonDataViewModel {
    // Repository for managing movement data
    private final MovementRepo movementRepo;
    /**
     * Constructor for MovementsViewModel.
     * @param repo       The SettingsRepo instance.
     * @param movementRepo The MovementRepo instance.
     * @param stateRepo   The StateRepo instance.
     */
    @Inject
    public MovementsViewModel(SettingsRepo repo, MovementRepo movementRepo, StateRepo stateRepo) {
        super(repo,stateRepo);
        this.movementRepo = movementRepo;
    }
    /**
     * Retrieves the MovementRepo instance.
     * @return The MovementRepo instance.
     */
    public MovementRepo getMovementRepo() {
        return movementRepo;
    }

}
