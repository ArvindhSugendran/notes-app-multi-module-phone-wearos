package com.app.notesappandroidproject.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.notesappandroidproject.databinding.LayoutToolbarSearchBinding;

/**
 * SearchViewComponent
 * <p>
 * This custom UI component represents a search bar widget with search text handling and close button functionality.
 * It allows users to input and dynamically observe text changes in a search bar. Additionally, it provides a
 * "clear search" button to reset the input field and notify listeners of close actions.
 * <p>
 * Features:
 * 1. Dynamically notify text change events through `SearchViewListener`.
 * 2. Handle click events on a clean button to clear input and trigger a callback.
 * 3. Observe input text changes using `TextWatcher`.
 * 4. Use data binding (`LayoutToolbarSearchBinding`) for a clean and maintainable way to interact with UI elements.
 * 5. Allow external code to set the listener via `setSearchViewListener`.
 * <p>
 * Dependencies:
 * - Data binding with `LayoutToolbarSearchBinding`.
 */
public class SearchViewComponent extends LinearLayout implements View.OnClickListener {

    private SearchViewListener mSearchViewListener;
    private final LayoutToolbarSearchBinding binding;

    /**
     * Interface to communicate events back to the parent or external classes.
     */
    public interface SearchViewListener {
        void onTextChangeListener(String txt);

        void onCloseSearchView();
    }

    /**
     * Set the listener for handling events like text change and close button clicks.
     *
     * @param mSearchViewListener Listener implementation.
     */
    public void setSearchViewListener(SearchViewListener mSearchViewListener) {
        this.mSearchViewListener = mSearchViewListener;
    }

    /**
     * Constructor to initialize the SearchViewComponent with provided context and attributes.
     * Inflates the layout and initializes the control logic.
     *
     * @param context Context from the parent view.
     * @param attrs   AttributeSet if any XML attributes are defined.
     */
    public SearchViewComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = LayoutToolbarSearchBinding.inflate(LayoutInflater.from(context), this, false);
        addView(binding.getRoot());
        initControl();
    }

    /**
     * Initialize UI controls and listeners.
     * Sets click listener on the clear button and adds text change observation to the search field.
     */
    private void initControl() {
        binding.imCleanSearch.setOnClickListener(this);
        binding.edtSearch.addTextChangedListener(textWatcher);
    }


    /**
     * TextWatcher to listen for text changes in the search bar and notify listeners dynamically.
     */
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mSearchViewListener != null)
                mSearchViewListener.onTextChangeListener(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    @Override
    public void onClick(View view) {
        if(view.getId() == binding.imCleanSearch.getId()) {
            if (mSearchViewListener != null)
                mSearchViewListener.onCloseSearchView();
            binding.edtSearch.setText("");
        }
    }

    /**
     * Set or reset the text in the search field programmatically.
     *
     * @param txt Text value to set; if null, clears the input field.
     */
    public void setEdtSearch(String txt) {
        binding.edtSearch.setText(txt == null ? "" : txt);
    }
}

