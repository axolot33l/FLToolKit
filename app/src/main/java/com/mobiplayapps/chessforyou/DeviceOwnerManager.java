package com.mobiplayapps.chessforyou;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceOwnerManager {

    private static final String TAG = "DeviceOwnerManager";
    private static DeviceOwnerManager instance;

    private final Context context;
    private final DevicePolicyManager dpm;
    private final ComponentName adminComponent;
    private final UserManager userManager;

    private static final List<String> ALL_KNOWN_RESTRICTIONS = Arrays.asList(
            UserManager.DISALLOW_ADD_USER,
            UserManager.DISALLOW_REMOVE_USER,
            UserManager.DISALLOW_DEBUGGING_FEATURES,
            UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES,
            UserManager.DISALLOW_SAFE_BOOT,
            UserManager.DISALLOW_FACTORY_RESET,
            UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA,
            "no_camera",
            "no_microphone",
            "no_screenshot"
    );

    private DeviceOwnerManager(Context context) {
        this.context = context.getApplicationContext();
        this.dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.adminComponent = new ComponentName(context, AdminReceiver.class);
        this.userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
    }

    public static DeviceOwnerManager getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceOwnerManager(context);
        }
        return instance;
    }

    public boolean isAdminActive() {
        return dpm != null && dpm.isAdminActive(adminComponent);
    }

    public boolean isDeviceOwner() {
        return dpm != null && dpm.isDeviceOwnerApp(context.getPackageName());
    }

    public boolean isProfileOwner() {
        return dpm != null && dpm.isProfileOwnerApp(context.getPackageName());
    }

    public void applyAllPolicies() {
        if (!isAdminActive()) return;
        enforceDeveloperOptions(true);
        Log.d(TAG, "Standard admin policies applied");
    }

    public void enforceDeveloperOptions(boolean enable) {
        if (!isAdminActive()) return;
        try {
            if (enable) {
                dpm.clearUserRestriction(adminComponent, UserManager.DISALLOW_DEBUGGING_FEATURES);
            } else {
                dpm.addUserRestriction(adminComponent, UserManager.DISALLOW_DEBUGGING_FEATURES);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Developer options policy requires higher privileges", e);
            ErrorHandler.showErrorNotification(context, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "enforceDeveloperOptions failed", e);
            ErrorHandler.showErrorNotification(context, "Error enforcing Developer Options: " + e.getMessage());
        }
    }

    public boolean isDeveloperOptionsEnabled() {
        try {
            return android.provider.Settings.Global.getInt(
                    context.getContentResolver(), "development_settings_enabled", 0) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public void enforceMultiUser(boolean enable) {
        if (!isAdminActive()) return;
        try {
            if (enable) {
                dpm.clearUserRestriction(adminComponent, UserManager.DISALLOW_ADD_USER);
            } else {
                dpm.addUserRestriction(adminComponent, UserManager.DISALLOW_ADD_USER);
            }
        } catch (Exception e) {
            Log.e(TAG, "enforceMultiUser failed", e);
            ErrorHandler.showErrorNotification(context, "Error enforcing Multi-User: " + e.getMessage());
        }
    }

    public boolean isMultiUserEnabled() {
        if (userManager == null) return false;
        return !userManager.hasUserRestriction(UserManager.DISALLOW_ADD_USER);
    }

    public List<RestrictionItem> getAllActiveRestrictions() {
        List<RestrictionItem> items = new ArrayList<>();
        if (userManager == null) return items;

        Bundle myRestrictions = new Bundle();
        if (isAdminActive()) {
            try {
                myRestrictions = dpm.getUserRestrictions(adminComponent);
            } catch (Exception ignored) {}
        }

        for (String key : ALL_KNOWN_RESTRICTIONS) {
            if (userManager.hasUserRestriction(key)) {
                boolean setByMe = myRestrictions.getBoolean(key, false);
                items.add(new RestrictionItem(key, RestrictionItem.getFriendlyName(key), true, setByMe));
            }
        }
        return items;
    }

    public boolean removeRestriction(String restrictionKey) {
        if (!isAdminActive()) return false;
        try {
            dpm.clearUserRestriction(adminComponent, restrictionKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void addRestriction(String restrictionKey) {
        if (!isAdminActive()) return;
        try {
            dpm.addUserRestriction(adminComponent, restrictionKey);
        } catch (Exception ignored) {}
    }

    public void lockNow() {
        if (!isAdminActive()) return;
        try {
            dpm.lockNow();
        } catch (Exception ignored) {}
    }

    public boolean setPackageSuspended(String packageName, boolean suspended) {
        if (!isAdminActive()) return false;
        try {
            String[] packages = {packageName};
            String[] failed = dpm.setPackagesSuspended(adminComponent, packages, suspended);
            return failed.length == 0;
        } catch (SecurityException e) {
            Log.e(TAG, "setPackageSuspended requires Device Owner or Profile Owner", e);
            ErrorHandler.showErrorNotification(context, "Privilege Error: App suspension requires DO/PO status.");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "setPackageSuspended failed", e);
            ErrorHandler.showErrorNotification(context, "Error suspending package: " + e.getMessage());
            return false;
        }
    }

    public boolean isPackageSuspended(String packageName) {
        if (!isAdminActive()) return false;
        try {
            return dpm.isPackageSuspended(adminComponent, packageName);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean setApplicationHidden(String packageName, boolean hidden) {
        if (!isAdminActive()) return false;
        try {
            return dpm.setApplicationHidden(adminComponent, packageName, hidden);
        } catch (Exception e) {
            Log.e(TAG, "setApplicationHidden failed", e);
            return false;
        }
    }

    public boolean isApplicationHidden(String packageName) {
        if (!isAdminActive()) return false;
        try {
            return dpm.isApplicationHidden(adminComponent, packageName);
        } catch (Exception e) {
            return false;
        }
    }

    public void setScreenCaptureDisabled(boolean disabled) {
        if (!isAdminActive()) return;
        try {
            dpm.setScreenCaptureDisabled(adminComponent, disabled);
        } catch (Exception e) {
            Log.e(TAG, "setScreenCaptureDisabled failed", e);
            ErrorHandler.showErrorNotification(context, "Error disabling screen capture: " + e.getMessage());
        }
    }

    public boolean isScreenCaptureDisabled() {
        if (!isAdminActive()) return false;
        try {
            return dpm.getScreenCaptureDisabled(adminComponent);
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getSuspendedPackages() {
        List<String> suspended = new ArrayList<>();
        if (!isAdminActive()) return suspended;
        try {
            List<android.content.pm.PackageInfo> installedPackages = context.getPackageManager()
                    .getInstalledPackages(0);
            for (android.content.pm.PackageInfo pkg : installedPackages) {
                if (dpm.isPackageSuspended(adminComponent, pkg.packageName)) {
                    suspended.add(pkg.packageName);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getSuspendedPackages failed", e);
        }
        return suspended;
    }

    public List<AppInfo> getInstalledApps() {
        List<AppInfo> apps = new ArrayList<>();
        try {
            android.content.pm.PackageManager pm = context.getPackageManager();
            List<android.content.pm.PackageInfo> packages = pm.getInstalledPackages(0);
            for (android.content.pm.PackageInfo pkg : packages) {
                if (pkg.packageName.equals(context.getPackageName())) continue;
                String label = pkg.applicationInfo.loadLabel(pm).toString();
                android.graphics.drawable.Drawable icon = pkg.applicationInfo.loadIcon(pm);
                apps.add(new AppInfo(label, pkg.packageName, icon));
            }
            // Sort by label
            apps.sort((a, b) -> a.label.compareToIgnoreCase(b.label));
        } catch (Exception e) {
            Log.e(TAG, "getInstalledApps failed", e);
        }
        return apps;
    }

    public static class AppInfo {
        public final String label;
        public final String packageName;
        public final android.graphics.drawable.Drawable icon;

        public AppInfo(String label, String packageName, android.graphics.drawable.Drawable icon) {
            this.label = label;
            this.packageName = packageName;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public List<String> getAllAvailableRestrictionKeys() {
        return ALL_KNOWN_RESTRICTIONS;
    }
}
