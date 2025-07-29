package com.app.notesappandroidproject.sync;

import android.content.Context;

import com.app.notesappandroidproject.data.local.NoteMapper;
import com.app.notesappandroidproject.data.repository.NoteRepositoryImpl;
import com.app.notesappandroidproject.presentation.Note;

import java.util.List;

import javax.inject.Inject;

/**
 * WearSyncService acts as a bridge between the wearable service logic and data synchronization.
 * It uses SyncManager to handle communication and converts notes into the appropriate format for syncing.
 */
public class WearSyncService {

    /**
     * Constructor initializes the WearSyncService with a context and creates a SyncManager instance.
     * @param context The application context used to initialize the SyncManager
     */
    private SyncManager syncManager;

    public WearSyncService(Context context) {
        this.syncManager = new SyncManager(context);
    }

    /**
     * Synchronizes notes from the wearable to the mobile app by converting data from presentation layer
     * to local database entities and sending them via SyncManager.
     * @param notes List of Note objects to sync
     */
    public void syncNotes(List<Note> notes) {
        syncManager.syncNotesToMobileApp(NoteMapper.mapToEntityList(notes));
    }
}
