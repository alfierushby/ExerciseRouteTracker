package com.example.cw2;

import android.app.Application;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import dagger.hilt.android.HiltAndroidApp;

/**
 * This is an override over the Application class to contain the Repositories that need application-level
 * scope.
 */
@HiltAndroidApp
public class AppState extends Application {
    private final ArrayList<Integer> colorResources = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        // Set global contextual state of possible background colours.
        getColorResources().add(ContextCompat.getColor(getApplicationContext(), R.color.White));
        getColorResources().add(ContextCompat.getColor(getApplicationContext(), R.color.Red));
        getColorResources().add(ContextCompat.getColor(getApplicationContext(), R.color.Green));
    }

    /**
     * Held in the application for context scope.
     * @return Gets the list of colours resources.
     */
    public ArrayList<Integer> getColorResources() {
        return colorResources;
    }
}
