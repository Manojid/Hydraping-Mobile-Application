package com.hydroping.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(drink: DrinkLog): Long

    @Update
    suspend fun updateDrink(drink: DrinkLog)

    @Delete
    suspend fun deleteDrink(drink: DrinkLog)

    @Query("DELETE FROM drink_logs WHERE id = :id")
    suspend fun deleteDrinkById(id: Long)

    @Query("DELETE FROM drink_logs WHERE timestampMs >= :startOfDayMs AND timestampMs < :endOfDayMs")
    suspend fun deleteTodayDrinks(startOfDayMs: Long, endOfDayMs: Long)

    @Query("DELETE FROM drink_logs")
    suspend fun clearAllDrinkLogs()

    @Query("DELETE FROM daily_goals")
    suspend fun clearAllDailyGoals()

    @Query("SELECT * FROM drink_logs WHERE timestampMs >= :startOfDayMs AND timestampMs < :endOfDayMs ORDER BY timestampMs DESC")
    fun getTodayDrinksFlow(startOfDayMs: Long, endOfDayMs: Long): Flow<List<DrinkLog>>

    @Query("SELECT * FROM drink_logs WHERE timestampMs >= :startOfDayMs AND timestampMs < :endOfDayMs ORDER BY timestampMs DESC")
    suspend fun getTodayDrinksList(startOfDayMs: Long, endOfDayMs: Long): List<DrinkLog>

    @Query("SELECT * FROM drink_logs WHERE timestampMs >= :startMs AND timestampMs < :endMs ORDER BY timestampMs DESC")
    fun getDrinksForDateRangeFlow(startMs: Long, endMs: Long): Flow<List<DrinkLog>>

    @Query("SELECT * FROM drink_logs WHERE timestampMs >= :startMs AND timestampMs < :endMs ORDER BY timestampMs DESC")
    suspend fun getDrinksForDateRangeList(startMs: Long, endMs: Long): List<DrinkLog>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM drink_logs WHERE timestampMs >= :startOfDayMs AND timestampMs < :endOfDayMs")
    fun getTodayTotalMlFlow(startOfDayMs: Long, endOfDayMs: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM drink_logs WHERE timestampMs >= :startOfDayMs AND timestampMs < :endOfDayMs")
    suspend fun getTodayTotalMl(startOfDayMs: Long, endOfDayMs: Long): Int

    @Query("SELECT * FROM drink_logs ORDER BY timestampMs DESC LIMIT 1")
    fun getLastDrinkFlow(): Flow<DrinkLog?>

    @Query("SELECT * FROM drink_logs ORDER BY timestampMs DESC LIMIT 1")
    suspend fun getLastDrink(): DrinkLog?

    @Query("SELECT * FROM drink_logs ORDER BY timestampMs DESC")
    suspend fun getAllDrinksList(): List<DrinkLog>

    @Query("SELECT * FROM drink_logs WHERE timestampMs >= :sinceMs ORDER BY timestampMs ASC")
    fun getRecentHistoryFlow(sinceMs: Long): Flow<List<DrinkLog>>

    @Query("SELECT * FROM drink_logs WHERE timestampMs >= :sinceMs ORDER BY timestampMs ASC")
    suspend fun getRecentHistoryList(sinceMs: Long): List<DrinkLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyGoal(goal: DailyGoal)

    @Query("SELECT * FROM daily_goals WHERE dateString = :dateString")
    fun getDailyGoalFlow(dateString: String): Flow<DailyGoal?>

    @Query("SELECT * FROM daily_goals WHERE dateString = :dateString")
    suspend fun getDailyGoal(dateString: String): DailyGoal?

    @Query("SELECT * FROM daily_goals ORDER BY dateString DESC LIMIT 90")
    fun getRecentDailyGoalsFlow(): Flow<List<DailyGoal>>

    @Query("SELECT * FROM daily_goals ORDER BY dateString DESC LIMIT 90")
    suspend fun getRecentDailyGoalsList(): List<DailyGoal>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM drink_logs")
    fun getTotalLifetimeMlFlow(): Flow<Int>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM drink_logs")
    suspend fun getTotalLifetimeMl(): Int

    @Query("SELECT COUNT(*) FROM drink_logs")
    fun getTotalDrinksCountFlow(): Flow<Int>

    @Query("SELECT COALESCE(MAX(totalConsumedMl), 0) FROM daily_goals")
    fun getMaxSingleDayMlFlow(): Flow<Int>

    @Query("SELECT COALESCE(MAX(streakCount), 0) FROM daily_goals")
    fun getMaxStreakFlow(): Flow<Int>
}

