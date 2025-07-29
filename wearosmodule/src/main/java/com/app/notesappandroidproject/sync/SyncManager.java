package com.app.notesappandroidproject.sync;

import android.content.Context;
import android.util.Log;

import com.app.notesappandroidproject.data.local.NoteEntity;
import com.app.notesappandroidproject.data.repository.NoteRepositoryImpl;
import com.app.notesappandroidproject.presentation.Note;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.util.List;

/**
 * SyncManager handles the synchronization of notes between the wearable device and the mobile app.
 * It uses Google's Wearable API to send data from a wearable to a mobile device.
 */
public class SyncManager {

    private final DataClient dataClient;

    private final Context context;

    /**
     * Constructor initializes the SyncManager with a given context and sets up the DataClient.
     * @param context The application context
     */
    public SyncManager(Context context) {
        this.context = context;
        this.dataClient = com.google.android.gms.wearable.Wearable.getDataClient(context);
    }

    /**
     * Synchronizes a list of notes from the wearable device to the mobile application.
     * Converts the notes list into JSON format and sends it using the Wearable DataClient API.
     * @param notes List of NoteEntity objects to send to the mobile app
     */
    public void syncNotesToMobileApp(List<NoteEntity> notes) {
        String notesJson = new Gson().toJson(notes);

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/notes_wear_to_mobile");
        putDataMapRequest.getDataMap().putString("notes_data", notesJson);

        PutDataRequest request = putDataMapRequest.asPutDataRequest().setUrgent();
        dataClient.putDataItem(request)
                .addOnSuccessListener(aVoid -> Log.d("WEAR_SYNC", "Notes data sent to Mobile Phone successfully. "+ notesJson))
                .addOnFailureListener(e -> Log.e("WEAR_SYNC", "Failed to send notes data to Mobile Phone.", e));

        Wearable.getNodeClient(context).getConnectedNodes()
                .addOnSuccessListener(nodes -> {
                    for (Node node : nodes) {
                        Log.d("WEAR_SYNC", "Connected node: " + node.getId() + ", " + node.getDisplayName());
                    }
                });
    }
}
