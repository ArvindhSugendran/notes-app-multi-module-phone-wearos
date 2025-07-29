package com.app.notesappandroidproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SharedPreferencesDB {
    public static final String PREF_NAME = "NoteAppAuth";

    // Define your keys as constants
    public static final String KEY_PASSWORD = "NoteAppPassword";

    private final SharedPreferences preferences;

    public SharedPreferencesDB(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    // Method to save data to SharedPreferences
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Method to get data from SharedPreferences
    public String getString(String key) {
        return preferences.getString(key, ""); // null is the default value if the key is not found
    }
}
