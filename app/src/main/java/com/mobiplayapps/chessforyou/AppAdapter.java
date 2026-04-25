package com.mobiplayapps.chessforyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AppAdapter extends ArrayAdapter<DeviceOwnerManager.AppInfo> {

    public AppAdapter(@NonNull Context context, @NonNull List<DeviceOwnerManager.AppInfo> apps) {
        super(context, 0, apps);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_app_selection, parent, false);
        }

        DeviceOwnerManager.AppInfo app = getItem(position);

        ImageView iconView = convertView.findViewById(R.id.ivAppIcon);
        TextView nameView = convertView.findViewById(R.id.tvAppName);
        TextView pkgView = convertView.findViewById(R.id.tvAppPackage);

        if (app != null) {
            iconView.setImageDrawable(app.icon);
            nameView.setText(app.label);
            pkgView.setText(app.packageName);
        }

        return convertView;
    }
}
