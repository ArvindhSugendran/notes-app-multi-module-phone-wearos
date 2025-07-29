package com.app.notesappandroidproject.di;

import android.content.Context;

import androidx.room.Room;

import com.app.notesappandroidproject.data.local.AppDatabase;
import com.app.notesappandroidproject.data.local.NoteDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;


/**
 * DatabaseModule
 * <p>
 * This Dagger-Hilt module is responsible for providing dependencies related to database access,
 * specifically setting up the Room database and providing access to DAO instances.
 * <p>
 * This module ensures that database-related dependencies like `AppDatabase` and `NoteDao` are provided
 * as singletons, promoting efficient use of resources and ensuring only one instance exists throughout
 * the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    /**
     * Provides a singleton instance of the AppDatabase.
     * <p>
     * This method sets up the Room database using the application's context. The database is configured
     * with a fallback mechanism to destructive migrations in case of schema changes.
     *
     * @param context The application context required to initialize the database.
     * @return The singleton instance of the AppDatabase.
     */
    @Provides
    @Singleton
    public static AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "note.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * Provides the NoteDao instance.
     * <p>
     * This method fetches the DAO from the provided Room database instance. The DAO serves as the
     * abstraction layer for database operations related to `notes`.
     *
     * @param appDatabase The Room database instance to obtain the DAO from.
     * @return The DAO instance for accessing database operations.
     */
    @Provides
    public static NoteDao provideNoteDao(AppDatabase appDatabase) {
        return appDatabase.noteDao();
    }
}