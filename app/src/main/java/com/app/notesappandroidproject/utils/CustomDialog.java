package com.app.notesappandroidproject.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.InsetDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.app.notesappandroidproject.R;


/**
 * CustomDialog extends AlertDialog.Builder to provide a custom implementation for creating and displaying dialogs.
 * It allows customization of dialog layout, title, message, and button actions.
 */
public class CustomDialog extends AlertDialog.Builder {

    private View dialog_design; // View for custom dialog layout
    private AlertDialog dialog; // AlertDialog instance for the dialog

    /**
     * Constructs a CustomDialog object.
     *
     * @param context The Context in which the dialog should appear.
     */
    public CustomDialog(Context context) {
        super(context);
        load(context); // Load custom dialog layout
    }

    /**
     * Inflates the custom dialog layout and initializes its components.
     *
     * @param context The Context in which the dialog should appear.
     */
    private void load(Context context) {
        // Inflate custom dialog layout
        dialog_design = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);

        // Initially hide title, message, and buttons layout
        dialog_design.findViewById(R.id.mTitle).setVisibility(View.GONE);
        dialog_design.findViewById(R.id.mMessage).setVisibility(View.GONE);
        dialog_design.findViewById(R.id.mButtons).setVisibility(View.GONE);

        // Set the custom layout for the dialog
        setView(dialog_design);
    }

    /**
     * Sets the title of the dialog.
     *
     * @param title The title to be set for the dialog.
     * @return This CustomDialog object to allow for chaining of method calls.
     */
    public CustomDialog setTitle(@Nullable CharSequence title) {
        // Make title view visible and set its text
        dialog_design.findViewById(R.id.mTitle).setVisibility(View.VISIBLE);
        ((TextView) dialog_design.findViewById(R.id.mTitle)).setText(title);
        return this;
    }

    /**
     * Sets the message of the dialog.
     *
     * @param message The message to be set for the dialog.
     * @return This CustomDialog object to allow for chaining of method calls.
     */
    public CustomDialog setMessage(@Nullable CharSequence message) {
        // Make message view visible and set its text
        dialog_design.findViewById(R.id.mMessage).setVisibility(View.VISIBLE);
        ((TextView) dialog_design.findViewById(R.id.mMessage)).setText(message);
        return this;
    }

    /**
     * Sets the positive button of the dialog.
     *
     * @param text     The text to be displayed on the positive button.
     * @param listener The OnClickListener to be invoked when the positive button is clicked.
     * @return This CustomDialog object to allow for chaining of method calls.
     */
    public CustomDialog setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        // Make buttons layout and positive button visible, set button text, and handle click event
        dialog_design.findViewById(R.id.mButtons).setVisibility(View.VISIBLE);
        dialog_design.findViewById(R.id.mButtonPos).setVisibility(View.VISIBLE);
        ((Button) dialog_design.findViewById(R.id.mButtonPos)).setText(text);
        dialog_design.findViewById(R.id.mButtonPos).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Invoke listener's onClick method and dismiss dialog
                if (listener != null) listener.onClick(dialog, 0);
                dismiss();
            }
        });
        return this;
    }

    /**
     * Sets the negative button of the dialog.
     *
     * @param text     The text to be displayed on the negative button.
     * @param listener The OnClickListener to be invoked when the negative button is clicked.
     * @return This CustomDialog object to allow for chaining of method calls.
     */
    public CustomDialog setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        // Make buttons layout and negative button visible, set button text, and handle click event
        dialog_design.findViewById(R.id.mButtons).setVisibility(View.VISIBLE);
        dialog_design.findViewById(R.id.mButtonNeg).setVisibility(View.VISIBLE);
        ((Button) dialog_design.findViewById(R.id.mButtonNeg)).setText(text);
        dialog_design.findViewById(R.id.mButtonNeg).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Invoke listener's onClick method and dismiss dialog
                if (listener != null) listener.onClick(dialog, 0);
                dismiss();
            }
        });
        return this;
    }

    /**
     * Dismisses the dialog if it is showing.
     */
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * Creates the AlertDialog using the builder configuration.
     *
     * @return The created AlertDialog.
     */
    @NonNull
    public AlertDialog create() {
        dialog = super.create();
        return dialog;
    }

    /**
     * Shows the dialog with customized appearance.
     *
     * @return The displayed AlertDialog.
     */
    public AlertDialog show() {
        try {
            dialog = super.show();
            // Make dialog background transparent
            InsetDrawable background = (InsetDrawable) dialog.getWindow().getDecorView().getBackground();
            background.setAlpha(0);
        } catch (Exception ignored) {
        }
        return dialog;
    }
}
