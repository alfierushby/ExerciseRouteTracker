package com.example.cw2;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Events {
    public static class FinishEvent{}
    public static class DeleteReminder{
        public int eId = 0;
    }

    /**
     * Used to open the Movement Creator View
     */
    public static class OpenMovementView{ }

    /**
     * Used to open the Reminder Creator View
     */
    public static class OpenReminderView{ }

    /**
     * Used to call for a new point on the route
     */
    public static class NewPoint{
        public List<LatLng> points;
    }
}
