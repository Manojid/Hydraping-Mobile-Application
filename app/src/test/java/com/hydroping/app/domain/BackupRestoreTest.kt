package com.hydroping.app.domain

import com.hydroping.app.data.DailyGoal
import com.hydroping.app.data.DrinkDao
import com.hydroping.app.data.DrinkLog
import com.hydroping.app.utils.BackupRestoreHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeDrinkDao : DrinkDao {
    val drinks = mutableListOf<DrinkLog>()
    val goals = mutableListOf<DailyGoal>()

    override suspend fun insertDrink(drink: DrinkLog): Long {
        val id = (drinks.size + 1).toLong()
        drinks.add(drink.copy(id = id))
        return id
    }

    override suspend fun updateDrink(drink: DrinkLog) {
        val index = drinks.indexOfFirst { it.id == drink.id }
        if (index >= 0) drinks[index] = drink
    }

    override suspend fun deleteDrink(drink: DrinkLog) {
        drinks.removeAll { it.id == drink.id }
    }

    override suspend fun deleteDrinkById(id: Long) {
        drinks.removeAll { it.id == id }
    }

    override suspend fun deleteTodayDrinks(startOfDayMs: Long, endOfDayMs: Long) {
        drinks.removeAll { it.timestampMs in startOfDayMs until endOfDayMs }
    }

    override suspend fun clearAllDrinkLogs() {
        drinks.clear()
    }

    override suspend fun clearAllDailyGoals() {
        goals.clear()
    }

    override fun getTodayDrinksFlow(startOfDayMs: Long, endOfDayMs: Long): Flow<List<DrinkLog>> =
        flowOf(drinks.filter { it.timestampMs in startOfDayMs until endOfDayMs })

    override suspend fun getTodayDrinksList(startOfDayMs: Long, endOfDayMs: Long): List<DrinkLog> =
        drinks.filter { it.timestampMs in startOfDayMs until endOfDayMs }

    override fun getDrinksForDateRangeFlow(startMs: Long, endMs: Long): Flow<List<DrinkLog>> =
        flowOf(drinks.filter { it.timestampMs in startMs..endMs })

    override suspend fun getDrinksForDateRangeList(startMs: Long, endMs: Long): List<DrinkLog> =
        drinks.filter { it.timestampMs in startMs..endMs }

    override fun getTodayTotalMlFlow(startOfDayMs: Long, endOfDayMs: Long): Flow<Int> =
        flowOf(drinks.filter { it.timestampMs in startOfDayMs until endOfDayMs }.sumOf { it.amountMl })

    override suspend fun getTodayTotalMl(startOfDayMs: Long, endOfDayMs: Long): Int =
        drinks.filter { it.timestampMs in startOfDayMs until endOfDayMs }.sumOf { it.amountMl }

    override fun getLastDrinkFlow(): Flow<DrinkLog?> =
        flowOf(drinks.lastOrNull())

    override suspend fun getLastDrink(): DrinkLog? =
        drinks.lastOrNull()

    override suspend fun getAllDrinksList(): List<DrinkLog> = drinks

    override fun getRecentHistoryFlow(sinceMs: Long): Flow<List<DrinkLog>> =
        flowOf(drinks.filter { it.timestampMs >= sinceMs })

    override suspend fun getRecentHistoryList(sinceMs: Long): List<DrinkLog> =
        drinks.filter { it.timestampMs >= sinceMs }

    override suspend fun insertOrUpdateDailyGoal(goal: DailyGoal) {
        val index = goals.indexOfFirst { it.dateString == goal.dateString }
        if (index >= 0) goals[index] = goal else goals.add(goal)
    }

    override fun getDailyGoalFlow(dateString: String): Flow<DailyGoal?> =
        flowOf(goals.find { it.dateString == dateString })

    override suspend fun getDailyGoal(dateString: String): DailyGoal? =
        goals.find { it.dateString == dateString }

    override fun getRecentDailyGoalsFlow(): Flow<List<DailyGoal>> =
        flowOf(goals)

    override suspend fun getRecentDailyGoalsList(): List<DailyGoal> = goals

    override fun getTotalLifetimeMlFlow(): Flow<Int> = flowOf(drinks.sumOf { it.amountMl })
    override suspend fun getTotalLifetimeMl(): Int = drinks.sumOf { it.amountMl }
    override fun getTotalDrinksCountFlow(): Flow<Int> = flowOf(drinks.size)
    override fun getMaxSingleDayMlFlow(): Flow<Int> = flowOf(goals.maxOfOrNull { it.totalConsumedMl } ?: 0)
    override fun getMaxStreakFlow(): Flow<Int> = flowOf(goals.maxOfOrNull { it.streakCount } ?: 0)
}

class BackupRestoreTest {

    @Test
    fun `test export and restore backup JSON roundtrip`() = runBlocking {
        val originalDao = FakeDrinkDao()
        originalDao.insertDrink(DrinkLog(amountMl = 350, timestampMs = 1726300000000L, source = "APP"))
        originalDao.insertDrink(DrinkLog(amountMl = 500, timestampMs = 1726304000000L, source = "QUICK_500"))
        originalDao.insertOrUpdateDailyGoal(DailyGoal("2026-09-14", 2000, 850, 4))

        val backupJson = BackupRestoreHelper.exportBackupJson(originalDao, userName = "Alex", dailyGoalMl = 2000)

        assertTrue(backupJson.contains("HydraPing"))
        assertTrue(backupJson.contains("Alex"))
        assertTrue(backupJson.contains("350"))
        assertTrue(backupJson.contains("500"))

        val targetDao = FakeDrinkDao()
        val result = BackupRestoreHelper.restoreBackupJson(backupJson, targetDao)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull())
        assertEquals(2, targetDao.drinks.size)
        assertEquals(1, targetDao.goals.size)
        assertEquals(850, targetDao.goals[0].totalConsumedMl)
        assertEquals(2000, targetDao.goals[0].targetMl)
        assertEquals(4, targetDao.goals[0].streakCount)
    }
}
