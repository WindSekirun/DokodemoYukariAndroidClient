package com.github.windsekirun.yukarisynthesizer.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.github.windsekirun.yukarisynthesizer.R;
import pyxis.uzuki.live.richutilskt.impl.F1;
import pyxis.uzuki.live.richutilskt.utils.RichUtils;

import java.util.Map;

public class NotificationTool {

    public static void show(Context context, String message, Map<String, Object> map) {
        show(context, message, null, context.getString(R.string.app_name), map);
    }

    public static void show(Context context, String message, String title, Map<String, Object> map) {
        show(context, message, null, title, map);
    }

    public static void show(Context context, String message, String title, Bitmap bitmap , Map<String, Object> map) {
        show(context, message, bitmap, title, map);
    }

    /**
     * show notification in FCMMessagingService
     *
     * @param context
     * @param message
     * @param map
     */
    public static void show(Context context, String message, Bitmap bitmap, String title, Map<String, Object> map) {
        String id = "bruza-FCM";
        int requestID = (int) System.currentTimeMillis();
        int colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary);


        NotificationManager notificationManager = RichUtils.getNotificationManager(context);
        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id);

        Intent intent = new Intent(context, BackgroundActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Bundle bundle = new Bundle();
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                RichUtils.put(bundle, entry.getKey(), entry.getValue());
            }
        }

        intent.putExtras(bundle);

        PendingIntent pending = PendingIntent.getActivity(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setContentTitle(title)
                .setContentIntent(pending)
                .setColor(colorPrimary)
                .setColorized(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Uri.parse(Settings.System.DEFAULT_NOTIFICATION_URI.toString()))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setVibrate(new long[]{0, 1000})
                .setAutoCancel(true);

        if (bitmap != null) {
            NotificationCompat.BigPictureStyle bigStyle = new NotificationCompat.BigPictureStyle(builder);
            bigStyle.setBigContentTitle(title);
            bigStyle.setSummaryText(message);
            bigStyle.bigPicture(bitmap);
            bigStyle.bigLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        } else {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle(builder);
            bigTextStyle.setSummaryText(message);
            bigTextStyle.setBigContentTitle(title);
            bigTextStyle.bigText(message);
        }

        notificationManager.notify(10140, builder.build());
    }


    public static void downloadBitmap(String imagePath, F1<Bitmap> callback) {
        RichUtils.runAsync(() -> {
            Bitmap bitmap = RichUtils.downloadBitmap(imagePath);
            callback.invoke(bitmap);
        });
    }
}
