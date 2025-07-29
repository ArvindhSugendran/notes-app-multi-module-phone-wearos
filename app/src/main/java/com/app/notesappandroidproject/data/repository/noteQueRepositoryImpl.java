package com.app.notesappandroidproject.data.repository;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.notesappandroidproject.utils.AppExecutors;
import com.app.notesappandroidproject.data.dataSource.noteQueDao;
import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.domain.question.Question;
import com.app.notesappandroidproject.domain.repository.noteQueRepository;

import java.util.List;

/**
 * Implementation of the noteQueRepository interface.
 * <p>
 * This class serves as the repository layer that manages interactions between
 * database operations (via DAO) and business logic, specifically for notes and questions.
 * <p>
 * Responsibilities:
 * - Handle database operations like insertion, deletion, retrieval, and synchronization.
 * - Manage communication with Wear OS by sending local broadcasts using LocalBroadcastManager.
 * - Use AppExecutors to ensure database operations are executed on a background thread.
 * <p>
 * Key Features:
 * - Retrieve notes and list of notes from the database.
 * - Insert, delete, archive, and unarchive notes.
 * - Handle communication with Wear OS via broadcasts upon events like syncing, archiving, or deleting notes.
 * - Synchronize data between Wear OS and the local database.
 */
public class noteQueRepositoryImpl implements noteQueRepository {

    private final noteQueDao mNoteQueDao;

    private final Context context;

    /**
     * Constructor for initializing the Repository with DAO and application context.
     *
     * @param mNoteQueDao DAO instance for database interactions related to notes and questions.
     * @param context     Application context for managing local broadcasts.
     */
    public noteQueRepositoryImpl(noteQueDao mNoteQueDao, Context context) {
        this.mNoteQueDao = mNoteQueDao;
        this.context = context;
    }

    // Retrieves all notes from the database.
    @Override
    public List<Note> getNotes() {
        return mNoteQueDao.getNotes();
    }

    // Retrieves a specific list of notes from the database.
    @Override
    public List<Note> getListNote() {
        return mNoteQueDao.getListNote();
    }

    // Deletes notes in the background and broadcasts the deletion event.
    @Override
    public void deleteNote(List<Note> notes) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            mNoteQueDao.deleteNote(notes);

            // Send broadcast after archiving
            Intent intent = new Intent("ACTION_NOTE_DELETED");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
    }

    // Inserts a note into the database.
    @Override
    public void insertNote(Note mNote) {
        mNoteQueDao.insertNote(mNote);
    }

    // Inserts a question into the database.
    @Override
    public void insertQuestion(Question mQuestion) {
        mNoteQueDao.insertQuestion(mQuestion);
    }

    // Retrieves a question from the database by a predefined ID.
    @Override
    public Question getQuestion() {
        return mNoteQueDao.getQuestion(Question.ID_FINAL);
    }

    // Retrieves notes marked for recycling from the database.
    @Override
    public List<Note> getRecycledListNote() {
        return mNoteQueDao.getRecycledListNote();
    }

    // Unarchived a note and sends a broadcast indicating the operation.
    @Override
    public void unArchiveRecycledNote(Note note) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            mNoteQueDao.unarchieveRecycledNote(note.id);

            // Send broadcast after archiving
            Intent intent = new Intent("ACTION_NOTE_UNARCHIVED");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
    }

    // Archives a note and sends a broadcast indicating the operation.
    @Override
    public void archiveNote(Note note) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            mNoteQueDao.archiveNote(note.id);

            // Send broadcast after archiving
            Intent intent = new Intent("ACTION_NOTE_ARCHIVED");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
    }

    // Synchronizes notes coming from Wear OS with the local database.
    @Override
    public void syncNotesFromWear(List<Note> incomingNotes) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Note> localNotes = mNoteQueDao.getNotes();

            Log.d("MOBILE_SYNC", "OLD ITEM SIZE: " + localNotes.size());
            for (Note incomingNote : incomingNotes) {
                Note existingNote = findNoteById(localNotes, incomingNote.id);

                if (existingNote != null) {
                    if (incomingNote.dateUpdate > existingNote.dateUpdate) {
                        insertNote(incomingNote);
                        Log.d("MOBILE_SYNC", "Updated note with id: " + incomingNote.id);
                    }
                } else {
                    insertNote(incomingNote);
                    Log.d("MOBILE_SYNC", "Inserted new note with id: " + incomingNote.id);
                }
            }

            // Notify that sync is complete
            Intent broadcastIntent = new Intent("SYNC_COMPLETED_ACTION");
            broadcastIntent.putExtra("status", "success");
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        });
    }

    /**
     * Helper method to find a note by ID in a given list of notes.
     *
     * @param localNotes List of local notes to search.
     * @param noteId     ID of the note to find.
     * @return The found note or null if not found.
     */
    private Note findNoteById(List<Note> localNotes, long noteId) {
        for (Note note : localNotes) {
            if (note.id == noteId) {
                return note;
            }
        }
        return null;
    }
}