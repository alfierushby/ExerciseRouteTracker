package com.example.cw2.viewmodels;


import static com.example.cw2.config.Configuration.LOG_TAG;

import android.util.Log;

import com.example.cw2.repos.SettingsRepo;
import com.example.cw2.repos.StateRepo;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * View model for the settings activity, where it handles setting DataStore values for the
 * background colour.
 */
@HiltViewModel
public class SettingsViewModel extends CommonDataViewModel {

    /**
     * @param repo The injected repository, sourced from Hilt Application Container
     */
    @Inject
    public SettingsViewModel(SettingsRepo repo, StateRepo stateRepo){
        super(repo, stateRepo);
    }

    /**
     * This sets the colour index of the settings repo, which is saved and persisted.
     * @param colour Integer colour indexing the colour resources, 0-2
     */
    public void setColour(int colour){
        // Do not set a colour beyond the limit
        if(colour >= getColorResources().size()){
            Log.d(LOG_TAG,"Tried to set a colour beyond the possible array!");
            colour = getColorResources().size() -1;
        }
        getSettingsRepo().setBackgroundColour(colour);
    }
}
