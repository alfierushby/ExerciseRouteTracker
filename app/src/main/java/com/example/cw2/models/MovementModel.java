package com.example.cw2.models;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * A business model that represents a movement.
 */
public class MovementModel {
    private final int mid;
    private final String name;
    private final Integer type;
    private final Long date;
    private final List<LatLng> points;

    public MovementModel(int mid, String name, Integer type, Long date, List<LatLng> points) {
        this.mid = mid;
        this.name = name;
        this.type = type;
        this.date = date;
        this.points = points;
    }

    /**
     * @return The type of the Movement.
     */
    public Integer getType() {
        return type;
    }

    /**
     * @return Id of the movement
     */
    public int getMid() {
        return mid;
    }

    /**
     * @return Name of the movement
     */
    public String getName() {
        return name;
    }

    /**
     * @return Date the movement was created.
     */
    public Long getDate() {
        return date;
    }

    /**
     * @return All the position points represent the route of the movement
     */
    public List<LatLng> getPoints() {
        return points;
    }
}
