package com.app.notesappandroidproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.PopupMenu;

import com.app.notesappandroidproject.R;
import com.app.notesappandroidproject.databinding.ActivityConfirmPasswordBinding;
import com.app.notesappandroidproject.domain.question.Question;
import com.app.notesappandroidproject.services.DBService;
import com.app.notesappandroidproject.utils.Config;
import com.app.notesappandroidproject.utils.SharedPreferencesDB;

/**
 * Activity for confirming the user's password and setting up a security question.
 * Handles UI initialization, validation, and navigation to the next activity.
 */
public class ConfirmPassword extends AppCompatActivity implements View.OnClickListener{

    ActivityConfirmPasswordBinding confirmPasswordBinding = null;

    SharedPreferencesDB sharedPreferencesDB;

    String appPassword = "";

    DBService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        confirmPasswordBinding = ActivityConfirmPasswordBinding.inflate(getLayoutInflater());
        setContentView(confirmPasswordBinding.getRoot());

        initView();
    }

    /**
     * Initializes the UI components, retrieves data from the intent, and sets up listeners.
     */
    private void initView() {

        Intent intent = getIntent();
        appPassword= intent.getStringExtra("appPassword");

        sharedPreferencesDB = new SharedPreferencesDB(this);

        dbService = new DBService(this);

        confirmPasswordBinding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                confirmPasswordBinding.statusBack.setImageResource(R.drawable.ic_arrow_down);
                confirmPasswordBinding.statusBack.setTag(R.drawable.ic_arrow_down);

            } else {
                //Expanded
                confirmPasswordBinding.statusBack.setImageResource(R.drawable.ic_arrow_back);
                confirmPasswordBinding.statusBack.setTag(R.drawable.ic_arrow_back);

            }
        });

        confirmPasswordBinding.splashLottie.setAnimation(R.raw.login_lottie);

        confirmPasswordBinding.confirmPasswordEt.setOnFocusChangeListener(focusChangeListener);
        confirmPasswordBinding.confirmPasswordEt.addTextChangedListener(textWatcher);
        confirmPasswordBinding.confirmPasswordEt.setOnClickListener(this);

        confirmPasswordBinding.securityQuestionEt.setOnFocusChangeListener(focusChangeListener);
        confirmPasswordBinding.securityQuestionEt.addTextChangedListener(textWatcher);
        confirmPasswordBinding.securityQuestionEt.setOnClickListener(this);

        confirmPasswordBinding.statusBack.setOnClickListener(this);
        confirmPasswordBinding.doneButton.setOnClickListener(this);
        confirmPasswordBinding.changeQuestion.setOnClickListener(this);
    }

    /**
     * Listener for managing focus changes to input fields, collapsing the AppBar when focused.
     */
    View.OnFocusChangeListener focusChangeListener = (view, b) -> {
        if (b) {
            confirmPasswordBinding.appBarLayout.setExpanded(false, true);
        }
    };


    // Text watcher for input fields
    /**
     * TextWatcher for monitoring changes in the input fields and enabling the done button
     * based on the validity of the inputs.
     */
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not used
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean textFields = confirmPasswordBinding.confirmPasswordEt.getText().length() > 0
                                                  && confirmPasswordBinding.securityQuestionEt.getText().length() > 0;

            confirmPasswordBinding.doneButton.setEnabled(textFields);
            confirmPasswordBinding.doneButton.setBackgroundTintList(ContextCompat.getColorStateList(ConfirmPassword.this,
                    textFields ? R.color.black : R.color.gray));

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };



    @Override
    public void onClick(View view) {
        if(view.getId() == confirmPasswordBinding.confirmPasswordEt.getId()) {
            confirmPasswordBinding.appBarLayout.setExpanded(false, true);
        }

        if(view.getId() == confirmPasswordBinding.securityQuestionEt.getId()) {
            confirmPasswordBinding.appBarLayout.setExpanded(false, true);
        }

        if(view.getId() == confirmPasswordBinding.statusBack.getId()) {
            int resourceID = (int) confirmPasswordBinding.statusBack.getTag();
            if (resourceID == R.drawable.ic_arrow_down) {
                confirmPasswordBinding.appBarLayout.setExpanded(true, true);
            } else if (resourceID == R.drawable.ic_arrow_back) {
                onBackPressed();
            }
        }

        if(view.getId() == confirmPasswordBinding.changeQuestion.getId()) {
            PopupMenu popup = new PopupMenu(ConfirmPassword.this, confirmPasswordBinding.changeQuestion);
            for (int i = 0; i < Config.LST_QUESTION.length; i++) {
                popup.getMenu().add(0, i, 0, Config.LST_QUESTION[i]);
            }
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == -1) {
                    confirmPasswordBinding.securityQuestionEt.setVisibility(View.VISIBLE);
                } else {
                    confirmPasswordBinding.securityQuestionEt.setHint(item.getTitle());
                    confirmPasswordBinding.securityQuestionEt.setText("");
                }
                return true;
            });
            popup.show();
        }

        if(view.getId() == confirmPasswordBinding.doneButton.getId()) {
            check();
        }
    }

    /**
     * Validates the password and security question inputs and performs the appropriate action.
     * Inserts the Security question and answer into the database
     * Inserts the new password in sharedPreferences
     */
    private void check() {
        if(!(TextUtils.isEmpty(confirmPasswordBinding.confirmPasswordEt.getText().toString().trim()) || TextUtils.isEmpty(appPassword) || TextUtils.isEmpty(confirmPasswordBinding.securityQuestionEt.getText().toString().trim()))) {
            if (appPassword.trim().length() >= 4 )
            {
                if (appPassword.trim().equals(confirmPasswordBinding.confirmPasswordEt.getText().toString().trim()))
                {
                    sharedPreferencesDB.saveString(SharedPreferencesDB.KEY_PASSWORD, appPassword);

                    dbService.insertQuestion(new Question(confirmPasswordBinding.securityQuestionEt.getHint().toString(),
                            confirmPasswordBinding.securityQuestionEt.getText().toString().trim()));

                    Intent intent = new Intent(this, EnterAppPassword.class);
                    startActivity(intent);
                    finish();

                } else {
                    confirmPasswordBinding.confirmPasswordEt.setError("Password do not match");
                }
            }
        }
    }


    /**
     * Handle the back press to expand the app bar if it is collapsed, otherwise perform default action.
     */
    @Override
    public void onBackPressed() {
        if (confirmPasswordBinding.appBarLayout.isLifted()) {
            confirmPasswordBinding.appBarLayout.setExpanded(true, true);
        } else
        { super.onBackPressed();
        }

    }
}