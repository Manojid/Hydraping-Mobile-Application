package com.hydroping.app

import android.app.Application
import com.hydroping.app.reminder.NotificationHelper

class HydroPingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
