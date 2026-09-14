package com.hydroping.app.domain

import java.util.Locale

enum class HydrationUnit(val label: String, val stepMl: Int) {
    ML("ml", 50),
    L("L", 100),
    OZ("oz", 30) // ~1 fl oz is 29.57ml
}

object HydrationUnitHelper {

    private const val ML_PER_OZ = 29.5735f
    private const val ML_PER_L = 1000f

    fun format(amountMl: Int, unit: HydrationUnit): String {
        return when (unit) {
            HydrationUnit.ML -> "$amountMl ml"
            HydrationUnit.L -> String.format(Locale.getDefault(), "%.2f L", amountMl / ML_PER_L)
            HydrationUnit.OZ -> String.format(Locale.getDefault(), "%.1f oz", amountMl / ML_PER_OZ)
        }
    }

    fun formatShort(amountMl: Int, unit: HydrationUnit): String {
        return when (unit) {
            HydrationUnit.ML -> "$amountMl"
            HydrationUnit.L -> String.format(Locale.getDefault(), "%.2f", amountMl / ML_PER_L)
            HydrationUnit.OZ -> String.format(Locale.getDefault(), "%.1f", amountMl / ML_PER_OZ)
        }
    }

    fun toMl(value: Float, unit: HydrationUnit): Int {
        return when (unit) {
            HydrationUnit.ML -> value.toInt()
            HydrationUnit.L -> (value * ML_PER_L).toInt()
            HydrationUnit.OZ -> (value * ML_PER_OZ).toInt()
        }
    }

    fun fromMl(amountMl: Int, unit: HydrationUnit): Float {
        return when (unit) {
            HydrationUnit.ML -> amountMl.toFloat()
            HydrationUnit.L -> amountMl / ML_PER_L
            HydrationUnit.OZ -> amountMl / ML_PER_OZ
        }
    }
}
