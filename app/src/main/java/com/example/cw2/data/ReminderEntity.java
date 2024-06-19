package com.example.cw2.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDate;
import java.util.Date;

/**
 * An entity that interfaces with the DAO.
 */
@Entity
public class ReminderEntity {
    @PrimaryKey(autoGenerate = true)
    public int eid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "radius")
    public int radius;

    @ColumnInfo(name = "date")
    public Long date;

    @ColumnInfo(name = "latitude")
    public Double latitude;

    @ColumnInfo(name = "longitude")
    public Double longitude;

}
