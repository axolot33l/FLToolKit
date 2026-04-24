package fi.junixald.fltoolkit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ErrorHandler {

    private static final String CHANNEL_ID = "error_notifications";
    private static final int NOTIFICATION_ID = 999;
    public static final String ACTION_COPY_ERROR = "fi.junixald.fltoolkit.ACTION_COPY_ERROR";
    public static final String EXTRA_ERROR_TEXT = "extra_error_text";

    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Error Reports",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for application errors");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            showErrorNotification(context, throwable.toString());
            // Exit the app after a delay to allow notification to be sent
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            System.exit(1);
        });
    }

    public static void showErrorNotification(Context context, String errorText) {
        Intent copyIntent = new Intent(context, ErrorActionReceiver.class);
        copyIntent.setAction(ACTION_COPY_ERROR);
        copyIntent.putExtra(EXTRA_ERROR_TEXT, errorText);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                copyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle("App Error Occurred")
                .setContentText("Click to copy error to clipboard")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(errorText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_send, "Copy Error", pendingIntent);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            // Permission might be missing on Android 13+
        }
    }

    public static class ErrorActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_COPY_ERROR.equals(intent.getAction())) {
                String errorText = intent.getStringExtra(EXTRA_ERROR_TEXT);
                if (errorText != null) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("App Error", errorText);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Error copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
