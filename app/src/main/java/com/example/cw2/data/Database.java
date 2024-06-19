package com.example.cw2.data;

import androidx.room.*;
import androidx.room.RoomDatabase;

/**
 * A database that contains two tables (DAOS).
 */
@androidx.room.Database(version = 2, entities = {ReminderEntity.class, MovementEntity.class},  autoMigrations = {@AutoMigration (from =1, to = 2)} )
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {
    public abstract ReminderDAO reminderDAO();
    public abstract MovementDAO movementDAO();
}
