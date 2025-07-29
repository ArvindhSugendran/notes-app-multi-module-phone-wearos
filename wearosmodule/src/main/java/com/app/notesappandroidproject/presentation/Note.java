package com.app.notesappandroidproject.presentation;


import java.io.Serializable;

/**
 * Represents a Note entity with properties such as ID, title, description, date of last update, and recycle status.
 * Implements Serializable to allow passing Note objects between components or saving instance states.
 */
public class Note implements Serializable {
    private final long id;
    private final String title;
    private final String description;
    private final long dateUpdate;
    private final boolean isRecycled;

    /**
     * Constructor to initialize a Note object with the provided parameters.
     * @param id Unique identifier for the note
     * @param title The title of the note
     * @param description The description/content of the note
     * @param dateUpdate Timestamp for the last note update
     * @param isRecycled Boolean representing the recycle status of the note
     */
    public Note(long id, String title, String description, long dateUpdate, Boolean isRecycled) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateUpdate = dateUpdate;
        this.isRecycled = isRecycled;
    }

    /**
     * Gets the unique ID of the note.
     * @return The ID of the note
     */
    public long getId() {
        return id;
    }

    /**
     * Checks if the note is marked as recycled.
     * @return True if the note is recycled, otherwise false
     */
    public boolean isRecycled() {
        return isRecycled;
    }

    /**
     * Retrieves the title of the note.
     * @return The title of the note
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the description of the note.
     * @return The description of the note
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the timestamp representing the last date the note was updated.
     * @return The date of the last update in milliseconds
     */
    public long getDateUpdate() {
        return dateUpdate;
    }

}