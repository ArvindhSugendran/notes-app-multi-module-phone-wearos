package com.app.notesappandroidproject.service;

import android.util.Log;

import androidx.annotation.NonNull;
import com.app.notesappandroidproject.data.local.NoteEntity;
import com.app.notesappandroidproject.data.repository.NoteRepositoryImpl;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * NoteWearableListenerServicePhoneToWatch listens for data changes from the wearable device
 * and processes synchronization events from the mobile application to the wearable.
 */
@AndroidEntryPoint
public class NoteWearableListenerServicePhoneToWatch extends WearableListenerService {

    @Inject
    NoteRepositoryImpl noteRepository;

    public static final String NOTES_DATA_PATH = "/notes_mobile_to_wear";

    /**
     * Called when data changes are detected on the wearable communication channel.
     * Processes the data if it pertains to the expected data path and synchronizes it with the repository.
     * @param dataEvents A collection of events related to wearable communication
     */
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEvents) {
        // Iterate through all data change events
        for (DataEvent event : dataEvents) {
            // Check if the event indicates a change and matches the expected data path
            if (event.getType() == DataEvent.TYPE_CHANGED && Objects.equals(event.getDataItem().getUri().getPath(), NOTES_DATA_PATH)) {
                // Parse the data received from the event
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                String notesJson = dataMapItem.getDataMap().getString("notes_data");

                Log.d("WEAR_SYNC", "DATA RECEIVED : " + notesJson);
                NoteEntity[] notesArray = new Gson().fromJson(notesJson, NoteEntity[].class);
                List<NoteEntity> notes = java.util.Arrays.asList(notesArray);

                // Trigger the repository synchronization process with the parsed notes
                noteRepository.syncNotesFromMobile(notes);
            }
        }
    }
}
