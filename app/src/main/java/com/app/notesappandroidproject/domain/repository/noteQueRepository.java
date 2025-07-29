package com.app.notesappandroidproject.domain.repository;

import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.domain.question.Question;

import java.util.List;

/**
 * Repository interface that defines the contract for interacting with data sources.
 * This interface abstracts data operations such as CRUD operations for Notes and Questions.
 */
public interface noteQueRepository {

    /**
     * Retrieves all notes from the database.
     * @return List of all notes.
     */
    List<Note> getNotes();

    /**
     * Retrieves a specific subset of notes.
     * @return List of notes for a specific subset or use-case.
     */
    List<Note> getListNote();

    /**
     * Deletes a list of notes from the database.
     * @param notes List of notes to delete.
     */
    void deleteNote(List<Note> notes);

    /**
     * Inserts a new note into the database.
     * @param mNote The note to insert.
     */
    void insertNote(Note mNote);

    /**
     * Inserts a new question into the database.
     * @param mQuestion The question to insert.
     */
    void insertQuestion(Question mQuestion);

    /**
     * Retrieves a question based on the predefined constant ID.
     * @return The retrieved question.
     */
    Question getQuestion();

    /**
     * Retrieves all notes that have been marked as "recycled."
     * @return List of recycled notes.
     */
    List<Note> getRecycledListNote();

    /**
     * Unarchives a note, restoring it to its normal state from a recycled state.
     * @param note The note to unarchive.
     */
    void unArchiveRecycledNote(Note note);

    /**
     * Archives a note, marking it as recycled.
     * @param note The note to archive.
     */
    void archiveNote(Note note);

    /**
     * Synchronizes notes from a Wear OS device to the mobile application database.
     * @param notes List of notes to sync from the Wear OS.
     */
    void syncNotesFromWear(List<Note> notes);

}