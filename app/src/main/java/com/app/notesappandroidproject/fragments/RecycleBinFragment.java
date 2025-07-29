package com.app.notesappandroidproject.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.notesappandroidproject.R;
import com.app.notesappandroidproject.activities.MainActivity;
import com.app.notesappandroidproject.adapter.note.ListNoteAdapter;
import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.utils.CustomDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

/**
 * RecycleBinFragment
 * <p>
 * This fragment represents the Recycle Bin screen for the notes application.
 * It allows users to view, delete, and restore notes from the Recycle Bin.
 * It includes UI animations, database synchronization, and dynamic search features.
 * <p>
 * Features:
 * 1. Display a list of deleted (recycled) notes using a RecyclerView with adapter support.
 * 2. Enable users to delete and restore notes through UI interactions.
 * 3. Synchronize note status (restore or delete) with other parts of the application using Broadcast Receivers.
 * 4. Perform database operations to fetch and restore notes via `DBService`.
 * 5. Animate UI elements using the YoYo animation library for better visual feedback.
 * 6. Handle edge cases such as empty Recycle Bin states and invalid actions.
 * <p>
 * Dependencies:
 * - DBService for database operations.
 * - MobileSyncService for synchronization between components.
 * - YoYo for UI animations.
 * - CustomDialog for confirmation dialogs.
 * - SearchViewComponent for dynamic search capabilities.
 */

public class RecycleBinFragment extends NoteFragment {


    private boolean isButtonAnimated = false;

    /**
     * onCreateView
     * <p>
     * Inflates the fragment's layout from the parent fragment's layout.
     * This ensures the parent fragment's base logic is preserved.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * onViewCreated
     * <p>
     * Handles view initialization after the fragment's view is created.
     * Sets up click listeners for FAB buttons to trigger UI actions.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noteBinding.fabDeleteNote.setOnClickListener(view1 -> showDialogConfirmDeleteNote());
    }

    /**
     * adapterItemClickListener
     * <p>
     * Sets up adapter item click listeners for filtering and click-based note management.
     * Includes listener logic for handling checkbox selection and search visibility.
     */
    @Override
    public void adapterItemClickListener() {
        mListNoteAdapter.setFilterListener(filteredNotesList -> {
            if (noteBinding.searchViewComponent.isShown()) {
                noteBinding.notesRecyclerView.setVisibility(mListNoteAdapter.getNotesList().isEmpty() ? View.GONE : View.VISIBLE);
                noteBinding.tvNoNote.setVisibility(mListNoteAdapter.getNotesList().isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        mListNoteAdapter.setmItemClickListener(new ListNoteAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(Note mNote) {

            }

            @Override
            public void onLongClickListener(Note mNote) {
                setStageToolbarDelete(true);
            }

            @Override
            public void onCheckboxChanged(Note note) {
                if (isSelectable) {
                    if (deleteNote.contains(note)) {
                        deleteNote.remove(note);
                    } else {
                        deleteNote.add(note);
                    }

                    if (deleteNote.isEmpty() && noteBinding.search.isShown()) {
                        noteBinding.searchViewComponent.setVisibility(View.VISIBLE);
                    } else {
                        noteBinding.searchViewComponent.setVisibility(View.GONE);
                    }
                }
            }

        });
    }

    /**
     * initView
     * <p>
     * Initializes the fragment view elements like buttons and status text.
     * Also applies animations using the YoYo library.
     */
    @Override
    public void initView() {
        super.initView();
        noteBinding.fabConfirmDelete.setText(R.string.restore_note);
        noteBinding.statusContent.setText(R.string.recyclebin);

        noteBinding.fabConfirmDelete.setVisibility(View.VISIBLE);
        noteBinding.fabDeleteNote.setVisibility(View.VISIBLE);

        noteBinding.fabConfirmDelete.setIcon(getResources().getDrawable(R.drawable.baseline_cached_24, null));

        YoYo.with(Techniques.ZoomOut).duration(0).playOn(noteBinding.fabConfirmDelete);
        YoYo.with(Techniques.ZoomOut).duration(0).playOn(noteBinding.fabDeleteNote);

        noteBinding.fabCreatNote.setVisibility(View.INVISIBLE);
    }

    /**
     * loadData
     * <p>
     * Fetches data from the database to populate the Recycle Bin with notes.
     * Handles visibility changes depending on whether data exists.
     */
    @Override
    public void loadData() {
        mListNoteAdapter.addAll(dbService.getRecycledNotesList());
        noteBinding.notesRecyclerView.setVisibility(mListNoteAdapter.getNotesList().isEmpty() ? View.GONE : View.VISIBLE);
        noteBinding.tvNoNote.setVisibility(mListNoteAdapter.getNotesList().isEmpty() ? View.VISIBLE : View.GONE);
        sortListNote();
    }

    /**
     * showDialogConfirmDeleteNote
     * <p>
     * Displays a dialog prompting the user to confirm deletion of selected notes.
     * If no notes are selected, a toast is shown.
     */
    @Override
    public void showDialogConfirmDelete() {
        new CustomDialog(requireContext())
                .setTitle("Restore !! ")
                .setMessage("Are you sure you want to restore ?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Restore", (dialog, which) -> {
                    // Delete Note and update data
                    if (deleteNote.isEmpty()) {
                        String message = requireContext().getResources().getString(R.string.must_select_one_item);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        dbService.restoreNotes(deleteNote);
                    }
                })
                .show();
    }

    @Override
    public void setStageToolbarDelete(boolean canSelect) {
        mListNoteAdapter.setCanSelect(canSelect);
        noteBinding.statusBack.setVisibility(canSelect ? View.VISIBLE : View.GONE);
        noteBinding.search.setVisibility(canSelect ? View.GONE : View.VISIBLE);
        ((MainActivity) requireActivity()).setHamburgerMenuVisibility(!canSelect);

        isSelectable = canSelect;
        deleteNote.clear();

        mListNoteAdapter.notifyDataSetChanged();

        if (canSelect) {
            YoYo.with(Techniques.ZoomIn).duration(300).playOn(noteBinding.fabDeleteNote);
            YoYo.with(Techniques.ZoomIn).duration(300).playOn(noteBinding.fabConfirmDelete);

            isButtonAnimated = true;
        } else {

            if (isButtonAnimated) {
                YoYo.with(Techniques.ZoomOut).duration(300).playOn(noteBinding.fabDeleteNote);
                YoYo.with(Techniques.ZoomOut).duration(300).playOn(noteBinding.fabConfirmDelete);

                isButtonAnimated = false;
            }

            loadData();
        }
    }

    @Override
    public void openOrCloseSearchView(boolean canSearch) {
        noteBinding.searchViewComponent.setVisibility(canSearch ? View.VISIBLE : View.GONE);

        YoYo.with(canSearch ? Techniques.ZoomOut : Techniques.ZoomIn).duration(300).playOn(noteBinding.fabCreatNote);
        noteBinding.fabCreatNote.setVisibility(View.INVISIBLE);

        if (canSearch)
            noteBinding.searchViewComponent.setEdtSearch("");

        loadData();
    }


    public void showDialogConfirmDeleteNote() {
        new CustomDialog(requireContext())
                .setTitle("Delete !! ")
                .setMessage("Are you sure you want to delete ?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete Note and update data
                    if (deleteNote.isEmpty()) {
                        String message = requireContext().getResources().getString(R.string.must_select_one_item);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        dbService.deleteNote(deleteNote);
                    }
                })
                .show();
    }

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_NOTE_UNARCHIVED".equals(intent.getAction())) {
                Log.d("MOBILE_SYNC", "Note unarchived successfully");
                new Handler(Looper.getMainLooper()).post(() -> {
                    refreshScreen();
                });
            } else if("ACTION_NOTE_DELETED".equals(intent.getAction())) {
                Log.d("MOBILE_SYNC", "Note deleted successfully");
                new Handler(Looper.getMainLooper()).post(() -> {
                    refreshScreen();
                });
            }
        }
    };

    private void refreshScreen() {
        openOrCloseSearchView(false);
        setStageToolbarDelete(false);
        loadData();

        List<Note> notes = dbService.getNotes();
        mobileSyncService.syncNotes(notes);
    }


    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_NOTE_UNARCHIVED");
        filter.addAction("ACTION_NOTE_DELETED");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(syncReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(syncReceiver);
    }
}