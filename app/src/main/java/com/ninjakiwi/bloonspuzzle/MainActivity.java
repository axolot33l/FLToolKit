package com.ninjakiwi.bloonspuzzle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    private DeviceOwnerManager dom;

    private TextView tvStatus;
    private MaterialCardView cardStatus;

    private MaterialSwitch switchDevOptions;
    private MaterialSwitch switchMultiUser;

    private MaterialButton btnLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dom = DeviceOwnerManager.getInstance(this);

        if (!dom.isAdminActive()) {
            startActivity(new Intent(this, SetupActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        bindViews();
        setupSwitches();
        setupDeviceActions();
        refreshAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAll();
    }

    private void bindViews() {
        tvStatus = findViewById(R.id.tvStatus);
        cardStatus = findViewById(R.id.cardStatus);

        switchDevOptions = findViewById(R.id.switchDevOptions);
        switchMultiUser = findViewById(R.id.switchMultiUser);

        btnLock = findViewById(R.id.btnLock);
    }

    private void setupDeviceActions() {
        btnLock.setOnClickListener(v -> dom.lockNow());
    }

    private void setupSwitches() {
        switchDevOptions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dom.enforceDeveloperOptions(!isChecked);
            Toast.makeText(this, isChecked ? R.string.toast_dev_opts_restored : R.string.toast_dev_opts_enforced, Toast.LENGTH_SHORT).show();
        });

        switchMultiUser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dom.enforceMultiUser(isChecked);
            Toast.makeText(this, isChecked ? R.string.toast_multi_user_restored : R.string.toast_multi_user_enforced, Toast.LENGTH_SHORT).show();
        });
    }

    private void refreshAll() {
        updateStatusCard();
        updateSwitches();
    }

    private void updateStatusCard() {
        if (dom.isDeviceOwner()) {
            tvStatus.setText(R.string.status_do_active_main);
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (dom.isProfileOwner()) {
            tvStatus.setText(R.string.status_po_active_main);
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else if (dom.isAdminActive()) {
            tvStatus.setText(R.string.status_admin_active_main);
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            tvStatus.setText(R.string.status_inactive);
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void updateSwitches() {
        switchDevOptions.setOnCheckedChangeListener(null);
        switchDevOptions.setChecked(dom.isDeveloperOptionsEnabled());
        switchDevOptions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dom.enforceDeveloperOptions(isChecked);
            Toast.makeText(this, isChecked ? R.string.toast_dev_opts_restored : R.string.toast_dev_opts_enforced, Toast.LENGTH_SHORT).show();
        });

        switchMultiUser.setOnCheckedChangeListener(null);
        switchMultiUser.setChecked(dom.isMultiUserEnabled());
        switchMultiUser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dom.enforceMultiUser(isChecked);
            Toast.makeText(this, isChecked ? R.string.toast_multi_user_restored : R.string.toast_multi_user_enforced, Toast.LENGTH_SHORT).show();
        });
    }
}
