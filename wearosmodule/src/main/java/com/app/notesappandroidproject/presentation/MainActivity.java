package com.app.notesappandroidproject.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.notesappandroidproject.R;

import com.app.notesappandroidproject.data.local.NoteEntity;
import com.app.notesappandroidproject.sync.WearSyncService;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * MainActivity
 * <p>
 * This activity serves as the main entry point for the Notes application. It is responsible for managing
 * the user interface related to displaying notes, observing changes from the database, and synchronizing
 * data with wearable devices over the Data Layer API.
 * <p>
 * Features:
 * 1. Synchronize notes with wearable devices using Google Wearable's Data Layer API.
 * 2. Observe changes in the notes database and dynamically update the UI using the ViewModel.
 * 3. Handle first-time synchronization with wearable devices.
 * 4. Configure and handle user interactions with the notes UI, including search and note creation.
 * <p>
 * Responsibilities:
 * - Set up and initialize the user interface.
 * - Observe changes in the notes' database and handle synchronization.
 * - Implement RecyclerView for displaying a dynamic list of notes.
 * - Handle user interactions with search and note creation functionalities.
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    com.app.notesappandroidproject.databinding.ActivityMainBinding binding;
    View rootView = null;
    private ListNoteAdapter adapter;
    private NoteViewModel noteViewModel;

    public static final String NOTES_DATA_PATH = "/notes_mobile_to_wear";

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "wearable_sync_prefs";
    private static final String KEY_FIRST_TIME_SYNC = "first_time_sync";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.app.notesappandroidproject.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);

        init();
        configureUI();

        if (isFirstTime()) {
            Log.d("WEAR_SYNC", "First-time sync detected. Fetching all buffered data.");
            fetchDataFromDataLayer();
        } else {
            Log.d("WEAR_SYNC", "Not the first time. No need to fetch cached data.");
            observeNotes();
        }
    }

    /**
     * Fetch data from the wearable device's Data Layer for first-time synchronization.
     * This method fetches all notes from the wearable device's data layer to ensure data consistency.
     */
    private void fetchDataFromDataLayer() {
        DataClient dataClient = Wearable.getDataClient(this);

        // Retrieve all data items from the Data Layer
        Task<DataItemBuffer> dataItemsTask = dataClient.getDataItems();
        dataItemsTask.addOnCompleteListener(new OnCompleteListener<DataItemBuffer>() {
            @Override
            public void onComplete(@NonNull Task<DataItemBuffer> task) {
                if (task.isSuccessful()) {
                    DataItemBuffer dataItems = task.getResult();

                    for (DataItem dataItem : dataItems) {
                        String path = dataItem.getUri().getPath();
                        if (NOTES_DATA_PATH.equals(path)) {
                            DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                            String notesJson = dataMap.getString("notes_data");

                            Log.d("WEAR_SYNC", "FETCHED DATA IN LAUNCH : " + notesJson);
                            NoteEntity[] notesArray = new Gson().fromJson(notesJson, NoteEntity[].class);
                            List<NoteEntity> notes = java.util.Arrays.asList(notesArray);

                            noteViewModel.syncNotesFromMobile(notes);

                        }
                    }
                    dataItems.release(); // Important: Release the buffer after processing
                    observeNotes();
                    sharedPreferences.edit().putBoolean(KEY_FIRST_TIME_SYNC, false).apply();
                } else {
                    Log.e("WEAR_SYNC", "Failed to fetch data items.", task.getException());
                }
            }
        });
    }

    /**
     * Initialize core dependencies and settings such as SharedPreferences and ViewModel.
     */
    private void init() {

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        //check if the wear os is round or square
        boolean isRound = getResources().getConfiguration().isScreenRound();

        int padding = getResources().getDimensionPixelOffset(R.dimen.round_padding);

        //if the wear is round then apply a padding of 15 dp to the Box Inset Layout
        if(isRound) {
            binding.relativeDesign.setPadding(padding, padding, padding, padding);
        }
    }

    /**
     * Configure UI-related listeners and RecyclerView settings.
     */
    private void configureUI() {
        binding.searchNote.setOnClickListener(this);
        binding.fabCreateNote.setOnClickListener(this);
        binding.imCleanSearch.setOnClickListener(this);
        binding.edtSearch.addTextChangedListener(textWatcher);
        setupRecyclerView();
    }

    /**
     * Set up the RecyclerView with an adapter to display note data.
     */
    private void setupRecyclerView() {
        adapter = new ListNoteAdapter(new ArrayList<>()); // Start with an empty list
        binding.notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.notesRecyclerView.setAdapter(adapter);

        adapterItemClickListener();
    }

    /**
     * Observe changes in the database and synchronize data changes to the UI.
     */
    public void adapterItemClickListener() {
        adapter.setFilterListener(filteredNotesList -> {
            if (binding.llSearch.isShown()) {
                binding.notesRecyclerView.setVisibility(adapter.getNotesList().isEmpty() ? View.GONE : View.VISIBLE);
                binding.tvNoNote.setVisibility(adapter.getNotesList().isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        adapter.setmItemClickListener(new ListNoteAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(Note mNote) {
                openDetailScreen(mNote);
                if (binding.llSearch.isShown())
                    openOrCloseSearchView(false);
            }

        });
    }

    private void openDetailScreen(Note mNote) {
        Intent intent = new Intent(this, CreateNoteActivity.class);
        if (mNote != null) {
            intent.putExtra("NOTE_DATA", mNote);
        }
        startActivity(intent);
    }

    /**
     * Observe changes in the database and synchronize data changes to the UI.
     */
    private void observeNotes() {
        noteViewModel.getNotes().observe(this, notes -> {

            Log.d("WEAR_SYNC", "Observe Notes From Activity " + notes.size());

            if(!notes.isEmpty()) {
                WearSyncService wearSyncService = new WearSyncService(MainActivity.this);
                wearSyncService.syncNotes(notes);
            }

            notes.sort((note1, note2) -> {
                long time1;
                long time2;
                time1 = note1.getDateUpdate() == 0 ? note1.getId() : note1.getDateUpdate();
                time2 = note2.getDateUpdate() == 0 ? note2.getId() : note2.getDateUpdate();

                return Long.compare(time2, time1);
            });

            binding.notesRecyclerView.setVisibility(notes.isEmpty() ? View.GONE : View.VISIBLE);
            binding.tvNoNote.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);

            adapter.updateNotes(notes);

        });
    }


    private boolean isFirstTime() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_SYNC, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isFirstTime()) {
            noteViewModel.refreshNotes();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            adapter.getFilter().filter(charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("SYNC_COMPLETED_ACTION".equals(intent.getAction())) {
                String status = intent.getStringExtra("status");
                if ("success".equals(status)) {
                    Log.d("WEAR_SYNC", "Sync done successfully");
                    noteViewModel.refreshNotes();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("SYNC_COMPLETED_ACTION");
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.searchNote.getId()) {
            openOrCloseSearchView(true);
        }

        if(v.getId() == binding.imCleanSearch.getId()) {
            openOrCloseSearchView(false);
            binding.edtSearch.setText("");
        }

        if(v.getId() == binding.fabCreateNote.getId()) {
            openDetailScreen(null);
        }
    }

    @Override
    public void addMenuProvider(@NonNull MenuProvider provider, @NonNull LifecycleOwner owner, @NonNull Lifecycle.State state) {

    }

    /**
     * Handles the logic for opening and closing the search view with animations.
     * Toggles visibility of the search view and the FAB button while resetting or observing notes.
     * @param canSearch Boolean to determine if the search view should be shown or hidden.
     */
    public void openOrCloseSearchView(boolean canSearch) {
        binding.llSearch.setVisibility(canSearch ? View.VISIBLE : View.GONE);

        YoYo.with(canSearch ? Techniques.ZoomOut : Techniques.ZoomIn).duration(300).playOn(binding.fabCreateNote);
        binding.fabCreateNote.setVisibility(canSearch ? View.GONE : View.VISIBLE);

        if (canSearch) {
            binding.edtSearch.setText("");
        } else {
            observeNotes();
    }
    }

}