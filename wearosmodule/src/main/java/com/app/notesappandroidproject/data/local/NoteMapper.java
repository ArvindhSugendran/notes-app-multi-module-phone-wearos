package com.app.notesappandroidproject.data.local;

import com.app.notesappandroidproject.presentation.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteMapper {

    // Converts NoteEntity to Note
    public static Note mapToPresentation(NoteEntity noteEntity) {
        return new Note(
                noteEntity.id,
                noteEntity.title,
                noteEntity.content,
                noteEntity.dateUpdate,
                noteEntity.isRecycled
        );
    }

    // Converts Note to NoteEntity
    public static NoteEntity mapToEntity(Note note) {
        NoteEntity noteEntity = new NoteEntity();
        noteEntity.id = note.getId();
        noteEntity.title = note.getTitle();
        noteEntity.content = note.getDescription();
        noteEntity.dateUpdate = note.getDateUpdate();
        noteEntity.isRecycled = note.isRecycled();
        return noteEntity;
    }

    // Converts a list of NoteEntity to a list of Note
    public static List<Note> mapToPresentationList(List<NoteEntity> noteEntities) {
        List<Note> notes = new ArrayList<>();
        for (NoteEntity noteEntity : noteEntities) {
            notes.add(mapToPresentation(noteEntity));
        }
        return notes;
    }

    // Converts a list of Note to a list of NoteEntity
    public static List<NoteEntity> mapToEntityList(List<Note> notes) {
        List<NoteEntity> noteEntities = new ArrayList<>();
        for (Note note : notes) {
            noteEntities.add(mapToEntity(note));
        }
        return noteEntities;
    }
}
