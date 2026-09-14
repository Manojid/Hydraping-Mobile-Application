package com.hydroping.app.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.widget.RemoteViews
import com.hydroping.app.MainActivity
import com.hydroping.app.R
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.CharacterMood

object NotificationHelper {

    const val CHANNEL_ID = "channel_hydroping_reminders"
    
    // Stable notification IDs: ONE active hydration reminder, ONE temporary confirmation
    const val NOTIFICATION_ID_REMINDER = 1001
    const val NOTIFICATION_ID_CONFIRMATION = 1002

    // Backwards compatibility constant
    const val NOTIFICATION_ID = NOTIFICATION_ID_REMINDER

    const val ACTION_QUICK_DRINK = "com.hydroping.app.ACTION_QUICK_DRINK"
    const val ACTION_DRINKING_NOW = "com.hydroping.app.ACTION_DRINKING_NOW"
    const val ACTION_REMIND_LATER = "com.hydroping.app.ACTION_REMIND_LATER"
    const val ACTION_CONFIRM_DONE = "com.hydroping.app.ACTION_CONFIRM_DONE"
    const val ACTION_SKIP_REMINDER = "com.hydroping.app.ACTION_SKIP_REMINDER"

    const val EXTRA_AMOUNT_ML = "extra_amount_ml"
    const val EXTRA_CHARACTER_ID = "extra_character_id"

    // Deterministic Request Codes for PendingIntents
    private const val RC_APP = 0
    private const val RC_DRINK = 1
    private const val RC_LOG = 2
    private const val RC_SNOOZE = 3
    private const val RC_DONE = 4
    private const val RC_SKIP = 5

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showHydrationReminder(
        context: Context,
        characterName: String,
        message: String,
        amountMl: Int,
        userName: String = "Friend",
        consumedMl: Int = 0,
        targetMl: Int = 2000,
        characterId: String = "pikachu"
    ) {
        createNotificationChannel(context)

        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_SHOW_REMINDER", true)
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            RC_APP,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 1: "Drink X ml"
        val drinkIntent = Intent(context, QuickDrinkReceiver::class.java).apply {
            action = ACTION_QUICK_DRINK
            putExtra(EXTRA_AMOUNT_ML, amountMl)
            putExtra(EXTRA_CHARACTER_ID, characterId)
        }
        val drinkPendingIntent = PendingIntent.getBroadcast(
            context,
            RC_DRINK,
            drinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 2: "Log Water"
        val logIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_OPEN_LOG_DIALOG", true)
        }
        val logPendingIntent = PendingIntent.getActivity(
            context,
            RC_LOG,
            logIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 3: "Snooze"
        val snoozeIntent = Intent(context, QuickDrinkReceiver::class.java).apply {
            action = ACTION_REMIND_LATER
            putExtra(EXTRA_CHARACTER_ID, characterId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            RC_SNOOZE,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 4: "Skip"
        val skipIntent = Intent(context, QuickDrinkReceiver::class.java).apply {
            action = ACTION_SKIP_REMINDER
            putExtra(EXTRA_CHARACTER_ID, characterId)
        }
        val skipPendingIntent = PendingIntent.getBroadcast(
            context,
            RC_SKIP,
            skipIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val safeTarget = if (targetMl > 0) targetMl else 2000
        val progressPercent = ((consumedMl.toFloat() / safeTarget.toFloat()) * 100).toInt().coerceIn(0, 100)
        val displayName = if (userName.isNotBlank()) userName else "Friend"
        val resolvedName = CharacterCatalog.fromId(characterId).name

        val avatarRes = CharacterCatalog.getDrawable(
            characterId = characterId,
            mood = if (progressPercent < 70) CharacterMood.THIRSTY else CharacterMood.HYDRATED
        )
        val largeIconBitmap = BitmapFactory.decodeResource(context.resources, avatarRes)

        val cleanMessage = if (message.isNotBlank()) message else "Time to drink $amountMl ml to stay on track!"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(avatarRes)
            .setLargeIcon(largeIconBitmap)
            .setContentTitle("$resolvedName • Hey $displayName 💧")
            .setContentText(cleanMessage)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(cleanMessage)
                    .setSummaryText("Today: $consumedMl / $safeTarget ml ($progressPercent%)")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(appPendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(false)
            .addAction(avatarRes, "Drink $amountMl ml", drinkPendingIntent)
            .addAction(avatarRes, "Log Water", logPendingIntent)
            .addAction(CharacterCatalog.getDrawable(characterId, CharacterMood.SLEEPING), "Snooze", snoozePendingIntent)
            .addAction(CharacterCatalog.getDrawable(characterId, CharacterMood.SLEEPING), "Skip", skipPendingIntent)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REMINDER, builder.build())
        }
    }

    fun showDrinkingInProgressNotification(
        context: Context,
        characterName: String,
        amountMl: Int,
        characterId: String = "pikachu"
    ) {
        val doneIntent = Intent(context, QuickDrinkReceiver::class.java).apply {
            action = ACTION_CONFIRM_DONE
            putExtra(EXTRA_AMOUNT_ML, amountMl)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            RC_DONE,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val avatarRes = CharacterCatalog.getDrawable(characterId, CharacterMood.WAITING)
        val largeIconBitmap = BitmapFactory.decodeResource(context.resources, avatarRes)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(avatarRes)
            .setLargeIcon(largeIconBitmap)
            .setContentTitle("$characterName is waiting...")
            .setContentText("Enjoying your water? Tap Done when finished!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("I'll wait right here... Done? 🚰"))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .addAction(avatarRes, "Done! ($amountMl ml)", donePendingIntent)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REMINDER, builder.build())
        }
    }

    fun showCelebrationNotification(
        context: Context,
        characterName: String,
        amountMl: Int,
        consumedMl: Int = 0,
        targetMl: Int = 0,
        characterId: String = "pikachu"
    ) {
        createNotificationChannel(context)
        val avatarRes = CharacterCatalog.getDrawable(characterId, CharacterMood.HAPPY_CELEBRATING)
        val largeIconBitmap = BitmapFactory.decodeResource(context.resources, avatarRes)

        val detailText = if (targetMl > 0) {
            "Yay! +$amountMl ml logged! Today: $consumedMl / $targetMl ml 🎉"
        } else {
            "Yay! +$amountMl ml logged! Keep up the great streak!"
        }

        val resolvedName = CharacterCatalog.fromId(characterId).name

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(avatarRes)
            .setLargeIcon(largeIconBitmap)
            .setContentTitle("$resolvedName is happy! 🎉")
            .setContentText(detailText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(detailText))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setTimeoutAfter(4000)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CONFIRMATION, builder.build())
        }
    }

    fun showPreviewNotification(
        context: Context,
        animationName: String,
        userName: String,
        amountMl: Int,
        consumedMl: Int = 0,
        targetMl: Int = 2000,
        characterId: String = "pikachu"
    ) {
        val resolvedName = CharacterCatalog.fromId(characterId).name
        showHydrationReminder(
            context = context,
            characterName = resolvedName,
            message = "Time for a crisp hydration break to stay sharp & energized! ($animationName active)",
            amountMl = amountMl,
            userName = userName,
            consumedMl = consumedMl,
            targetMl = targetMl,
            characterId = characterId
        )
    }

    fun cancelReminderNotification(context: Context) {
        val nm = NotificationManagerCompat.from(context)
        nm.cancel(NOTIFICATION_ID_REMINDER)
        nm.cancel(NOTIFICATION_ID_REMINDER + 10) // Clean up any stale legacy preview ID
    }

    fun cancelConfirmationNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_CONFIRMATION)
    }

    fun cancelAllNotifications(context: Context) {
        val nm = NotificationManagerCompat.from(context)
        nm.cancel(NOTIFICATION_ID_REMINDER)
        nm.cancel(NOTIFICATION_ID_CONFIRMATION)
        nm.cancel(NOTIFICATION_ID_REMINDER + 10)
    }

    fun cancelNotification(context: Context) {
        cancelAllNotifications(context)
    }
}
