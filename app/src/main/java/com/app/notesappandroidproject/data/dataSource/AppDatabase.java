package com.app.notesappandroidproject.data.dataSource;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.app.notesappandroidproject.App;
import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.domain.question.Question;

// Singleton Room Database class to manage database creation and access for the app.
// Contains entities `Note` and `Question` and provides an instance of `noteQueDao` for database operations.
@Database(entities = {Note.class, Question.class}
        , version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Provides DAO for database operations.
    private static AppDatabase instance;

    // Returns a singleton instance of the database.
    public abstract noteQueDao noteQueDao();

    public static AppDatabase getInstance() {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                instance = Room.databaseBuilder(App.getInstance()
                                , AppDatabase.class
                                , "noteQue.db")
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return instance;
    }

}
