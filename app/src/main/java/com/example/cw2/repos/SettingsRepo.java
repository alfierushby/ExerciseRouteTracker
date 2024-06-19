package com.example.cw2.repos;

import io.reactivex.rxjava3.core.Flowable;

/**
 * Defines the functions the SettingsRepo uses, to separate logic.
 * This defines the interfaces of what everything uses, so all functions to be exposed are defined here.
 */
public interface SettingsRepo {
     /**
      * @return The background colour of the app
      */
     Flowable<Integer> getBackgroundColour();

     /**
      * @param colour The colour the app is set to.
      * Is an Int which corresponds to an index in a colour resource list.
      */
     void setBackgroundColour(int colour);
}
