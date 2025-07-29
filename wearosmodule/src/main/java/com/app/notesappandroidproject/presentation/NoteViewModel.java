package com.app.notesappandroidproject.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.app.notesappandroidproject.data.local.NoteEntity;
import com.app.notesappandroidproject.data.repository.NoteRepositoryImpl;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel responsible for managing the UI-related data for notes.
 * Handles data operations through the repository and exposes LiveData for observing changes.
 */
@HiltViewModel
public class NoteViewModel extends ViewModel {

    private final NoteRepositoryImpl repository;
    private final LiveData<List<Note>> notes;

    /**
     * Constructor to inject dependencies using Hilt.
     * @param repository The repository instance responsible for data access logic
     */
    @Inject
    public NoteViewModel(NoteRepositoryImpl repository) {
        this.repository = repository;
        this.notes = repository.getListNote();
    }

    /**
     * Exposes LiveData to observe the list of notes in the UI layer.
     * @return LiveData containing the list of notes
     */
    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    /**
     * Saves a new note to the database by delegating to the repository.
     * @param note The note to save
     */
    public void saveNote(Note note) {
        repository.insertNote(note);
    }

    /**
     * Synchronizes notes from a mobile source to the local database through the repository.
     * @param notes List of NoteEntity objects representing notes to sync
     */
    public void syncNotesFromMobile(List<NoteEntity> notes) {
        repository.syncNotesFromMobile(notes);
    }

    /**
     * Triggers a refresh to load the latest notes from the database.
     * Useful for ensuring the UI stays in sync with data changes.
     */
    public void refreshNotes() {
        repository.loadNotes();
    }
}

