package com.app.notesappandroidproject.data.dataSource;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.domain.question.Question;

import java.util.List;

// Data Access Object (DAO) interface for database operations related to Note and Question entities.
// Defines database interactions using Room annotations for CRUD operations.
@Dao
public interface noteQueDao {

    // Fetch all notes from the database, ordered by the latest noteId first.
    @Query("SELECT * FROM notes ORDER BY noteId DESC")
    List<Note> getNotes();

    // Fetch notes that are not marked as recycled, ordered by the latest noteId first.
    @Query("SELECT * FROM notes where is_recycled = 0 ORDER BY noteId DESC")
    List<Note> getListNote();

    // Deletes a list of notes from the database.
    @Delete
    void deleteNote(List<Note> notes);

    // Inserts a Note into the database, replacing it if it conflicts with an existing entry.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note mNote);

    // Inserts a Question into the database, replacing it if it conflicts with an existing entry.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(Question mQuestion);

    // Fetches a question by its ID from the database.
    @Query("SELECT * FROM question WHERE id =:id")
    Question getQuestion(int id);

    // Fetch notes that are marked as recycled, ordered by the latest noteId first.
    @Query("SELECT * FROM notes where is_recycled = 1 ORDER BY noteId DESC")
    List<Note> getRecycledListNote();

    // Marks a note as unarchived by setting its `is_recycled` flag to 0.
    @Query("Update notes set is_recycled = 0 where noteId =:id")
    void unarchieveRecycledNote(long id);

    // Marks a note as archived by setting its `is_recycled` flag to 1.
    @Query("Update notes set is_recycled = 1 where noteId =:id")
    void archiveNote(long id);

}