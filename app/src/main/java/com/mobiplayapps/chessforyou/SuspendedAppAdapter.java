package com.mobiplayapps.chessforyou;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class SuspendedAppAdapter extends RecyclerView.Adapter<SuspendedAppAdapter.ViewHolder> {

    public interface OnUnsuspendClickListener {
        void onUnsuspend(String packageName);
    }

    private List<String> items = new ArrayList<>();
    private final OnUnsuspendClickListener listener;

    public SuspendedAppAdapter(OnUnsuspendClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restriction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String pkg = items.get(position);
        holder.tvName.setText(pkg);
        holder.tvKey.setText(R.string.label_package);
        holder.tvSource.setVisibility(View.GONE);
        holder.btnRemove.setText(R.string.btn_unsuspend);
        holder.btnRemove.setOnClickListener(v -> listener.onUnsuspend(pkg));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvKey;
        TextView tvSource;
        MaterialButton btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestrictionName);
            tvKey = itemView.findViewById(R.id.tvRestrictionKey);
            tvSource = itemView.findViewById(R.id.tvRestrictionSource);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
