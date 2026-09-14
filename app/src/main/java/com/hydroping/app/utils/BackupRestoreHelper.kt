package com.hydroping.app.utils

import com.hydroping.app.data.DailyGoal
import com.hydroping.app.data.DrinkDao
import com.hydroping.app.data.DrinkLog
import org.json.JSONArray
import org.json.JSONObject

object BackupRestoreHelper {

    suspend fun exportBackupJson(drinkDao: DrinkDao, userName: String, dailyGoalMl: Int): String {
        val drinks = drinkDao.getAllDrinksList()
        val goals = drinkDao.getRecentDailyGoalsList()

        val root = JSONObject()
        root.put("app", "HydraPing")
        root.put("version", "1.0.0")
        root.put("timestamp", System.currentTimeMillis())
        root.put("userName", userName)
        root.put("dailyGoalMl", dailyGoalMl)

        val drinksArray = JSONArray()
        for (d in drinks) {
            val dObj = JSONObject()
            dObj.put("id", d.id)
            dObj.put("timestampMs", d.timestampMs)
            dObj.put("amountMl", d.amountMl)
            dObj.put("source", d.source)
            drinksArray.put(dObj)
        }
        root.put("drinks", drinksArray)

        val goalsArray = JSONArray()
        for (g in goals) {
            val gObj = JSONObject()
            gObj.put("dateString", g.dateString)
            gObj.put("targetMl", g.targetMl)
            gObj.put("totalConsumedMl", g.totalConsumedMl)
            gObj.put("streakCount", g.streakCount)
            goalsArray.put(gObj)
        }
        root.put("dailyGoals", goalsArray)

        return root.toString(2)
    }

    suspend fun restoreBackupJson(jsonString: String, drinkDao: DrinkDao): Result<Int> {
        return try {
            val root = JSONObject(jsonString)
            val drinksArray = root.optJSONArray("drinks") ?: JSONArray()
            val goalsArray = root.optJSONArray("dailyGoals") ?: JSONArray()

            var count = 0
            for (i in 0 until drinksArray.length()) {
                val obj = drinksArray.getJSONObject(i)
                val drink = DrinkLog(
                    id = obj.optLong("id", 0L),
                    timestampMs = obj.getLong("timestampMs"),
                    amountMl = obj.getInt("amountMl"),
                    source = obj.optString("source", "RESTORED")
                )
                drinkDao.insertDrink(drink)
                count++
            }

            for (i in 0 until goalsArray.length()) {
                val obj = goalsArray.getJSONObject(i)
                val goal = DailyGoal(
                    dateString = obj.getString("dateString"),
                    targetMl = obj.getInt("targetMl"),
                    totalConsumedMl = obj.getInt("totalConsumedMl"),
                    streakCount = obj.optInt("streakCount", 1)
                )
                drinkDao.insertOrUpdateDailyGoal(goal)
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
