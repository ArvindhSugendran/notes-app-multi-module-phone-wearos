package com.app.notesappandroidproject.data.dataSource;

import android.content.Context;

import com.app.notesappandroidproject.domain.note.Note;

import java.util.List;

// Service class responsible for handling synchronization logic between the mobile app and Wear OS.
// It acts as an abstraction layer over the SyncManager to perform note synchronization.
public class MobileSyncService {

    private SyncManager syncManager;

    // Constructor initializes the SyncManager with the provided application context.
    public MobileSyncService(Context context) {
        this.syncManager = new SyncManager(context);
    }

    // Triggers the synchronization of notes to Wear OS using SyncManager.
    public void syncNotes(List<Note> notes) {
        syncManager.syncNotesToWearOS(notes);
    }
}
