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
import com.app.notesappandroidproject.databinding.ActivityResetPasswordBinding;
import com.app.notesappandroidproject.domain.question.Question;
import com.app.notesappandroidproject.services.DBService;


/**
 * Activity for resetting the password
 * Handles UI initialization, validation, and navigation to the next activity.
 */
public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    // Declare ResetPassword binding
    ActivityResetPasswordBinding resetPasswordBinding = null;

    // Declare DB service to access repository functions
    DBService dbService;

    String answer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize reset password binding
        resetPasswordBinding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(resetPasswordBinding.getRoot());

        initView();

    }


    /**
     * Initialize the view with DB service and all the necessary buttons and set on click listeners
     */
    private void initView() {

        dbService = new DBService(this);

        Question question = dbService.getQuestion();

        resetPasswordBinding.securityAnswer.setHint(question.question);

        answer = question.answer;

        resetPasswordBinding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                resetPasswordBinding.statusBack.setImageResource(R.drawable.ic_arrow_down);
                resetPasswordBinding.statusBack.setTag(R.drawable.ic_arrow_down);

            } else {
                //Expanded
                resetPasswordBinding.statusBack.setImageResource(R.drawable.ic_arrow_back);
                resetPasswordBinding.statusBack.setTag(R.drawable.ic_arrow_back);

            }
        });

        resetPasswordBinding.splashLottie.setAnimation(R.raw.login_lottie);

        resetPasswordBinding.securityAnswer.setOnFocusChangeListener(focusChangeListener);
        resetPasswordBinding.securityAnswer.addTextChangedListener(textWatcher);
        resetPasswordBinding.securityAnswer.setOnClickListener(this);

        resetPasswordBinding.statusBack.setOnClickListener(this);
        resetPasswordBinding.doneButton.setOnClickListener(this);
    }

    // Listener to handle focus changes on the password input field
    View.OnFocusChangeListener focusChangeListener = (view, b) -> {
        if (b) {
            resetPasswordBinding.appBarLayout.setExpanded(false, true);
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
            boolean securityAnswerTextField = resetPasswordBinding.securityAnswer.getText().length() > 0;

            resetPasswordBinding.doneButton.setEnabled(securityAnswerTextField);
            resetPasswordBinding.doneButton.setBackgroundTintList(ContextCompat.getColorStateList(ResetPassword.this,
                    securityAnswerTextField ? R.color.black : R.color.gray));

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Handle button click actions such as checking the password, or expanding/collapsing the app bar.
     */
    @Override
    public void onClick(View view) {

        if (view.getId() == resetPasswordBinding.securityAnswer.getId()) {
            resetPasswordBinding.appBarLayout.setExpanded(false, true);
        }

        if (view.getId() == resetPasswordBinding.statusBack.getId()) {
            int resourceID = (int) resetPasswordBinding.statusBack.getTag();
            if (resourceID == R.drawable.ic_arrow_down) {
                resetPasswordBinding.appBarLayout.setExpanded(true, true);
            } else if (resourceID == R.drawable.ic_arrow_back) {
                onBackPressed();
            }
        }

        if (view.getId() == resetPasswordBinding.doneButton.getId()) {
            check();
        }
    }

    /**
     * Validate the security question and security answer.
     */
    private void check() {
        if (!TextUtils.isEmpty(resetPasswordBinding.securityAnswer.getText().toString().trim())) {
            if (answer.equalsIgnoreCase(resetPasswordBinding.securityAnswer.getText().toString().trim())) {
                startActivity(new Intent(ResetPassword.this, ConfirmNewPassword.class));
                finish();
            } else {
                resetPasswordBinding.securityAnswer.setError("Answer does not match");
            }
        } else {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
        }
    }
}