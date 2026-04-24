package fi.junixald.fltoolkit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RestrictionAdapter extends RecyclerView.Adapter<RestrictionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(RestrictionItem item);
    }

    private final List<RestrictionItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public RestrictionAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<RestrictionItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restriction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestrictionItem item = items.get(position);
        holder.tvName.setText(item.getFriendlyName());
        holder.tvKey.setText(item.getKey());

        if (item.isSetByMe()) {
            holder.tvSource.setText(holder.itemView.getContext().getString(R.string.label_source_this_app));
            holder.tvSource.setTextColor(0xFF4CAF50); // green
        } else {
            holder.tvSource.setText(holder.itemView.getContext().getString(R.string.label_source_system));
            holder.tvSource.setTextColor(0xFF9E9E9E); // grey
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
        holder.btnRemove.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvKey, tvSource;
        Button btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestrictionName);
            tvKey = itemView.findViewById(R.id.tvRestrictionKey);
            tvSource = itemView.findViewById(R.id.tvRestrictionSource);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
