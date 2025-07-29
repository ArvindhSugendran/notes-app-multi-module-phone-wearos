package com.app.notesappandroidproject.domain.question;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Represents a Question entity mapped to the 'question' table in the Room database.
 * Implements Serializable for potential data transfer between components.
 */
@Entity(tableName = "question")
public class Question implements Serializable {

    public static final int ID_FINAL = 1;

    @PrimaryKey()
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "question")
    public String question;

    @ColumnInfo(name = "answer")
    public String answer;

    /**
     * Constructor to initialize a new Question object.
     * The ID is set to the predefined constant value (ID_FINAL).
     * @param question The text of the question
     * @param answer The answer to the question
     */
    public Question(String question, String answer) {
        this.id = ID_FINAL;
        this.question = question;
        this.answer = answer;
    }
}

