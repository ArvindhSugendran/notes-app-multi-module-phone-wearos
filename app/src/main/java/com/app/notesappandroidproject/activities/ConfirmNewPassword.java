package com.app.notesappandroidproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.app.notesappandroidproject.R;
import com.app.notesappandroidproject.databinding.ActivityConfirmNewPasswordBinding;
import com.app.notesappandroidproject.utils.SharedPreferencesDB;


/**
 * Activity for confirming the user's new password
 * Handles UI initialization, validation, and navigation to the next activity.
 */
public class ConfirmNewPassword extends AppCompatActivity implements View.OnClickListener {


    // Declare the binding
    ActivityConfirmNewPasswordBinding confirmNewPasswordBinding = null;

    // Declare shared preferences
    SharedPreferencesDB sharedPreferencesDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the binding and bind to the xml
        confirmNewPasswordBinding = ActivityConfirmNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(confirmNewPasswordBinding.getRoot());

        initView();
    }


    // Initialize the view by setting on click listeners to the buttons
    // Set on Focus change listeners to animate the app bar layout
    // Set add text changed listeners to editText to monitor the data that is being entered in the text field
    private void initView() {

        sharedPreferencesDB = new SharedPreferencesDB(this);

        confirmNewPasswordBinding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                confirmNewPasswordBinding.statusBack.setImageResource(R.drawable.ic_arrow_down);
                confirmNewPasswordBinding.statusBack.setTag(R.drawable.ic_arrow_down);

            } else {
                //Expanded
                confirmNewPasswordBinding.statusBack.setImageResource(R.drawable.ic_arrow_back);
                confirmNewPasswordBinding.statusBack.setTag(R.drawable.ic_arrow_back);

            }
        });

        confirmNewPasswordBinding.splashLottie.setAnimation(R.raw.login_lottie);

        confirmNewPasswordBinding.newPasswordEt.setOnFocusChangeListener(focusChangeListener);
        confirmNewPasswordBinding.newPasswordEt.addTextChangedListener(textWatcher);
        confirmNewPasswordBinding.newPasswordEt.setOnClickListener(this);

        confirmNewPasswordBinding.confirmNewPasswordEt.setOnFocusChangeListener(focusChangeListener);
        confirmNewPasswordBinding.confirmNewPasswordEt.addTextChangedListener(textWatcher);
        confirmNewPasswordBinding.confirmNewPasswordEt.setOnClickListener(this);

        confirmNewPasswordBinding.statusBack.setOnClickListener(this);
        confirmNewPasswordBinding.doneButton.setOnClickListener(this);
    }


    // Animating the app bar layout
    View.OnFocusChangeListener focusChangeListener = (view, b) -> {
        if (b) {
            confirmNewPasswordBinding.appBarLayout.setExpanded(false, true);
        }
    };

    // Text watcher for input fields to get the text from the text fields
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not used
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean textFields = confirmNewPasswordBinding.newPasswordEt.getText().length() > 0
                    && confirmNewPasswordBinding.newPasswordEt.getText().length() > 0;

            confirmNewPasswordBinding.doneButton.setEnabled(textFields);
            confirmNewPasswordBinding.doneButton.setBackgroundTintList(ContextCompat.getColorStateList(ConfirmNewPassword.this,
                    textFields ? R.color.black : R.color.gray));

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    /** On click listeners to handle clicks for editText, floatingActionButtons
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == confirmNewPasswordBinding.newPasswordEt.getId()) {
            confirmNewPasswordBinding.appBarLayout.setExpanded(false, true);
        }

        if(view.getId() == confirmNewPasswordBinding.confirmNewPasswordEt.getId()) {
            confirmNewPasswordBinding.appBarLayout.setExpanded(false, true);
        }

        if(view.getId() == confirmNewPasswordBinding.statusBack.getId()) {
            int resourceID = (int) confirmNewPasswordBinding.statusBack.getTag();
            if (resourceID == R.drawable.ic_arrow_down) {
                confirmNewPasswordBinding.appBarLayout.setExpanded(true, true);
            } else if (resourceID == R.drawable.ic_arrow_back) {
                onBackPressed();
            }
        }


        // Once the password and confirm password is retrieved from the editText
        // we validate the password length and check if both the passwords are same
        // Once validation is passed we update the new password in the SharedPreferences

        if(view.getId() == confirmNewPasswordBinding.doneButton.getId()) {
            if (validatePass())
            {
                changePassword();
            }
        }
    }


    /** function to update the new password in shared Preferences
     */
    private void changePassword() {
        sharedPreferencesDB.saveString(SharedPreferencesDB.KEY_PASSWORD, confirmNewPasswordBinding.confirmNewPasswordEt.getText().toString().trim());
        toast("Updated Successfully");

        startActivity(new Intent(this, EnterAppPassword.class));
        finish();
    }

    /** Method to show toast message
     */
    public void toast(String content) {
        if (!TextUtils.isEmpty(content))
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }


     /**
     * Function to validate password and it returns the boolean value if the validation is succeeded or not
     */
    private boolean validatePass() {

        if (confirmNewPasswordBinding.newPasswordEt.getText().length() < 4) {
            confirmNewPasswordBinding.newPasswordEt.setError(getString(R.string.please_enter_dihits));
            return false;
        }

        if (confirmNewPasswordBinding.confirmNewPasswordEt.getText().length() < 4) {
            confirmNewPasswordBinding.confirmNewPasswordEt.setError(getString(R.string.please_enter_dihits));
            return false;
        }

        if (!confirmNewPasswordBinding.confirmNewPasswordEt.getText().toString().equals(confirmNewPasswordBinding.newPasswordEt.getText().toString())) {
            confirmNewPasswordBinding.confirmNewPasswordEt.setError(getString(R.string.pass_not_match));
            return false;
        }

        return true;
    }
}