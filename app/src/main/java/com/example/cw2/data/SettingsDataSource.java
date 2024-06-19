package com.example.cw2.data;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

/**
 * A data source using a DataStore. Not directly used by view models. Use the Repo.
 */
public class SettingsDataSource {
    private RxDataStore<Preferences> dataStore = null;

    /**
     * @param context A context to create the datastore, and is only needed on creation.
     */
    public SettingsDataSource(Context context) {
        this.dataStore = new RxPreferenceDataStoreBuilder(context, "settings").build();
    }

    /**
     * Gets the datastore that's used by the repo.
     * @return The datastore for the settings.
     */
    public RxDataStore<Preferences> getDataStore() {
        return dataStore;
    }
}
