package com.example.cw2.viewmodels;

import static androidx.lifecycle.LiveDataReactiveStreams.fromPublisher;

import android.graphics.drawable.ColorDrawable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.cw2.repos.SettingsRepo;
import com.example.cw2.repos.StateRepo;

import org.reactivestreams.Publisher;

import java.util.ArrayList;

/**
 * A general view model that implements the settings repo livedata logic.
 * Specifically, it integrates with the SettingsRepo and exposes the settings live data to the View.
 * This is never directly instantiated
 */
public abstract class CommonDataViewModel extends ViewModel {

    private ArrayList<Integer> colorResources = new ArrayList<>();
    private final SettingsRepo settingsRepo;
    private final StateRepo stateRepo;

    private LiveData<Integer> backgroundColour;
    private LiveData<ColorDrawable> backgroundColourDrawable;

    /**
     * Constructor for CommonDataViewModel.
     *
     * @param repo      The SettingsRepo instance.
     * @param stateRepo The StateRepo instance.
     */
    public CommonDataViewModel(SettingsRepo repo, StateRepo stateRepo){
        this.settingsRepo = repo;
        this.stateRepo = stateRepo;
        setupLiveData();
    }

    /**
     * Connects the Settings repo to the Setting's view by setting the exposed backgroundColour live data.
     * Note that the Drawable livedata is what the view data binds to.
     */
    private void setupLiveData(){
        // It is important to interface in the view model with livedata.
        // It took way too long to figure out how to find the java equivalent from kotlin...
        setBackgroundColour(fromPublisher((Publisher<Integer>) getSettingsRepo().getBackgroundColour()));
        // This uses a mapping that holds a function that's called everytime the colour is changed.
        setBackgroundColourDrawable(Transformations.switchMap(backgroundColour, colour ->{
            MutableLiveData<ColorDrawable> c = new MutableLiveData<>();
            c.setValue(new ColorDrawable(colorResources.get(colour)));
            return c;
        } ));
    }
    /**
     * Sets the background color Drawable LiveData.
     * @param backgroundColourDrawable The LiveData representing the ColorDrawable for the background color.
     */
    private void setBackgroundColourDrawable(LiveData<ColorDrawable> backgroundColourDrawable) {
        this.backgroundColourDrawable = backgroundColourDrawable;
    }
    /**
     * Sets the background color LiveData.
     * @param backgroundColour The LiveData representing the background color.
     */
    private void setBackgroundColour(LiveData<Integer> backgroundColour) {
        this.backgroundColour = backgroundColour;
    }
    //////////////////////
    // Public Functions //
    //////////////////////

    /**
     * Retrieves the SettingsRepo instance.
     * @return The SettingsRepo instance.
     */
    public StateRepo getStateRepo() {
        return stateRepo;
    }

    /**
     * @return The repository that interfaces with the settings DataStore
     */
    public SettingsRepo getSettingsRepo() {
        return settingsRepo;
    }

    /**
     * @return A list of colour ints that can be used.
     */
    public ArrayList<Integer> getColorResources() {
        return colorResources;
    }

    /**
     * @param colorResources An array if overriding is needed.
     */
    public void setColorResources(ArrayList<Integer> colorResources) {
        this.colorResources = colorResources;
    }

    /**
     * @return A drawable colour to be used by Views.
     */
    public LiveData<ColorDrawable> getBackgroundColourDrawable() {
        return backgroundColourDrawable;
    }
}
