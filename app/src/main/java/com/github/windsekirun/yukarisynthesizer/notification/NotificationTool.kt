package com.github.windsekirun.yukarisynthesizer.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.github.windsekirun.yukarisynthesizer.R
import pyxis.uzuki.live.richutilskt.utils.downloadBitmap
import pyxis.uzuki.live.richutilskt.utils.notificationManager
import pyxis.uzuki.live.richutilskt.utils.put
import pyxis.uzuki.live.richutilskt.utils.runAsync

object NotificationTool {

    @JvmStatic
    fun show(context: Context, message: String?, map: Map<String, Any>) {
        show(context, message, null, context.getString(R.string.app_name), map)
    }

    @JvmStatic
    fun show(context: Context, message: String?, title: String?, map: Map<String, Any>) {
        show(context, message, null, title, map)
    }

    @JvmStatic
    fun show(context: Context, message: String?, title: String?, bitmap: Bitmap?, map: Map<String, Any>) {
        show(context, message, bitmap, title, map)
    }

    /**
     * show notification in FCMMessagingService
     *
     * @param context
     * @param message
     * @param map
     */
    fun show(context: Context, message: String?, bitmap: Bitmap?, title: String?, map: Map<String, Any>) {
        val id = "Yukari-FCM"
        val requestID = System.currentTimeMillis().toInt()
        val colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary)


        val notificationManager = context.notificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val name = context.getString(R.string.app_name)
            val description = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description
            notificationManager.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(context, id)

        val intent = Intent(context, BackgroundActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val bundle = Bundle()
        if (!map.isEmpty()) {
            for ((key, value) in map) {
                bundle.put(key, value)
            }
        }

        intent.putExtras(bundle)

        val pending = PendingIntent.getActivity(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setContentTitle(title)
                .setContentIntent(pending)
                .setColor(colorPrimary)
                .setColorized(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Uri.parse(Settings.System.DEFAULT_NOTIFICATION_URI.toString()))
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setVibrate(longArrayOf(0, 1000))
                .setAutoCancel(true)

        if (bitmap != null) {
            val bigStyle = NotificationCompat.BigPictureStyle(builder)
            bigStyle.setBigContentTitle(title)
            bigStyle.setSummaryText(message)
            bigStyle.bigPicture(bitmap)
            bigStyle.bigLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        } else {
            val bigTextStyle = NotificationCompat.BigTextStyle(builder)
            bigTextStyle.setSummaryText(message)
            bigTextStyle.setBigContentTitle(title)
            bigTextStyle.bigText(message)
        }

        notificationManager.notify(10140, builder.build())
    }


    @JvmStatic
    fun downloadBitmap(imagePath: String, callback: (Bitmap?) -> Unit) {
        runAsync {
            val bitmap = downloadBitmap(imagePath)
            callback.invoke(bitmap)
        }
    }
}
