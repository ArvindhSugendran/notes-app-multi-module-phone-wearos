package com.app.notesappandroidproject.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.notesappandroidproject.R;
import com.app.notesappandroidproject.activities.MainActivity;
import com.app.notesappandroidproject.databinding.FragmentNoteDetailBinding;
import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.services.DBService;
import com.app.notesappandroidproject.utils.Config;
import com.app.notesappandroidproject.utils.DateUtils;

import java.util.Date;
import java.util.Objects;


/**
 * NoteDetailFragment
 * <p>
 * This fragment allows users to view, create, and edit a note's details.
 * It provides functionality to save and validate note details and displays
 * the creation or update timestamps for the note if available.
 * <p>
 * Features:
 * 1. View details of an existing note if passed as arguments.
 * 2. Allow users to create new notes or update an existing one.
 * 3. Input validation for required fields (title and content).
 * 4. Handle timestamps (creation time & last updated) using DateUtils.
 * 5. Database interaction for saving and retrieving notes using DBService.
 * 6. UI customization, such as adjusting text size and line spacing.
 * 7. Toast messages to inform users of validation failures.
 * <p>
 * Dependencies:
 * - DBService for database operations.
 * - Config for UI settings like text sizes and line spacing.
 * - DateUtils for formatting timestamps.
 * <p>
 * Note: `MainActivity` is used for navigation handling (back press & hamburger menu visibility).
 */
public class NoteDetailFragment extends Fragment implements View.OnClickListener {

    public static String NOTE_DATA = "note data";

    FragmentNoteDetailBinding noteDetailBinding = null;
    View rootView = null;

    DBService dbService;

    private boolean isUpdate;

    Note mNote = null;

    String title = "", content = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment using view binding
        noteDetailBinding = FragmentNoteDetailBinding.inflate(inflater, container, false);
        rootView = noteDetailBinding.getRoot(); // Get the root view

        initView();

        return rootView; // Return the root view
    }

    /**
     * Initializes UI components, listeners, and sets up the initial state.
     */
    // Initialize database service instance
    // Hide hamburger menu icon when this fragment is active
    // Check if note data is passed to the fragment as arguments
    // Populate the UI with the note's title, content, and timestamps if available
    // Set up click listeners
    // Attach text watchers to the title and content fields
    // Configure the text size and line spacing for better UI experience
    private void initView() {

        dbService = new DBService(requireContext()); // Initialize database service

        // Call this method to hide the hamburger menu icon from the hosting activity
        ((MainActivity) requireActivity()).setHamburgerMenuVisibility(false);

        // Check if note data is passed through arguments
        if (getArguments() != null) {
            mNote = (Note) getArguments().getSerializable(NOTE_DATA);
            isUpdate = true;
        }

        // Populate views with note data if available
        if (mNote != null) {
            if (!TextUtils.isEmpty(mNote.title)) {
                title = mNote.title;
                noteDetailBinding.edtTitle.setText(mNote.title);
            }
            if (!TextUtils.isEmpty(mNote.content)) {
                content = mNote.content;
                noteDetailBinding.edtContent.setText(mNote.content);
            }
            noteDetailBinding.tvTimeCreate.setText(getString(R.string.created_at, DateUtils.getStampByDate(new Date(mNote.id), DateUtils.DATE_FORMAT_1)));
            noteDetailBinding.tvTimeCreate.setVisibility(View.VISIBLE);
            if (mNote.dateUpdate != 0) {
                noteDetailBinding.tvTimeUpdate.setText(getString(R.string.update_at, DateUtils.getStampByDate(new Date(mNote.dateUpdate), DateUtils.DATE_FORMAT_1)));
                noteDetailBinding.tvTimeUpdate.setVisibility(View.VISIBLE);
            }
        }

        noteDetailBinding.statusBack.setOnClickListener(this); // Set click listener for back button
        noteDetailBinding.fabSave.setOnClickListener(this); // Set click listener for save button

        // Set text change listeners for title and content fields
        noteDetailBinding.edtTitle.addTextChangedListener(titleTextWatcher);
        noteDetailBinding.edtContent.addTextChangedListener(contentTextWatcher);

        // Set text size and line spacing
        setTextSize();
        setLineSpace();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == noteDetailBinding.statusBack.getId()) {
            ((MainActivity) requireActivity()).backPress();
        }

        if(view.getId() == noteDetailBinding.fabSave.getId()) {
            if (validate()) {
                if (mNote == null) {
                    mNote = new Note(title, content,false);
                    isUpdate = false;
                } else {
                    mNote.title = title;
                    mNote.content = content;
                    if (isUpdate)
                        mNote.dateUpdate = System.currentTimeMillis();
                }

                dbService.insertNote(mNote);
                ((MainActivity) requireActivity()).backPress();
            }
        }
    }

    // Text watcher for input fields
    /**
     * TextWatcher to monitor changes in the title field.
     */
    private final TextWatcher titleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not used
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            title = s.toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * TextWatcher to monitor changes in the content field.
     */
    private final TextWatcher contentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not used
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            content = s.toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Validates the user's input for required fields.
     * @return true if validation succeeds, false otherwise.
     */
    private boolean validate() {
        if (TextUtils.isEmpty(Objects.requireNonNull(noteDetailBinding.edtTitle.getText()).toString())) {
            String errorTitleMessage = getString(R.string.error_title_note);
            Toast.makeText(requireContext(),errorTitleMessage,Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(noteDetailBinding.edtContent.getText()).toString())) {
            String errorTitleContentMessage = getString(R.string.error_title_content);
            Toast.makeText(requireContext(),errorTitleContentMessage,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Method to set text size for views
    /**
     * Sets the text size for various text components in the UI.
     */
    private void setTextSize() {
        noteDetailBinding.edtTitle.setTextSize(Config.TEXT_SIZE_DEFAULT);
        noteDetailBinding.edtContent.setTextSize(Config.TEXT_SIZE_DEFAULT);
        noteDetailBinding.tvTimeUpdate.setTextSize(Config.TEXT_SIZE_DEFAULT - 2);
        noteDetailBinding.tvTimeCreate.setTextSize(Config.TEXT_SIZE_DEFAULT - 2);
    }

    // Method to set line spacing for views
    /**
     * Adjusts the line spacing for text fields in the UI for better readability.
     */
    private void setLineSpace() {
        noteDetailBinding.edtTitle.setLineSpacing(Config.TEXT_LINE_SPACE_DEFAULT, 1.0f);
        noteDetailBinding.edtContent.setLineSpacing(Config.TEXT_LINE_SPACE_DEFAULT, 1.0f);
        noteDetailBinding.tvTimeUpdate.setLineSpacing(Config.TEXT_LINE_SPACE_DEFAULT, 1.0f);
        noteDetailBinding.tvTimeCreate.setLineSpacing(Config.TEXT_LINE_SPACE_DEFAULT, 1.0f);
    }
}