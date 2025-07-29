package com.app.notesappandroidproject.data.repository;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.notesappandroidproject.utils.AppExecutors;
import com.app.notesappandroidproject.data.local.NoteDao;
import com.app.notesappandroidproject.data.local.NoteEntity;
import com.app.notesappandroidproject.data.local.NoteMapper;
import com.app.notesappandroidproject.domain.repository.NoteRepository;
import com.app.notesappandroidproject.presentation.Note;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * NoteRepositoryImpl
 * <p>
 * This class serves as the repository implementation for managing note-related database operations.
 * It acts as a mediator between the database (via NoteDao) and the application logic,
 * ensuring data is processed and exposed efficiently through LiveData while adhering to proper threading principles.
 * <p>
 * Features:
 * 1. Load and fetch non-recycled notes.
 * 2. Archive and unarchive notes as required.
 * 3. Insert or update notes in a safe and background-executed manner.
 * 4. Handle synchronization with incoming note lists from other sources (e.g., mobile devices).
 * 5. Delete unnecessary notes during synchronization.
 * 6. Communicate synchronization status using broadcasts.
 * <p>
 * Responsibilities:
 * - Map database entities to presentation models and vice versa.
 * - Expose non-recycled notes as LiveData for observation by UI components.
 * - Perform database operations in a background thread using AppExecutors.
 */
public class NoteRepositoryImpl implements NoteRepository {

    private final NoteDao noteDao;
    private final Context context;
    private final MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>();

    /**
     * Constructor for injecting dependencies.
     *
     * @param noteDao  The DAO for database operations related to notes.
     * @param context  The application context used for broadcasting intents.
     */
    @Inject
    public NoteRepositoryImpl(NoteDao noteDao, Context context) {
        this.noteDao = noteDao;
        this.context = context;
    }

    /**
     * Loads non-recycled notes from the database and updates the LiveData object for observation.
     * Data is fetched in a background thread to ensure UI thread remains responsive.
     */
    public void loadNotes() {
        Log.d("WEAR_SYNC", "loadNotes() called");
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<NoteEntity> notes = noteDao.getListNote();
            Log.d("WEAR_SYNC", "Fetched Notes from DB, Size: " + notes.size()); // Log after fetching notes

            notesLiveData.postValue(NoteMapper.mapToPresentationList(notes));
            Log.d("WEAR_SYNC", "NotesLiveData Updated, Size: " + NoteMapper.mapToPresentationList(notes).size()); // Log after updating LiveData
        });
    }

    /**
     * Exposes non-recycled notes from the database wrapped as LiveData for UI observation.
     *
     * @return LiveData of notes mapped to presentation models for UI consumption.
     */
    @Override
    public LiveData<List<Note>> getListNote() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<NoteEntity> notes = noteDao.getListNote();
            notesLiveData.postValue(NoteMapper.mapToPresentationList(notes));
        });

        return notesLiveData;
    }

    /**
     * Archives a note by setting its `is_recycled` field to 1 in the database.
     *
     * @param noteId ID of the note to archive.
     */
    public void archiveSyncingNotes(long noteId) {
        AppExecutors.getInstance().diskIO().execute(() -> noteDao.archiveNote(noteId));
    }

    /**
     * Unarchive a note by setting its `is_recycled` field to 0 in the database.
     *
     * @param noteId ID of the note to unarchive.
     */
    public void unArchiveSyncingNotes(long noteId) {
        AppExecutors.getInstance().diskIO().execute(() -> noteDao.unarchiveRecycledNote(noteId));
    }

    /**
     * Inserts or updates a note into the database safely.
     *
     * @param note Note to insert or update.
     */
    @Override
    public void insertNote(Note note) {
        AppExecutors.getInstance().diskIO().execute(() -> noteDao.upsertNote(NoteMapper.mapToEntity(note)));
    }

    /**
     * Inserts or updates a note by directly passing a NoteEntity to the database.
     *
     * @param note NoteEntity to upsert.
     */
    @Override
    public void upsertNote(NoteEntity note) {
        AppExecutors.getInstance().diskIO().execute(() -> noteDao.upsertNote(note));
    }

    /**
     * Synchronizes notes by comparing incoming note data with local data,
     * deletes outdated notes, archives/unarchive as needed, and updates local changes.
     * Broadcasts completion using LocalBroadcastManager.
     *
     * @param incomingNotes List of incoming notes from external sources (e.g., mobile sync).
     */
    @Override
    public void syncNotesFromMobile(List<NoteEntity> incomingNotes) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<NoteEntity> localNotes = noteDao.getNotes();

            if (incomingNotes.size() < localNotes.size()) {
                Log.d("WEAR_SYNC", "TIME TO DELETE NOTES");

                Set<Long> incomingIds = incomingNotes.stream()
                        .map(NoteEntity::getId)
                        .collect(Collectors.toSet());

                // Get the notes to remove from the database
                List<NoteEntity> notesToDelete = localNotes.stream()
                        .filter(localNote -> !incomingIds.contains(localNote.getId()))
                        .collect(Collectors.toList());

                noteDao.deleteNote(notesToDelete);
                Log.d("WEAR_SYNC", "Notes Deleted ");

            } else {
                Log.d("WEAR_SYNC", "OLD ITEM SIZE: " + localNotes.size());
                for (NoteEntity incomingNote : incomingNotes) {
                    NoteEntity existingNote = findNoteById(localNotes, incomingNote.id);

                    if (existingNote != null) {

                        if (incomingNote.isRecycled && !existingNote.isRecycled) {
                            archiveSyncingNotes(existingNote.id);
                            Log.d("WEAR_SYNC", "The note has been archived " + incomingNote.id);
                        }

                        if (!incomingNote.isRecycled && existingNote.isRecycled) {
                            unArchiveSyncingNotes(existingNote.id);
                            Log.d("WEAR_SYNC", "The note has been unarchived " + incomingNote.id);
                        }

                        if (incomingNote.dateUpdate > existingNote.dateUpdate) {
                            upsertNote(incomingNote);
                            Log.d("WEAR_SYNC", "Updated note with id: " + incomingNote.id);
                        }
                    } else {
                        upsertNote(incomingNote);
                        Log.d("WEAR_SYNC", "Inserted new note with id: " + incomingNote.id);
                    }
                }
            }

            // Notify that sync is complete
            Intent broadcastIntent = new Intent("SYNC_COMPLETED_ACTION");
            broadcastIntent.putExtra("status", "success");
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        });
    }

    /**
     * Searches for a note by its ID in a local list of notes.
     *
     * @param localNotes List of local notes to search in.
     * @param noteId     The note's ID to find.
     * @return The found NoteEntity, or null if not found.
     */
    private NoteEntity findNoteById(List<NoteEntity> localNotes, long noteId) {
        for (NoteEntity note : localNotes) {
            if (note.id == noteId) {
                return note;
            }
        }
        return null;
    }
}


