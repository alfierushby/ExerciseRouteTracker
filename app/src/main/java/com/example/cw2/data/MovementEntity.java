package com.example.cw2.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Entity for Routes.
 */
@Entity
public class MovementEntity {

        @PrimaryKey(autoGenerate = true)
        public int mid;

        @ColumnInfo(name = "name")
        public String name;

        @ColumnInfo(name = "type")
        public Integer type;

        @ColumnInfo(name = "date")
        public Long date;
        @ColumnInfo(name = "points")
        public List<LatLng> points;


}
