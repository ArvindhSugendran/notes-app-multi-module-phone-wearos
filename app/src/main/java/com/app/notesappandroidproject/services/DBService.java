package com.app.notesappandroidproject.services;

import android.content.Context;

import com.app.notesappandroidproject.data.dataSource.Injection;
import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.domain.question.Question;
import com.app.notesappandroidproject.domain.repository.noteQueRepository;

import java.util.List;

/**
 * DBService
 * <p>
 * This class serves as the central service for database interactions related to notes and questions.
 * It abstracts database operations by communicating with the underlying `noteQueRepository`.
 * This service is responsible for performing CRUD operations for both notes and question-related logic.
 * <p>
 * Features:
 * 1. Fetch all notes from the database.
 * 2. Fetch a specific filtered list of notes.
 * 3. Delete multiple notes and handle database persistence.
 * 4. Insert new notes or questions into the database.
 * 5. Synchronize notes data from wearable devices to the main database.
 * 6. Restore notes from the Recycle Bin.
 * 7. Archive or unarchive specific notes from a normal archive list.
 * <p>
 * Dependencies:
 * - `noteQueRepository`: Handles direct database interaction logic.
 * - `Injection`: Provides the data source for database repository initialization.
 * - `Note`: Represents the domain model for notes.
 * - `Question`: Represents the domain model for user queries or related input.
 */
public class DBService {

    private final noteQueRepository noteQueRepository;

    /**
     * DBService Constructor
     * <p>
     * Initializes the `noteQueRepository` using dependency injection via `Injection.providerNoteDataSource`.
     * This ensures that the repository is properly initialized with the provided application context.
     *
     * @param context The application context for initializing the repository.
     */
    public DBService(Context context) {
        noteQueRepository = Injection.providerNoteDataSource(context);
    }

    /**
     * getNotes
     * <p>
     * Fetches all notes from the database through the `noteQueRepository`.
     *
     * @return List of all notes available in the database.
     */
    public List<Note> getNotes() {
        return noteQueRepository.getNotes();
    }

    /**
     * getListNote
     * <p>
     * Fetches a filtered or specific subset of notes from the database.
     *
     * @return List of notes as per filtering criteria or logic applied by the repository.
     */
    public List<Note> getListNote() {
        return noteQueRepository.getListNote();
    }

    /**
     * deleteNote
     * <p>
     * Deletes a list of notes from the database by delegating to the repository.
     * This enables the user to permanently remove notes from the database.
     *
     * @param notes List of notes to delete.
     */
    public void deleteNote(List<Note> notes) {
        noteQueRepository.deleteNote(notes);
    }

    /**
     * insertNote
     * <p>
     * Inserts a single note into the database.
     * Used for creating new notes or saving changes made by the user.
     *
     * @param mNote The note object to insert into the database.
     */
    public void insertNote(Note mNote) {
        noteQueRepository.insertNote(mNote);
    }

    /**
     * insertQuestion
     * <p>
     * Inserts a question into the database via the repository.
     * This allows storing and retrieving questions for user interactions or other logic.
     *
     * @param mQuestion The question to save to the database.
     */
    public void insertQuestion(Question mQuestion) {
        noteQueRepository.insertQuestion(mQuestion);
    }


    /**
     * getQuestion
     * <p>
     * Retrieves a question from the database using the repository logic.
     *
     * @return The `Question` object representing a saved or queried user question.
     */
    public Question getQuestion() {
        return noteQueRepository.getQuestion();
    }

    /**
     * syncNotesFromWear
     * <p>
     * Synchronizes notes data received from wearable devices with the main database.
     * Useful for ensuring consistency between mobile and wearable platforms.
     *
     * @param notes List of notes received from a wearable device.
     */
    public void syncNotesFromWear(List<Note> notes) {
        noteQueRepository.syncNotesFromWear(notes);
    }

    /**
     * restoreNotes
     * <p>
     * Restores notes from the Recycle Bin by unarchiving them using the repository logic.
     * Each note is processed individually to restore its state in the database.
     *
     * @param notes List of notes to restore from the Recycle Bin.
     */
    public void restoreNotes(List<Note> notes) {
        for (int i = 0; i < notes.size(); i++) {
            noteQueRepository.unArchiveRecycledNote(notes.get(i));
        }
    }


    /**
     * getRecycledNotesList
     * <p>
     * Retrieves all notes currently in the Recycle Bin from the database.
     *
     * @return List of notes in the Recycle Bin (recycled/archived state).
     */
    public List<Note> getRecycledNotesList() {
        return noteQueRepository.getRecycledListNote();
    }

    /**
     * archiveNormalNote
     * <p>
     * Archives a list of notes to the database using the repository's archiving logic.
     * Allows a user to move notes to a different storage state for later use or organization.
     *
     * @param notes List of notes to archive.
     */
    public void archiveNormalNote(List<Note> notes) {
        for (int i = 0; i < notes.size(); i++) {
            noteQueRepository.archiveNote(notes.get(i));
        }
    }

}