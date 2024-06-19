package com.example.cw2.ContentProvider;


import static com.example.cw2.config.Configuration.DATABASE_NAME;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import android.opengl.Matrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.example.cw2.data.Database;
import com.example.cw2.data.MovementDAO;
import com.example.cw2.data.MovementEntity;
import com.example.cw2.data.ReminderDAO;
import com.example.cw2.data.ReminderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;


/**
 * A {@link ContentProvider} based on a Room database. Exposes querying for reminders.
 */
public class DataContentProvider extends ContentProvider {

    /** The authority of this content provider. */
    public static final String AUTHORITY = "com.example.android.theprovider";

    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        uriMatcher.addURI("com.example.app.provider", "table3", 1);

        uriMatcher.addURI("com.example.app.provider", "table3/#", 2);
    }

    // state the reminderDAO
    private ReminderDAO reminderDAO;


    /**
     * Builds the room database.
     */
    public boolean onCreate() {

        Database database = Room.databaseBuilder(Objects.requireNonNull(getContext()), Database.class, DATABASE_NAME).build();

        // Gets a Data Access Object to perform the database operations
        reminderDAO = database.reminderDAO();

        return true;
    }

    /**
     * Provides integration with room and querying it to return cursors.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Form a cursor that can have the room data transferred in
        String[] columns = new String[] { "id","name","radius","date","latitude", "longitude"};
        MatrixCursor c = new MatrixCursor(columns);
        switch (uriMatcher.match(uri)) {
            // For all of the rows
            case 1:
                List<ReminderEntity> es = reminderDAO.getAllDirect().blockingGet();
                for(ReminderEntity e : es){
                    c.addRow(new Object[] {e.eid,e.name,e.radius,e.date,e.latitude,e.longitude});
                }
                c.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
                return c;
            // For a certain row
            case 2:
                ReminderEntity e =  reminderDAO.get((int) ContentUris.parseId(uri)).blockingGet();
                c.addRow(new Object[] {e.eid,e.name,e.radius,e.date,e.latitude,e.longitude});
                c.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
                return c;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}
