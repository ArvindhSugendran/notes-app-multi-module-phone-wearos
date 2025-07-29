package com.app.notesappandroidproject.data.dataSource;

import android.content.Context;

import com.app.notesappandroidproject.data.repository.noteQueRepositoryImpl;

// A helper class responsible for providing dependencies, specifically the Note Repository instance.
// This follows the Dependency Injection pattern to manage object creation and dependencies in a modular way.
public class Injection {

    // Provides a singleton instance of `noteQueRepositoryImpl` with required dependencies
    public static noteQueRepositoryImpl providerNoteDataSource(Context context) {
        AppDatabase mAppDatabase = AppDatabase.getInstance();
        return new noteQueRepositoryImpl(mAppDatabase.noteQueDao(), context);
    }
}
