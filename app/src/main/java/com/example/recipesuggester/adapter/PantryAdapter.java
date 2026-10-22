package com.example.recipesuggester.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

    // from the settings screen
    private boolean expiryAlertsOn;
    private int warningDays;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    // swaps in the latest list from the database (and the current expiry settings) and redraws
    public void setItems(List<PantryItem> newItems, boolean expiryAlertsOn, int warningDays) {
        items.clear();
        items.addAll(newItems);
        this.expiryAlertsOn = expiryAlertsOn;
        this.warningDays = warningDays;
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

        showExpiry(holder, item);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.delete.setOnClickListener(v -> listener.onDeleteClick(item));
        holder.delete.setContentDescription(holder.itemView.getContext()
                .getString(R.string.delete_item, item.getName()));
    }

    // the expiry line on the card. with alerts off it's just grey text with the date.
    // with alerts on, expired items go red and items expiring within warningDays go orange
    private void showExpiry(ViewHolder holder, PantryItem item) {
        Context context = holder.itemView.getContext();
        Integer days = item.daysUntilExpiry();
        if (days == null) {
            holder.expiry.setVisibility(View.GONE); // no date set
            return;
        }
        holder.expiry.setVisibility(View.VISIBLE);

        // reset first because RecyclerView reuses cards, otherwise a reused card
        // could keep the red colour from a different item
        holder.expiry.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        holder.expiry.setTypeface(null, Typeface.NORMAL);
        holder.expiry.setText(context.getString(R.string.pantry_expires, item.getExpiryDate()));

        if (!expiryAlertsOn) {
            return;
        }
        if (days < 0) {
            holder.expiry.setText(context.getString(R.string.pantry_expired, item.getExpiryDate()));
            holder.expiry.setTextColor(ContextCompat.getColor(context, R.color.expired_red));
            holder.expiry.setTypeface(null, Typeface.BOLD);
        } else if (days <= warningDays) {
            String text;
            if (days == 0) {
                text = context.getString(R.string.pantry_expires_today);
            } else if (days == 1) {
                text = context.getString(R.string.pantry_expires_tomorrow);
            } else {
                text = context.getResources().getQuantityString(R.plurals.pantry_expires_in_days, days, days);
            }
            holder.expiry.setText(text);
            holder.expiry.setTextColor(ContextCompat.getColor(context, R.color.expiring_orange));
            holder.expiry.setTypeface(null, Typeface.BOLD);
        }
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
