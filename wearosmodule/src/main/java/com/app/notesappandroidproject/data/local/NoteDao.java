package com.app.notesappandroidproject.data.local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

/**
 * NoteDao
 * <p>
 * This interface serves as the Data Access Object (DAO) for database operations related to notes.
 * It provides methods to interact with the `notes` table in the Room database. DAOs act as the
 * abstraction layer for database operations, allowing for cleaner and more maintainable code.
 * <p>
 * Features:
 * 1. Fetch all notes from the database, ordered by their IDs in descending order.
 * 2. Fetch non-recycled notes from the database, ordered by their IDs in descending order.
 * 3. Delete specific notes from the database.
 * 4. Insert or update a note using the `Upsert` operation.
 * 5. Archive and unarchive notes by updating the `is_recycled` status of a specific note by its ID.
 * <p>
 * Methods:
 * - `getNotes()`: Retrieves all notes, sorted by their IDs.
 * - `getListNote()`: Retrieves only non-recycled notes, sorted by their IDs.
 * - `deleteNote()`: Deletes selected notes from the database.
 * - `upsertNote()`: Inserts or updates a note depending on its existence.
 * - `unarchiveRecycledNote()`: Unarchive a note by setting its `is_recycled` flag to 0.
 * - `archiveNote()`: Archives a note by setting its `is_recycled` flag to 1.
 */
@Dao
public interface NoteDao {

    /**
     * Fetches all notes from the database, ordered by their IDs in descending order.
     *
     * @return A list of all notes.
     */
    @Query("SELECT * FROM notes ORDER BY noteId DESC")
    List<NoteEntity> getNotes();

    /**
     * Fetches all non-recycled notes from the database, ordered by their IDs in descending order.
     *
     * @return A list of non-recycled notes.
     */
    @Query("SELECT * FROM notes  where is_recycled = 0 ORDER BY noteId DESC")
    List<NoteEntity> getListNote();

    /**
     * Deletes a list of notes from the database.
     *
     * @param notes The list of notes to delete.
     */
    @Delete
    void deleteNote(List<NoteEntity> notes);

    /**
     * Inserts or updates a note into the database using the Upsert operation.
     *
     * @param noteEntity The note entity to insert or update.
     */
    @Upsert
    void upsertNote(NoteEntity noteEntity);

    /**
     * Unarchive a note by setting the `is_recycled` flag to 0 for the provided note ID.
     *
     * @param id The ID of the note to unarchive.
     */
    @Query("Update notes set is_recycled = 0 where noteId =:id")
    void unarchiveRecycledNote(long id);

    /**
     * Archives a note by setting the `is_recycled` flag to 1 for the provided note ID.
     *
     * @param id The ID of the note to archive.
     */
    @Query("Update notes set is_recycled = 1 where noteId =:id")
    void archiveNote(long id);
}

