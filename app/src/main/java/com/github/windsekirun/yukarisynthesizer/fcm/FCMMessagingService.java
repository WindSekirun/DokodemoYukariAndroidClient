package com.github.windsekirun.yukarisynthesizer.fcm;


import android.text.TextUtils;
import com.github.windsekirun.yukarisynthesizer.notification.NotificationTool;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import pyxis.uzuki.live.nyancat.NyanCat;

import java.util.HashMap;
import java.util.Map;

public class FCMMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().isEmpty()) {
            return;
        }

        NyanCat.tag("FCM").d("Message data payload: %s", remoteMessage.getData().toString());
        String title = remoteMessage.getData().get("title");
        String content = remoteMessage.getData().get("body");
        String extra = remoteMessage.getData().get("extra");

        Map<String, Object> extrasMap = new HashMap<>();

        String imagePath = "";
        if (!TextUtils.isEmpty(imagePath)) {
            NotificationTool.downloadBitmap(imagePath, (bitmap) -> NotificationTool.show(this, content, title, bitmap, extrasMap));
        } else {
            NotificationTool.show(this, content, title, extrasMap);
        }
    }
}