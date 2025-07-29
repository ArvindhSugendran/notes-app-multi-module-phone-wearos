package com.app.notesappandroidproject.di;

import android.content.Context;

import com.app.notesappandroidproject.data.local.AppDatabase;
import com.app.notesappandroidproject.data.repository.NoteRepositoryImpl;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;


/**
 * RepositoryModule
 * <p>
 * This Dagger-Hilt module is responsible for providing the repository instance that abstracts
 * the data access logic. It sets up the `NoteRepositoryImpl` as part of the dependency graph.
 * </p>
 * The repository pattern provides a clean API for data access and decouples the data layer
 * from the rest of the application logic.
 */
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    /**
     * Provides a singleton instance of `NoteRepositoryImpl`.
     * <p>
     * This method creates and provides an instance of the `NoteRepositoryImpl` class. It requires
     * the `AppDatabase` and application context to initialize and interact with database operations.
     *
     * @param appDatabase The Room database instance required for database access.
     * @param context The application context for performing any context-dependent operations.
     * @return The instance of NoteRepositoryImpl responsible for handling data logic.
     */
    @Provides
    public static NoteRepositoryImpl provideNoteRepository(AppDatabase appDatabase, @ApplicationContext Context context) {
        return new NoteRepositoryImpl(appDatabase.noteDao(), context);
    }
}