package com.app.notesappandroidproject.data.dataSource;

import android.content.Context;
import android.util.Log;

import com.app.notesappandroidproject.domain.note.Note;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.util.List;


// Manages synchronization of notes data between the mobile app and Wear OS devices.
// Utilizes Google's Wearable API for communication and Gson for JSON serialization.
public class SyncManager {

    private Context context;
    private DataClient dataClient;

    // Constructor initializes the context and sets up the DataClient for Wear OS communication.
    public SyncManager(Context context) {
        this.context = context;
        this.dataClient = com.google.android.gms.wearable.Wearable.getDataClient(context);
    }

    // Synchronizes the provided list of notes to Wear OS using Google's DataClient API.
    // Convert the list of notes into a JSON string for easy data transfer.
    // Create a request to send data from the mobile app to the connected Wear OS device.
    // Set up the request as urgent to prioritize timely delivery.
    // Send the data to Wear OS and log the success or failure of the operation.
    // Log connected nodes (Wear OS devices) to ensure they are connected.
    public void syncNotesToWearOS(List<Note> notes) {
        String notesJson = new Gson().toJson(notes);

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/notes_mobile_to_wear");
        putDataMapRequest.getDataMap().putString("notes_data", notesJson);

        PutDataRequest request = putDataMapRequest.asPutDataRequest().setUrgent();
        dataClient.putDataItem(request)
                .addOnSuccessListener(aVoid -> Log.d("MOBILE_SYNC", "Notes data sent to Wear OS successfully. "+notesJson))
                .addOnFailureListener(e -> Log.e("MOBILE_SYNC", "Failed to send notes data to Wear OS.", e));

        Wearable.getNodeClient(context).getConnectedNodes()
                .addOnSuccessListener(nodes -> {
                    for (Node node : nodes) {
                        Log.d("MOBILE_SYNC", "Connected node: " + node.getId() + ", " + node.getDisplayName());
                    }
                });
    }
}
