package com.app.notesappandroidproject.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * NoteEntity represents the structure of the "notes" table in the Room database.
 * This class models the database entity with annotations to map the fields properly.
 * It implements Serializable to allow passing NoteEntity between activities or fragments.
 */
@Entity(tableName = "notes")
public class NoteEntity implements Serializable {

    /**
     * Primary key representing the unique identifier for each note in the database.
     * The database will automatically generate IDs if configured.
     */
    @PrimaryKey()
    @ColumnInfo(name = "noteId")
    public long id;

    /**
     * The title of the note. Represents a brief description or heading for the note.
     */
    @ColumnInfo(name = "title")
    public String title;

    /**
     * The main content of the note. This is the detailed information about the note.
     */
    @ColumnInfo(name = "content")
    public String content;

    /**
     * Timestamp representing the last date of note modification in milliseconds since epoch.
     */
    @ColumnInfo(name = "date_update")
    public long dateUpdate;

    /**
     * Indicates if a note is "recycled" (soft-deleted). Default is `false`.
     * Used to distinguish archived or deleted notes from active ones.
     */
    @ColumnInfo(name = "is_recycled" ,defaultValue = "false")
    public boolean isRecycled;

    /**
     * Getter for Note ID.
     *
     * @return The note's unique identifier.
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for Note ID.
     *
     * @param id The new ID value to set.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for the note's title.
     *
     * @return The note's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the note's title.
     *
     * @param title The new title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the note's content.
     *
     * @return The note's content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the note's content.
     *
     * @param content The new content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Getter for last updated timestamp.
     *
     * @return The last update timestamp.
     */
    public long getDateUpdate() {
        return dateUpdate;
    }

    /**
     * Setter for last updated timestamp.
     *
     * @param dateUpdate The timestamp value to set.
     */
    public void setDateUpdate(long dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    /**
     * Checks if a note is in a "recycled" (archived) state.
     *
     * @return True if recycled, otherwise false.
     */
    public boolean isRecycled() {
        return isRecycled;
    }

    /**
     * Sets a note's "recycled" status.
     *
     * @param recycled The status to set (true for archived, false otherwise).
     */
    public void setRecycled(boolean recycled) {
        isRecycled = recycled;
    }
}
