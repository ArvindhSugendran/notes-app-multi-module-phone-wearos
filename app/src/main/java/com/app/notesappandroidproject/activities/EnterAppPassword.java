package com.app.notesappandroidproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import com.app.notesappandroidproject.R;
import com.app.notesappandroidproject.databinding.ActivityEnterAppPasswordBinding;
import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.services.DBService;
import com.app.notesappandroidproject.utils.SharedPreferencesDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;

import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;

import java.util.List;

/**
 * Activity for creating the user's password.
 * Handles UI initialization, validation, and navigation to the next activity.
 */
public class EnterAppPassword extends AppCompatActivity implements View.OnClickListener {

    ActivityEnterAppPasswordBinding passwordBinding = null;
    String appPassword = "";

    public static final String NOTES_DATA_PATH = "/notes_wear_to_mobile";

    SharedPreferencesDB sharedPreferencesDB;
    DBService dbService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordBinding = ActivityEnterAppPasswordBinding.inflate(getLayoutInflater());
        setContentView(passwordBinding.getRoot());

        initView();
    }

    /**
     * Fetch data from the wearable Data Layer and synchronize it with the mobile app's database.
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

                            Log.d("MOBILE_SYNC", "FETCHED DATA IN LAUNCH : " + notesJson);
                            Note[] notesArray = new Gson().fromJson(notesJson, Note[].class);
                            List<Note> notes = java.util.Arrays.asList(notesArray);

                            dbService.syncNotesFromWear(notes);
                        }
                    }
                    dataItems.release(); // Important: Release the buffer after processing
                    startMainActivity();
                } else {
                    startMainActivity();
                    Log.e("MOBILE_SYNC", "Failed to fetch data items.", task.getException());
                }
            }
        });
    }

    /**
     * Initialize the view components and setup listeners for user interaction.
     */
    private void initView() {
        sharedPreferencesDB = new SharedPreferencesDB(this);

        dbService = new DBService(EnterAppPassword.this);

        appPassword = sharedPreferencesDB.getString(SharedPreferencesDB.KEY_PASSWORD);

        passwordBinding.statusType.setText(appPassword.isEmpty() ? getString(R.string.set_password_title) : getString(R.string.enter_app_password_title));
        passwordBinding.statusContent.setText(appPassword.isEmpty() ? getString(R.string.set_password_description) : getString(R.string.enter_app_password_description));
        passwordBinding.appPasswordEt.setHint(appPassword.isEmpty() ? getString(R.string.set_app_password) : getString(R.string.app_password));
        passwordBinding.forgotpasswordbtn.setVisibility(appPassword.isEmpty() ? View.GONE : View.VISIBLE);
        passwordBinding.actionButton.setText(appPassword.isEmpty() ? getString(R.string.next) : getString(R.string.open));

        passwordBinding.splashLottie.setAnimation(R.raw.login_lottie);

        passwordBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    passwordBinding.statusBack.setVisibility(View.VISIBLE);
                } else {
                    //Expanded
                    passwordBinding.statusBack.setVisibility(View.INVISIBLE);
                }
            }
        });

        passwordBinding.appPasswordEt.setOnFocusChangeListener(focusChangeListener);
        passwordBinding.appPasswordEt.addTextChangedListener(textWatcher);
        passwordBinding.appPasswordEt.setOnClickListener(this);

        passwordBinding.statusBack.setOnClickListener(this);
        passwordBinding.actionButton.setOnClickListener(this);
        passwordBinding.forgotpasswordbtn.setOnClickListener(this);
    }

    // Listener to handle focus changes on the password input field
    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                passwordBinding.appBarLayout.setExpanded(false, true);
            }
        }
    };

    // Text watcher for input fields
    /**
     * TextWatcher to monitor changes in the input field and update the button's state accordingly.
     */
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not used
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean appPasswordTextField = passwordBinding.appPasswordEt.getText().length() > 0;

            passwordBinding.actionButton.setEnabled(appPasswordTextField);
            passwordBinding.actionButton.setBackgroundTintList(ContextCompat.getColorStateList(EnterAppPassword.this,
                    appPasswordTextField ? R.color.black : R.color.gray));

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Handle button click actions such as checking the password, starting the reset password activity, or expanding/collapsing the app bar.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == passwordBinding.actionButton.getId()) {
            check();
        }

        if (view.getId() == passwordBinding.appPasswordEt.getId()) {
            passwordBinding.appBarLayout.setExpanded(false, true);
        }

        if (view.getId() == passwordBinding.statusBack.getId()) {
            passwordBinding.appBarLayout.setExpanded(true, true);
        }

        if(view.getId() == passwordBinding.forgotpasswordbtn.getId()) {
            startActivity(new Intent(this, ResetPassword.class));
        }
    }

    /**
     * Validate the entered password or set a new password if none exists.
     */
    private void check() {
        if (TextUtils.isEmpty(appPassword)) {
            if (!TextUtils.isEmpty(passwordBinding.appPasswordEt.getText().toString().trim())) {
                if (passwordBinding.appPasswordEt.getText().toString().trim().length() >= 4) {
                    Intent intent = new Intent(EnterAppPassword.this, ConfirmPassword.class);
                    intent.putExtra("appPassword", passwordBinding.appPasswordEt.getText().toString().trim());
                    startActivity(intent);
                    finish();
                } else {
                    passwordBinding.appPasswordEt.setError("Password length must be greater than 4 characters");
                }
            }
        } else {
            if (!TextUtils.isEmpty(passwordBinding.appPasswordEt.getText().toString().trim())) {
                if (passwordBinding.appPasswordEt.getText().toString().trim().equals(appPassword)) {
                    fetchDataFromDataLayer();
                } else {
                    passwordBinding.appPasswordEt.setError("Wrong password");
                }
            }
        }

    }

    /**
     * Start the main activity after successful password validation or data sync.
     */
    private void startMainActivity() {
        Intent intent = new Intent(EnterAppPassword.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    /**
     * Handle the back press to expand the app bar if it is collapsed, otherwise perform default action.
     */
    @Override
    public void onBackPressed() {
        if (passwordBinding.appBarLayout.isLifted()) {
            passwordBinding.appBarLayout.setExpanded(true, true);
        } else {
            super.onBackPressed();
        }

    }
}