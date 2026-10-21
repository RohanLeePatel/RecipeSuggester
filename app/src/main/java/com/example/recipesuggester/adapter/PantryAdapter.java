package com.example.recipesuggester.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesuggester.R;
import com.example.recipesuggester.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

// adapter for the pantry RecyclerView. it turns each PantryItem into a card (item_pantry.xml).
// RecyclerView only makes enough cards to fill the screen and then reuses them while
// scrolling, onBindViewHolder just puts the right item's data into a reused card
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // MainActivity implements this so it can decide what happens on a tap
    public interface Listener {
        void onItemClick(PantryItem item);   // tap the card -> edit it
        void onDeleteClick(PantryItem item); // tap the bin icon -> delete it
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    // swaps in the latest list from the database and redraws
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(item.getQuantityText());

        // only show the expiry line if the item actually has a date
        if (item.getExpiryDate() != null) {
            holder.expiry.setText(holder.itemView.getContext()
                    .getString(R.string.pantry_expires, item.getExpiryDate()));
            holder.expiry.setVisibility(View.VISIBLE);
        } else {
            holder.expiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.delete.setOnClickListener(v -> listener.onDeleteClick(item));
        holder.delete.setContentDescription(holder.itemView.getContext()
                .getString(R.string.delete_item, item.getName()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // holds the views of one card so findViewById only runs once per card, not on every scroll
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final TextView expiry;
        final ImageButton delete;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            quantity = itemView.findViewById(R.id.item_quantity);
            expiry = itemView.findViewById(R.id.item_expiry);
            delete = itemView.findViewById(R.id.item_delete);
        }
    }
}
