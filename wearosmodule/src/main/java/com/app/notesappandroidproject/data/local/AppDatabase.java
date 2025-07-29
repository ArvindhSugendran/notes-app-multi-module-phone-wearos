package com.app.notesappandroidproject.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * AppDatabase
 * <p>
 * This class represents the Room database for the application. It is the main entry point for managing
 * database operations related to local data storage. The database uses Room as an abstraction layer
 * over SQLite to simplify database interactions.
 * <p>
 * Features:
 * 1. Defines the database schema with entities.
 * 2. Provides an abstraction layer for database access via DAOs (Data Access Objects).
 * 3. Manages database versioning and migrations (currently at version 1).
 * <p>
 * Entities:
 * - NoteEntity: Represents the table structure for storing notes.
 * <p>
 * DAOs:
 * - NoteDao: Provides the data access methods for interacting with the `NoteEntity` table.
 */
@Database(entities = {NoteEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    /**
     * Provides access to the DAO for database operations related to notes.
     *
     * @return An instance of NoteDao for performing database queries.
     */
    public abstract NoteDao noteDao();
}