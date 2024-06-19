package com.example.cw2.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.example.cw2.R;
import com.example.cw2.repos.ReminderRepo;
import com.example.cw2.repos.SettingsRepo;
import com.example.cw2.repos.StateRepo;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * View model for the home.
 */
@HiltViewModel
public class HomeViewModel extends CommonDataViewModel {
    MutableLiveData<String> resource = new MutableLiveData<>("");
    /**
     * Constructor for DataOverviewViewModel.
     * @param repo      The SettingsRepo instance.
     * @param stateRepo The StateRepo instance.
     */
    @Inject
    public HomeViewModel(SettingsRepo repo, StateRepo stateRepo) {
        super(repo, stateRepo);
    }
    /**
     * Retrieves the MutableLiveData<String> resource.
     * @return The MutableLiveData<String> resource.
     */
    public MutableLiveData<String> getResource() {
        return resource;
    }
    /**
     * Sets the value of the MutableLiveData<String> resource.
     * @param val The String value to set.
     */
    public void setResource(String val) {
        getResource().setValue(val);
    }
}
