package com.mobiplayapps.chessforyou;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class SetupActivity extends AppCompatActivity {

    private static final int REQUEST_DEVICE_ADMIN = 1001;

    private DeviceOwnerManager dom;

    private MaterialCardView cardStep1;
    private Button btnRequestAdmin;
    private ImageView ivStep1Status;

    private Button btnContinue;
    private TextView tvAllDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dom = DeviceOwnerManager.getInstance(this);

        if (dom.isAdminActive()) {
            goToDashboard();
            return;
        }

        setContentView(R.layout.activity_setup);
        bindViews();
        updateStepStates();

        if (!dom.isAdminActive()) {
            requestDeviceAdmin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStepStates();
    }

    private void bindViews() {
        cardStep1     = findViewById(R.id.cardStep1);
        btnRequestAdmin = findViewById(R.id.btnRequestAdmin);
        ivStep1Status = findViewById(R.id.ivStep1Status);

        btnContinue   = findViewById(R.id.btnContinue);
        tvAllDone     = findViewById(R.id.tvAllDone);

        btnRequestAdmin.setOnClickListener(v -> requestDeviceAdmin());
        btnContinue.setOnClickListener(v -> goToDashboard());
    }

    private void updateStepStates() {
        if (dom.isAdminActive()) {
            cardStep1.setStrokeColor(0xFF4CAF50);
            cardStep1.setStrokeWidth(4);
            ivStep1Status.setImageResource(android.R.drawable.presence_online);
            ivStep1Status.setColorFilter(0xFF4CAF50);
            btnRequestAdmin.setText(R.string.status_admin_active);
            btnRequestAdmin.setEnabled(false);
            
            btnContinue.setVisibility(View.VISIBLE);
            tvAllDone.setVisibility(View.VISIBLE);
        } else {
            btnContinue.setVisibility(View.GONE);
            tvAllDone.setVisibility(View.GONE);
        }
    }

    private void requestDeviceAdmin() {
        ComponentName adminComp = new ComponentName(this, AdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComp);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "FL ToolKit requires administrative privileges to enforce system policies.");
        startActivityForResult(intent, REQUEST_DEVICE_ADMIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DEVICE_ADMIN) {
            updateStepStates();
        }
    }

    private void goToDashboard() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
