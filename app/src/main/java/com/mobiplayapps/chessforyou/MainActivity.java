package com.mobiplayapps.chessforyou;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1002;
    private DeviceOwnerManager dom;

    private TextView tvStatus;
    private MaterialCardView cardStatus;

    private MaterialSwitch switchDevOptions;
    private MaterialSwitch switchMultiUser;

    private MaterialButton btnLock;
    private MaterialButton btnTestError;

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

        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied. Errors will not be shown.", Toast.LENGTH_LONG).show();
            }
        }
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
        btnTestError = findViewById(R.id.btnTestError);

        btnTestError.setOnClickListener(v -> {
            ErrorHandler.showErrorNotification(this, "Manual Test: This is what a copyable error looks like!");
        });
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
