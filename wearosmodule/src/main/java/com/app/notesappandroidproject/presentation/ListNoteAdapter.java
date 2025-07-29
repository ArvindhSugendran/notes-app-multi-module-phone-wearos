package com.app.notesappandroidproject.presentation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.notesappandroidproject.databinding.ItemNoteBinding;
import com.app.notesappandroidproject.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ListNoteAdapter extends RecyclerView.Adapter<ListNoteAdapter.NotesViewHolder> implements Filterable {

    private List<Note> notes; // List of notes

    private final List<Note> filteredNotes; // List of filtered notes
    private ItemClickListener mItemClickListener; // Item click listener

    private FilterListener filterListener; // Filter listener

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


    public void updateNotes(List<Note> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for each item
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        // Bind data to each item
        Note note = notes.get(position);

        // Set title, content, and time for each note
        holder.binding.tvTitle.setText(note.getTitle());
        holder.binding.tvContent.setText(note.getDescription());
        holder.binding.tvTime.setText(DateUtils.formatDateFromTimeString(note.getDateUpdate() == 0 ? note.getId() : note.getDateUpdate()));

        // Item click listener
        holder.binding.llMain.setOnClickListener(view -> {
            if (mItemClickListener != null)
                mItemClickListener.onItemClickListener(note);
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // ViewHolder class
    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        private final ItemNoteBinding binding; // View binding object

        // Constructor
        public NotesViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

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
                        if (note.getTitle().toLowerCase().contains(charString.toLowerCase()) || note.getDescription().contains(charSequence)) {
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


