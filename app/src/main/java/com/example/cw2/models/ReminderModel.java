package com.example.cw2.models;

/**
 * A business model that represents a reminder.
 */
public class ReminderModel {
    private final int eid;
    private final String name;
    private final int radius;
    private final Long date;
    private final Double latitude;
    private final Double longitude;

    public ReminderModel(int eid, String name, int radius, Long date, Double latitude, Double longitude) {
        this.eid = eid;
        this.name = name;
        this.radius = radius;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return Id of the reminder
     */
    public int getEid() {
        return eid;
    }

    /**
     * @return Name of the reminder
     */
    public String getName() {
        return name;
    }

    /**
     * @return Radius of the reminder in the map
     */
    public int getRadius() {
        return radius;
    }

    /**
     * @return Date the reminder was made
     */
    public Long getDate() {
        return date;
    }

    /**
     * @return Latitude of the reminder point
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @return Longitude of the reminder point
     */
    public Double getLongitude() {
        return longitude;
    }
}
