package com.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;


import com.example.cw2.data.Database;
import com.example.cw2.data.MovementDAO;
import com.example.cw2.data.MovementEntity;
import com.example.cw2.data.ReminderDAO;
import com.example.cw2.data.ReminderEntity;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Unit tests for the Room Database and DAO operations.
 */
public class RoomDatabaseTests {
    private ReminderDAO reminderDAO;
    private MovementDAO movementDAO;
    private Database db;

    /**
     * Setup method to create an in-memory Room database and DAO instances.
     */
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, Database.class).build();
        reminderDAO = db.reminderDAO();
        movementDAO = db.movementDAO();
    }

    /**
     * Cleanup method to close the Room database after tests.
     * @throws IOException if an I/O error occurs
     */
    @After
    public void closeDb() throws IOException {
        db.close();
    }

    /**
     * Test for inserting and reading ReminderEntity from the Room database.
     * @throws Exception if an exception occurs
     */
    @Test
    public void insertAndRead() throws Exception {
        ReminderEntity entity = new ReminderEntity();
        entity.name = "tester";
        reminderDAO.insertAll(entity).blockingAwait();
        List<ReminderEntity> byName = reminderDAO.getAllDirect().blockingGet();
        assert (Objects.equals(byName.get(0).name, entity.name));
    }

    /**
     * Test for editing and reading latitude and longitude of ReminderEntity in the Room database.
     * @throws Exception if an exception occurs
     */
    @Test
    public void editAndReadLatLon() throws Exception {
        insertAndRead();
        ReminderEntity entity = reminderDAO.getAllDirect().blockingGet().get(0);
        reminderDAO.updateReminders(55.4,21.1254,entity.eid).blockingAwait();
        List<ReminderEntity> byName = reminderDAO.getAllDirect().blockingGet();
        assert Objects.equals(byName.get(0).radius, entity.radius);
        assert Objects.equals(byName.get(0).name, entity.name);
        assert !Objects.equals(byName.get(0).latitude, entity.latitude);
        assert !Objects.equals(byName.get(0).longitude, entity.longitude);
        assert Objects.equals(byName.get(0).latitude, 21.1254);
        assert Objects.equals(byName.get(0).longitude, 55.4);
    }

    /**
     * Test for editing and reading radius and name of ReminderEntity in the Room database.
     * @throws Exception if an exception occurs
     */
    @Test
    public void editAndReadRadNam() throws Exception {
        insertAndRead();
        ReminderEntity entity = reminderDAO.getAllDirect().blockingGet().get(0);
        reminderDAO.updateReminders(55,"Jay",entity.eid).blockingAwait();
        List<ReminderEntity> byName = reminderDAO.getAllDirect().blockingGet();
        assert !Objects.equals(byName.get(0).radius, entity.radius);
        assert !Objects.equals(byName.get(0).name, entity.name);
        assert Objects.equals(byName.get(0).radius, 55);
        assert Objects.equals(byName.get(0).name, "Jay");
        assert Objects.equals(byName.get(0).latitude, entity.latitude);
        assert Objects.equals(byName.get(0).longitude, entity.longitude);
    }

    /**
     * Test for inserting and reading MovementEntity from the Room database.
     * @throws Exception if an exception occurs
     */
    @Test
    public void movementInsertAndRead() throws Exception {
        MovementEntity entity = new MovementEntity();
        entity.name = "tester";
        movementDAO.insertAll(entity).blockingAwait();
        List<MovementEntity> byName = movementDAO.getAllDirect().blockingGet();
        assert (Objects.equals(byName.get(0).name, entity.name));
    }

    /**
     * Test for editing type and name of MovementEntity in the Room database.
     * @throws Exception if an exception occurs
     */
    @Test
    public void movementEditPure() throws Exception {
        movementInsertAndRead();
        MovementEntity entity = movementDAO.getAllDirect().blockingGet().get(0);
        movementDAO.updateMovements(1, "A run",entity.mid).blockingAwait();
        List<MovementEntity> byName = movementDAO.getAllDirect().blockingGet();
        assert !Objects.equals(byName.get(0).type, entity.type);
        assert !Objects.equals(byName.get(0).name, entity.name);
        assert Objects.equals(byName.get(0).type, 1);
        assert Objects.equals(byName.get(0).name, "A run");
        assert Objects.equals(byName.get(0).date, entity.date);
        assert Objects.equals(byName.get(0).points, entity.points);
    }

    /**
     * Test for editing points of MovementEntity in the Room database.
     * @throws Exception if an exception occurs
     */
    @Test
    public void movementEditPoints() throws Exception {
        movementInsertAndRead();
        MovementEntity entity = movementDAO.getAllDirect().blockingGet().get(0);
        ArrayList<LatLng> points = new ArrayList<>();
        points.add(new LatLng(10.5,5.5));
        movementDAO.updatePoints(points,entity.mid).blockingAwait();
        List<MovementEntity> byName = movementDAO.getAllDirect().blockingGet();
        assert Objects.equals(byName.get(0).type, entity.type);
        assert Objects.equals(byName.get(0).name, entity.name);
        assert Objects.equals(byName.get(0).date, entity.date);
        assert !Objects.equals(byName.get(0).points, entity.points);
        assert Objects.equals(byName.get(0).points.get(0).latitude, 10.5);
        assert Objects.equals(byName.get(0).points.get(0).longitude, 5.5);
    }
}