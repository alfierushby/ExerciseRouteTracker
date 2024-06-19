package com.example.cw2.repos;

import static com.example.cw2.config.Configuration.DEFAULT_COLOUR;
import static com.example.cw2.config.Configuration.LOG_TAG;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;

import com.example.cw2.data.SettingsDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * Singleton Setting's Repo that handles interfacing with the Setting's DataStore source.
 * This class is not directly used, instead the interface is.
 */

@Singleton
public class SettingsRepoImpl implements SettingsRepo {

    private SettingsDataSource dataSource;
    private Preferences.Key<Integer> background = PreferencesKeys.intKey("Background Colour");
    private Preferences.Key<Integer> mId = PreferencesKeys.intKey("mId");
    private Preferences.Key<Integer> eId = PreferencesKeys.intKey("eId");
    private Preferences.Key<Boolean> editing = PreferencesKeys.booleanKey("Editing");

    /**
     * Note that this repo is managed in a Hilt container, and injected directly to viewmodels when needed.
     * It is not normal to directly instantiate this class.
     * @param c Context, provided from Hilt using the application context
     */
    @Inject
    public SettingsRepoImpl(@ApplicationContext Context c){
        setDataSource(new SettingsDataSource(c));
        intializeDataSource();
        Log.d(LOG_TAG,"Hi there");
    }

    /**
     * This should be called on repository creation to verify the datasource contains the correct defaults.
     */
    private void intializeDataSource(){
        getDataSource().getDataStore().updateDataAsync(prefsIn -> {
            // Get a mutable preference to allow editing of the data
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            Integer currentColour = prefsIn.get(getBackgroundKey());
            if(currentColour == null)
                // Set the background colour to the default colour if it is null
                mutablePreferences.set(getBackgroundKey(), DEFAULT_COLOUR);
            return Single.just(mutablePreferences);
        });
    }


    /**
     * @return The Data the repo exposes
     */
    private SettingsDataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource datastore to be set in init.
     */
    private void setDataSource(SettingsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return The key corresponding to the background colour in the DataStore.
     */
    private Preferences.Key<Integer> getBackgroundKey() {
        return background;
    }

    @Override
    public void setBackgroundColour(int colour){
        getDataSource().getDataStore().updateDataAsync(prefsIn -> {
            // Get a mutable preference to allow editing of the data
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            // Set the background colour to the input.
            mutablePreferences.set(getBackgroundKey(), colour);
            return Single.just(mutablePreferences);
        });
    }

    @Override
    public Flowable<Integer> getBackgroundColour(){
       return getDataSource().getDataStore().data().map(p -> p.get(getBackgroundKey())).distinctUntilChanged();
    }
}
