package com.example.cw2.repos;

import static com.example.cw2.config.Configuration.DATABASE_NAME;

import android.content.Context;

import androidx.room.Room;

import com.example.cw2.data.Database;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * A module of all repositories that Hilt will use to provide singleton repos to the app.
 * It is scoped to the application.
 */
@Module
@InstallIn(SingletonComponent.class)
public interface RepositoryModule {
    /**
     * Provides the SettingRepo implementation whenever injecting.
     */
    @Binds
    public SettingsRepo provideSettingsRepo(SettingsRepoImpl repo);

    /**
     * Provides the ReminderRepo implementation whenever injecting.
     */
    @Binds
    public ReminderRepo provideReminderRepo(ReminderRepoImpl repo);

    /**
     * Provides the MovementRepo implementation whenever injecting.
     */
    @Binds
    public MovementRepo provideMovementRepo(MovementRepoImpl repo);

    /**
     * Provides the MovementRepo implementation whenever injecting.
     */
    @Binds
    public StateRepo provideStateRepo(StateRepoImpl repo);

    /**
     * This is set as a singleton, so it is only executed once and then injected everytime.
     * @param c A given context through hilt
     * @return The database Hilt uses.
     */
    @Provides
    @Singleton
    static Database provideDatabase(@ApplicationContext Context c){
        return Room.databaseBuilder(c,Database.class,DATABASE_NAME).build();
    }
}
