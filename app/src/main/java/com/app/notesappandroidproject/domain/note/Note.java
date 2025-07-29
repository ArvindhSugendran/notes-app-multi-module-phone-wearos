package com.app.notesappandroidproject.domain.note;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Represents a Note entity mapped to the 'notes' table in the Room database.
 * Implements Serializable for potential data transfer between components.
 */
@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey()
    @ColumnInfo(name = "noteId")
    public long id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "date_update")
    public long dateUpdate;

    @ColumnInfo(name = "is_recycled" ,defaultValue = "false")
    public boolean isRecycled;

    /**
     * Constructor to initialize a new Note object.
     * The note ID is automatically generated using the current timestamp.
     * @param title Title of the note
     * @param content Content of the note
     * @param isRecycled Indicates whether the note is archived
     */
    public Note(String title, String content, boolean isRecycled) {
        this.id = System.currentTimeMillis();
        this.title = title;
        this.content = content;
        this.isRecycled = isRecycled;
    }
}