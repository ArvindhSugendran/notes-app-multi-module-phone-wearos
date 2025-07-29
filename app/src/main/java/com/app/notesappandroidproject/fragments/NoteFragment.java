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
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.notesappandroidproject.R;
import com.app.notesappandroidproject.activities.MainActivity;
import com.app.notesappandroidproject.adapter.note.ListNoteAdapter;
import com.app.notesappandroidproject.data.dataSource.MobileSyncService;
import com.app.notesappandroidproject.databinding.FragmentNoteBinding;
import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.services.DBService;
import com.app.notesappandroidproject.utils.CustomDialog;
import com.app.notesappandroidproject.utils.DateUtils;
import com.app.notesappandroidproject.utils.IOBackPress;
import com.app.notesappandroidproject.widget.SearchViewComponent;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;

import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * NoteFragment
 * <p>
 * This fragment handles the display and interaction logic for a notes screen within the application.
 * It allows users to view, search, and manage notes, including creating, deleting, and synchronizing data
 * between the mobile app and wearable devices.
 * <p>
 * Features:
 * 1. Display a list of notes using a RecyclerView with adapter support.
 * 2. Allow users to create new notes or delete selected notes through UI interactions.
 * 3. Provide search functionality with a dynamic SearchView for filtering notes.
 * 4. Synchronize notes between mobile and wearable devices via `MobileSyncService`.
 * 5. Handle click and long-click interactions for note management.
 * 6. Perform database operations to fetch and save notes using `DBService`.
 * 7. Animate the UI with visual effects using the YoYo animation library.
 * 8. Handle input validation and check edge cases such as an empty notes list.
 * <p>
 * Dependencies:
 * - DBService for database operations.
 * - MobileSyncService for wearable synchronization.
 * - YoYo for UI animations.
 * - SearchViewComponent for dynamic search capabilities.
 * <p>
 * Note: `MainActivity` is used for navigation handling and back press management.
 */
public class NoteFragment extends Fragment implements View.OnClickListener, IOBackPress, DataClient.OnDataChangedListener  {


    // Binding for fragment views
    // Root view reference for the fragment
    // Database service for data management
    // Adapter to manage the notes list UI
    // Service for note synchronization with mobile devices
    public FragmentNoteBinding noteBinding = null;
    View rootView = null;

    DBService dbService;

    public ListNoteAdapter mListNoteAdapter;

    public final List<Note> deleteNote = new ArrayList<>();

    public boolean isSelectable = false;

    public static final String NOTES_DATA_PATH = "/notes_wear_to_mobile";

    MobileSyncService mobileSyncService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment using view binding
        noteBinding = FragmentNoteBinding.inflate(inflater, container, false);
        rootView = noteBinding.getRoot(); // Get the root view

        initView();
        initControl();

        List<Note> notes = dbService.getNotes();

        if(!notes.isEmpty()) {
            mobileSyncService = new MobileSyncService(requireContext());
            mobileSyncService.syncNotes(notes);
        }

        return rootView; // Return the root view
    }

    /**
     * Initialize view components and set up adapter for RecyclerView.
     */
    public void initView() {

        dbService = new DBService(requireContext());
        mListNoteAdapter = new ListNoteAdapter(new ArrayList<>());

        ((MainActivity) requireActivity()).setOnBackPressListener(this);

        noteBinding.search.setOnClickListener(this);
        noteBinding.statusBack.setOnClickListener(this);
        noteBinding.fabCreatNote.setOnClickListener(this);
        noteBinding.fabConfirmDelete.setOnClickListener(this);
        noteBinding.fabConfirmDelete.setVisibility(View.GONE);

        noteBinding.notesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteBinding.notesRecyclerView.setAdapter(mListNoteAdapter);
    }

    /**
     * Load data into the RecyclerView adapter and manage visibility based on data presence.
     */
    public void loadData() {
        mListNoteAdapter.addAll(dbService.getListNote());

        noteBinding.notesRecyclerView.setVisibility(mListNoteAdapter.getNotesList().isEmpty() ? View.GONE : View.VISIBLE);
        noteBinding.tvNoNote.setVisibility(mListNoteAdapter.getNotesList().isEmpty() ? View.VISIBLE : View.GONE);
        sortListNote();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == noteBinding.search.getId()) {
            openOrCloseSearchView(true);
        }

        if (view.getId() == noteBinding.statusBack.getId()) {
            if (noteBinding.searchViewComponent.isShown()) {
                openOrCloseSearchView(false);
                setStageToolbarDelete(false);
            } else if (isSelectable) {
                setStageToolbarDelete(false);
            }
        }

        if (view.getId() == noteBinding.fabCreatNote.getId()) {
            openDetailScreen(null);
        }

        if (view.getId() == noteBinding.fabConfirmDelete.getId()) {
            if (deleteNote.size() >= 1) {
                showDialogConfirmDelete();
            } else {
                String message = requireContext().getResources().getString(R.string.please_chosse_one);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Initialize listeners for the search bar and its dynamic behavior.
     */
    private void initControl() {
        adapterItemClickListener();


        noteBinding.searchViewComponent.setSearchViewListener(new SearchViewComponent.SearchViewListener() {
            @Override
            public void onTextChangeListener(String txt) {
                mListNoteAdapter.getFilter().filter(txt);
            }

            @Override
            public void onCloseSearchView() {
                openOrCloseSearchView(false);
                setStageToolbarDelete(false);
            }

        });
    }

    /**
     * Handle item click events, filtering, and note selection for deletion or edit operations.
     */
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
                openDetailScreen(mNote);
                if (noteBinding.searchViewComponent.isShown())
                    openOrCloseSearchView(false);
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
     * openDetailScreen
     * <p>
     * Opens the NoteDetailFragment screen for viewing or editing a note.
     * It bundles the note data and passes it to the navigation component.
     * <p>
     * Features:
     * 1. Check if the note is valid and bundle its data.
     * 2. Navigate to the detail screen using the navigation component.
     * <p>
     * Dependencies:
     * - Navigation component for navigation handling.
     */
    private void openDetailScreen(Note mNote) {
        Bundle bundle = new Bundle();
        if (mNote != null)
            bundle.putSerializable(NoteDetailFragment.NOTE_DATA, mNote);
        Navigation.findNavController(requireView()).navigate(R.id.action_nav_note_to_note_detail, bundle);
    }


    /**
     * openOrCloseSearchView
     * <p>
     * Toggles the visibility of the search view and adjusts the FAB (floating action button) animation state.
     * Also handles resetting search fields and triggers data loading as necessary.
     * <p>
     * Features:
     * 1. Show/hide the search view based on the `canSearch` boolean.
     * 2. Animate the FAB on showing or hiding search view using YoYo animations.
     * 3. Clear the search input when enabling the search view.
     * 4. Reload data upon showing the search view.
     * <p>
     * Dependencies:
     * - YoYo animations for UI effects.
     */
    public void openOrCloseSearchView(boolean canSearch) {
        noteBinding.searchViewComponent.setVisibility(canSearch ? View.VISIBLE : View.GONE);

        YoYo.with(canSearch ? Techniques.ZoomOut : Techniques.ZoomIn).duration(300).playOn(noteBinding.fabCreatNote);
        noteBinding.fabCreatNote.setVisibility(canSearch ? View.GONE : View.VISIBLE);

        if (canSearch)
            noteBinding.searchViewComponent.setEdtSearch("");

        loadData();

    }

    /**
     * setStageToolbarDelete
     * <p>
     * Manages UI changes for enabling or disabling multi-selection mode for deleting notes.
     * Handles animations and visibility changes on relevant buttons and UI components.
     * <p>
     * Features:
     * 1. Toggles the ability to select items for deletion.
     * 2. Shows or hides relevant UI components using animations when entering or exiting multi-select mode.
     * 3. Resets deletion states and updates the adapter list.
     * <p>
     * Dependencies:
     * - YoYo animations for button transitions.
     */
    public void setStageToolbarDelete(boolean canSelect) {

        mListNoteAdapter.setCanSelect(canSelect);
        noteBinding.statusBack.setVisibility(canSelect ? View.VISIBLE : View.GONE);
        noteBinding.search.setVisibility(canSelect ? View.GONE : View.VISIBLE);
        ((MainActivity) requireActivity()).setHamburgerMenuVisibility(!canSelect);

        isSelectable = canSelect;
        deleteNote.clear();

        mListNoteAdapter.notifyDataSetChanged();

        if (canSelect) {
            YoYo.with(Techniques.ZoomIn).duration(300).onEnd(animator -> {
                noteBinding.fabConfirmDelete.setVisibility(View.VISIBLE);
                noteBinding.fabCreatNote.setVisibility(View.GONE);
            }).playOn(noteBinding.fabConfirmDelete);
            YoYo.with(Techniques.ZoomOut).duration(300).playOn(noteBinding.fabCreatNote);
        } else {
            if (noteBinding.fabConfirmDelete.isShown()) {
                YoYo.with(Techniques.ZoomOut).duration(300).onEnd(animator -> {
                    noteBinding.fabConfirmDelete.setVisibility(View.GONE);
                    noteBinding.fabCreatNote.setVisibility(View.VISIBLE);
                }).playOn(noteBinding.fabConfirmDelete);
            }
            YoYo.with(Techniques.ZoomIn).duration(300).playOn(noteBinding.fabCreatNote);
            loadData();
        }
    }

    /**
     * sortListNote
     * <p>
     * Sorts the notes list in descending order based on their last update timestamp or creation ID if no timestamp exists.
     * This ensures the most recently updated notes are displayed first.
     * <p>
     * Features:
     * 1. Compare timestamps for sorting or fallback to note ID if no timestamp is provided.
     * 2. Notify adapter updates after sorting.
     * <p>
     * Dependencies:
     * - DateUtils for timestamp formatting.
     */
    public void sortListNote() {
        mListNoteAdapter.getNotesList().sort((order1, order2) -> {
            long time1;
            long time2;
            time1 = order1.dateUpdate == 0 ? order1.id : order1.dateUpdate;
            time2 = order2.dateUpdate == 0 ? order2.id : order2.dateUpdate;

            Log.d("TimeSort", order1.id + " - " + DateUtils.getStampByDate(new Date(order1.dateUpdate), DateUtils.DATE_FORMAT_1) + " - " + order2.id + " - " + DateUtils.getStampByDate(new Date(order2.dateUpdate), DateUtils.DATE_FORMAT_1));

            return Long.compare(time2, time1);
        });
        mListNoteAdapter.notifyDataSetChanged();
    }

    /**
     * showDialogConfirmDelete
     * <p>
     * Displays a confirmation dialog to the user to confirm deletion of selected notes.
     * Checks if any items are selected before proceeding with deletion.
     * <p>
     * Features:
     * 1. Dialog with Cancel and Delete buttons.
     * 2. Validate if any note is selected for deletion.
     * 3. Perform database operations for deletion if confirmed.
     * <p>
     * Dependencies:
     * - CustomDialog for creating dialog UI.
     * - dbService for database interaction.
     */
    public void showDialogConfirmDelete() {
        // Show confirmation dialog for deletion
        new CustomDialog(requireContext())
                .setTitle("Delete")
                .setMessage("Do you want to Delete ?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete Note and update data
                    if (deleteNote.isEmpty()) {
                        String message = requireContext().getResources().getString(R.string.must_select_one_item);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        dbService.archiveNormalNote(deleteNote);
                    }
                })
                .show();
    }


    /**
     * onResume
     * <p>
     * Lifecycle callback for fragment visibility.
     * Sets up Wearable data listeners and manages UI loading logic on data absence.
     * <p>
     * Features:
     * 1. Add listener for wearable data synchronization.
     * 2. If no notes are available, display loading animation.
     * 3. Delay data loading to ensure proper UI state changes.
     */
    @Override
    public void onResume() {
        super.onResume();

        Wearable.getDataClient(requireActivity()).addListener(this);

        if (deleteNote.isEmpty()) {

            noteBinding.notesRecyclerView.setVisibility(View.GONE);
            noteBinding.tvNoNote.setVisibility(View.GONE);

            noteBinding.progressBar.setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            Runnable runnable = () -> {
                noteBinding.progressBar.setVisibility(View.GONE);
                loadData();
            };

            handler.postDelayed(runnable, 500);
        }
    }

    /**
     * onBackPress
     * <p>
     * Handles back press actions based on the visibility of UI components and interaction modes.
     * Ensures proper navigation or exit depending on the current state of the search or multi-selection UI.
     * <p>
     * Features:
     * 1. Handle back press based on whether the search view or selection mode is active.
     * 2. Close search view or exit multi-select mode accordingly.
     */
    @Override
    public boolean onBackPress() {
        if (noteBinding.searchViewComponent.isShown()) {
            openOrCloseSearchView(false);
            setStageToolbarDelete(false);
            return false;
        } else if (isSelectable) {
            setStageToolbarDelete(false);
            return false;
        }
        return true;
    }

    // Loop through each event received from the DataEventBuffer
    // Check if the event type is TYPE_CHANGED and the URI matches the defined NOTES_DATA_PATH
    // Convert the DataItem to a DataMapItem to access its data
    // Extract JSON data from the DataMapItem under the key 'notes_data'
    // Log the received data for debugging purposes
    // Deserialize the JSON string into an array of Note objects using Gson
    // Convert the array into a List of Note objects
    // Pass the deserialized notes list to the database service for synchronization
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().equals(NOTES_DATA_PATH)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                String notesJson = dataMapItem.getDataMap().getString("notes_data");

                Log.d("MOBILE_SYNC", "DATA RECEIVED : " + notesJson);
                Note[] notesArray = new Gson().fromJson(notesJson, Note[].class);
                List<Note> notes = java.util.Arrays.asList(notesArray);

                dbService.syncNotesFromWear(notes);
            }
        }
    }

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_NOTE_ARCHIVED".equals(intent.getAction())) {
                Log.d("MOBILE_SYNC", "Note archived successfully");

                // Switch to the main thread for UI updates
                new Handler(Looper.getMainLooper()).post(() -> {
                    openOrCloseSearchView(false);
                    setStageToolbarDelete(false);
                    loadData();
                });

                List<Note> notes = dbService.getNotes();
                mobileSyncService.syncNotes(notes);

            } else if("SYNC_COMPLETED_ACTION".equals(intent.getAction())) {
                Log.d("MOBILE_SYNC", "Sync completed successfully");

                // Switch to the main thread for UI updates
                new Handler(Looper.getMainLooper()).post(() -> {
                    openOrCloseSearchView(false);
                    setStageToolbarDelete(false);
                    loadData();
                });
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_NOTE_ARCHIVED");
        filter.addAction("SYNC_COMPLETED_ACTION");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(syncReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(syncReceiver);
    }
}