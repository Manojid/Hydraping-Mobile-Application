package com.hydroping.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink_logs")
data class DrinkLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestampMs: Long = System.currentTimeMillis(),
    val amountMl: Int,
    val source: String // "APP", "WIDGET", "NOTIFICATION", "QS_TILE"
)

@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey
    val dateString: String, // "YYYY-MM-DD"
    val targetMl: Int = 2000,
    val totalConsumedMl: Int = 0,
    val streakCount: Int = 1
)
