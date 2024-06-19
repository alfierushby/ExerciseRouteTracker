package com.example.cw2.data;

import androidx.room.TypeConverter;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Converts the list of positions to json and viceversa.
 */
public class Converters {
    /**
     * Converts json to its list form.
     * @param value A json list in string value
     * @return A list of LatLngs, positions
     */
    @TypeConverter
    public static List<LatLng> toList(String value) {
        Type listType = new TypeToken<List<LatLng>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    /**
     * Converts lists to json.
     * @param list A list of LatLngs
     * @return A string of json serialised.
     */
    @TypeConverter
    public static String fromArrayList(List<LatLng> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}