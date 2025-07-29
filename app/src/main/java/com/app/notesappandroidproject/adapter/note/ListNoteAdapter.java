package com.app.notesappandroidproject.adapter.note;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.notesappandroidproject.databinding.NotesItemBinding;
import com.app.notesappandroidproject.domain.note.Note;
import com.app.notesappandroidproject.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ListNoteAdapter extends RecyclerView.Adapter<ListNoteAdapter.NotesViewHolder> implements Filterable {

    private List<Note> notes; // List of notes

    private final List<Note> filteredNotes; // List of filtered notes
    private ItemClickListener mItemClickListener; // Item click listener

    private FilterListener filterListener; // Filter listener
    private boolean canSelect; // Flag to indicate whether selection is allowed

    // Setter for item click listener
    public void setmItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    // Setter for filter listener
    public void setFilterListener(FilterListener filterListener) {
        this.filterListener = filterListener;
    }

    // Interface for item click listener
    public interface ItemClickListener {
        void onItemClickListener(Note mNote);
        void onLongClickListener(Note mNote);
        void onCheckboxChanged(Note note);
    }

    // Interface for filter listener
    public interface FilterListener {
        void onFilterComplete(List<Note> filteredNotesList);
    }

    // Constructor
    public ListNoteAdapter(List<Note> notes) {
        this.notes = notes;
        this.filteredNotes = notes;
    }

    // Get the list of notes
    public List<Note> getNotesList() {
        return notes;
    }

    // Set whether selection is allowed
    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
    }

    // Add all notes to the list
    public void addAll(List<Note> noteNewData) {
        notes.clear();
        notes.addAll(noteNewData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for each item
        NotesItemBinding binding = NotesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        // Bind data to each item
        Note note = notes.get(position);

        // Show or hide selection controls based on canSelect flag
        holder.binding.containerSelect.setVisibility(canSelect ? View.VISIBLE : View.GONE);
        holder.binding.rdSelect.setVisibility(canSelect ? View.VISIBLE : View.GONE);

        // Set title, content, and time for each note
        holder.binding.tvTitle.setText(note.title);
        holder.binding.tvContent.setText(note.content);
        holder.binding.tvTime.setText(DateUtils.formatDateFromTimeString(note.dateUpdate == 0 ? note.id : note.dateUpdate));

        if (!canSelect) {
            holder.binding.rdSelect.setChecked(false);
        }

        // Checkbox listener
        holder.binding.rdSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            holder.binding.rdSelect.setChecked(isChecked);

            if (mItemClickListener != null)
                mItemClickListener.onCheckboxChanged(note);
        });

        // Item click listener
        holder.binding.llMain.setOnClickListener(view -> {
            if (holder.binding.rdSelect.isShown()) {
                holder.binding.rdSelect.callOnClick();
            } else if (mItemClickListener != null)
                mItemClickListener.onItemClickListener(note);
        });

        // Long click listener
        holder.binding.llMain.setOnLongClickListener(view -> {
            if (mItemClickListener != null)
                mItemClickListener.onLongClickListener(note);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // ViewHolder class
    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        private final NotesItemBinding binding; // View binding object

        // Constructor
        public NotesViewHolder(NotesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Returns a filter to perform search functionality on a list of notes, filtering based on title or content.
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                // Filter the notes based on search query
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    notes = filteredNotes;
                } else {
                    List<Note> filteredList = new ArrayList<>();
                    for (Note note : filteredNotes) {
                        if (note.title.toLowerCase().contains(charString.toLowerCase()) || note.content.contains(charSequence)) {
                            filteredList.add(note);
                        }
                    }
                    notes = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = notes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                // Publish the filtered results
                notes = (ArrayList<Note>) filterResults.values;
                notifyDataSetChanged();

                if (filterListener != null) {
                    filterListener.onFilterComplete(notes);
                }

                Log.d("Notes", "Filtered " + notes.size());
            }
        };
    }
}