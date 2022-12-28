package org.autojs.autojs.external.foreground;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.autojs.autojs.R;
import org.autojs.autojs.ui.main.MainActivity;

public class ForegroundService extends Service {


    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = ForegroundService.class.getName() + ".foreground";

    public static void start(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ForegroundService.class));
        } else {
            context.startService(new Intent(context, ForegroundService.class));
        }
    }

    public static void stop(@NonNull Context context) {
        context.stopService(new Intent(context, ForegroundService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForeground() {
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    @NonNull
    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        int flagImmutable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            flagImmutable = PendingIntent.FLAG_IMMUTABLE;
        } else flagImmutable = 0;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, MainActivity.intent(this).get(), flagImmutable);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.foreground_notification_title))
                .setContentText(getString(R.string.foreground_notification_text))
                .setSmallIcon(R.drawable.autojs_material)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setChannelId(CHANNEL_ID)
                .setVibrate(new long[0])
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        CharSequence name = getString(R.string.foreground_notification_channel_name);
        String description = getString(R.string.foreground_notification_channel_name);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        channel.enableLights(false);
        manager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();

    }
}
