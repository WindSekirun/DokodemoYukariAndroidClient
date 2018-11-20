package com.github.windsekirun.yukarisynthesizer.fcm


import android.text.TextUtils
import com.github.windsekirun.yukarisynthesizer.notification.NotificationTool
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pyxis.uzuki.live.nyancat.NyanCat
import java.util.*

class FCMMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage!!.data.isEmpty()) {
            return
        }

        NyanCat.tag("FCM").d("Message data payload: %s", remoteMessage.data.toString())
        val title = remoteMessage.data["title"]
        val content = remoteMessage.data["body"]
        val extra = remoteMessage.data["extra"]

        val extrasMap = HashMap<String, Any>()

        val imagePath = ""
        if (!TextUtils.isEmpty(imagePath)) {
            NotificationTool.downloadBitmap(imagePath) { NotificationTool.show(this, content, title, it, extrasMap) }
        } else {
            NotificationTool.show(this, content, title, extrasMap)
        }
    }
}