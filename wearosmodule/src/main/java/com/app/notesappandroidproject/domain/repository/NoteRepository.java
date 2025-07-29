package com.app.notesappandroidproject.domain.repository;

import androidx.lifecycle.LiveData;

import com.app.notesappandroidproject.data.local.NoteEntity;
import com.app.notesappandroidproject.presentation.Note;

import java.util.List;

/**
 * NoteRepository
 * <p>
 * This interface defines the contract for the repository layer in the application. It acts as
 * an abstraction for data operations related to notes, decoupling the data source from the
 * business logic and UI layers.
 * </p>
 * The repository pattern provides methods to handle data operations like fetching, inserting,
 * updating, and deleting notes.
 */
public interface NoteRepository {

    /**
     * Fetches a live data stream of all notes in the repository.
     * <p>
     * This allows observing changes to the list of notes in real-time, ensuring the UI updates
     * automatically when data changes.
     *
     * @return A LiveData object containing a list of notes.
     */
    LiveData<List<Note>> getListNote();

    /**
     * Inserts a single note into the repository.
     * <p>
     * This allows adding new notes to the database or other storage mechanisms.
     *
     * @param mNote The note to insert into the repository.
     */
    void insertNote(Note mNote);

    /**
     * Performs an upsert operation (update if exists, insert otherwise) for a note.
     * <p>
     * This method ensures that the latest state of the note is stored in the database.
     *
     * @param mNote The note entity to upsert into the repository.
     */
    void upsertNote(NoteEntity mNote);

    /**
     * Synchronizes a list of notes from the mobile application to the repository.
     * <p>
     * This operation ensures data consistency, typically for syncing changes or restoring notes
     * from mobile storage to the server or database.
     *
     * @param notes A list of notes to sync from the mobile context.
     */
    void syncNotesFromMobile(List<NoteEntity> notes);
}