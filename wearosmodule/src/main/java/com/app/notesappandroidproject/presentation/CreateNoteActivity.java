package com.app.notesappandroidproject.presentation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.app.notesappandroidproject.R;
import com.app.notesappandroidproject.data.repository.NoteRepositoryImpl;
import com.app.notesappandroidproject.databinding.ActivityCreateNoteBinding;
import com.app.notesappandroidproject.utils.DateUtils;


import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * CreateNoteActivity
 * <p>
 * This activity handles creating and editing notes. It manages the user interface for note creation and
 * note editing workflows. It validates user input, interacts with the ViewModel to save data,
 * and performs necessary UI updates depending on whether the user is creating a new note or editing an existing one.
 * <p>
 * Features:
 * 1. Validate note fields for empty input before saving.
 * 2. Determine if the activity is in create mode or update mode based on incoming intent data.
 * 3. Observe changes in UI fields for real-time user interaction.
 * 4. Handle UI configuration based on whether the device has a round or square screen (Wear OS devices).
 * <p>
 * Responsibilities:
 * - Manage note saving through ViewModel.
 * - Map and display data in UI fields from the Note object.
 * - Observe user input changes and validate fields accordingly.
 */
@AndroidEntryPoint
public class CreateNoteActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityCreateNoteBinding noteDetailBinding;
    View rootView = null;

    private NoteViewModel noteViewModel;

    @Inject
    NoteRepositoryImpl noteRepositoryImpl;

    private boolean isUpdate;

    Note mNote = null;

    String title = "", content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteDetailBinding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        rootView = noteDetailBinding.getRoot();
        setContentView(rootView);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        init();
        configureUI();
    }

    /**
     * Initialize intent data and determine if this activity is in create mode or edit mode.
     */
    private void init() {
        mNote = (Note) getIntent().getSerializableExtra("NOTE_DATA");
        if (mNote != null) {
            Log.d("NOTE_DATA", mNote.getTitle());
            isUpdate = true;
        }
    }

    /**
     * Configure the user interface, including:
     * - Handling the Wear OS UI configuration (round/square devices).
     * - Populating the fields if editing an existing note.
     * - Adding listeners to handle save button and text changes.
     */
    private void configureUI() {

        //check if the wear os is round or square
        boolean isRound = getResources().getConfiguration().isScreenRound();

        int padding = getResources().getDimensionPixelOffset(R.dimen.round_padding);

        //if the wear is round then apply a padding of 15 dp to the Box Inset Layout
        if (isRound) {
            noteDetailBinding.relativeDesign.setPadding(padding, padding, padding, padding);
            noteDetailBinding.saveButton.setPadding(padding, padding, padding, padding);
        }

        noteDetailBinding.saveButton.bringToFront();

        // Populate views with note data if available
        if (mNote != null) {
            if (!TextUtils.isEmpty(mNote.getTitle())) {
                title = mNote.getTitle();
                noteDetailBinding.edtTitle.setText(mNote.getTitle());
            }
            if (!TextUtils.isEmpty(mNote.getDescription())) {
                content = mNote.getDescription();
                noteDetailBinding.edtContent.setText(mNote.getDescription());
            }
            noteDetailBinding.tvTimeCreate.setText(getString(R.string.created_at, DateUtils.getStampByDate(new Date(mNote.getId()), DateUtils.DATE_FORMAT_1)));
            noteDetailBinding.tvTimeCreate.setVisibility(View.VISIBLE);
            if (mNote.getDateUpdate() != 0) {
                noteDetailBinding.tvTimeUpdate.setText(getString(R.string.update_at, DateUtils.getStampByDate(new Date(mNote.getDateUpdate()), DateUtils.DATE_FORMAT_1)));
                noteDetailBinding.tvTimeUpdate.setVisibility(View.VISIBLE);
            }
        }

        noteDetailBinding.fabSaveNote.setOnClickListener(this); // Set click listener for save button

        // Set text change listeners for title and content fields
        noteDetailBinding.edtTitle.addTextChangedListener(titleTextWatcher);
        noteDetailBinding.edtContent.addTextChangedListener(contentTextWatcher);
    }

    // Text watcher for input fields
    /**
     * TextWatcher for monitoring title input field changes.
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
     * TextWatcher for monitoring content input field changes.
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
     * Validates title and content fields before saving.
     * Ensures neither field is empty and provides user feedback on invalid input.
     *
     * @return true if the fields are valid, false otherwise.
     */
    private boolean validate() {
        if (TextUtils.isEmpty(Objects.requireNonNull(noteDetailBinding.edtTitle.getText()).toString())) {
            String errorTitleMessage = getString(R.string.error_title_note);
            Toast.makeText(this, errorTitleMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(noteDetailBinding.edtContent.getText()).toString())) {
            String errorTitleContentMessage = getString(R.string.error_title_content);
            Toast.makeText(this, errorTitleContentMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteDetailBinding = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == noteDetailBinding.fabSaveNote.getId()) {
            if (validate()) {
                if (mNote == null) {
                    noteViewModel.saveNote(new Note(System.currentTimeMillis(), title, content, 0, false));
                    isUpdate = false;
                } else {
                    if (isUpdate) {
                        if(Objects.equals(mNote.getTitle(), title) && Objects.equals(mNote.getDescription(), content)) {
                            finish();
                        } else {
                            noteViewModel.saveNote(new Note(mNote.getId(), title, content, System.currentTimeMillis(), mNote.isRecycled()));
                        }
                    }
                }

                finish();
            }
        }
    }

    @Override
    public void addMenuProvider(@NonNull MenuProvider provider, @NonNull LifecycleOwner owner, @NonNull Lifecycle.State state) {

    }
}