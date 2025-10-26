package com.sap.codelab.common.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.sap.codelab.MainActivity
import com.sap.codelab.R
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.presentation.details.MemoDetailsFragmentArgs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val systemNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (systemNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_description)
                setShowBadge(true)
            }
            systemNotificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $CHANNEL_ID")
        } else {
            Log.d(TAG, "Notification channel already exists: $CHANNEL_ID")
        }
    }

    fun showNotification(memo: Memo) {
        val contentText = memo.description.take(MAX_CONTENT_LENGTH)

        val args = MemoDetailsFragmentArgs(memo.id).toBundle()
        val pendingIntent: PendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.nav_memo_details_fragment)
            .setArguments(args)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_location_notification)
            .setContentTitle(memo.title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        systemNotificationManager.notify(memo.id.toInt(), notification)
        Log.d(TAG, "Notification shown for memo ID: ${memo.id}")
    }

    companion object {
        private const val TAG = "NotificationHelper"
        private const val CHANNEL_ID = "memo_reminders_channel"
        private const val MAX_CONTENT_LENGTH = 140
    }
}